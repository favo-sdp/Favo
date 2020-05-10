package ch.epfl.favo.view;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;

import org.hamcrest.core.AllOf;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Objects;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.FakeViewModel;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.cache.CacheUtil;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.tabs.addFavor.FavorEditingView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.FAVOR_ID;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
/*
@RunWith(AndroidJUnit4.class)
public class AddFavorTest {
  private Favor fakeFavor = FakeItemFactory.getFavor();

  @Rule
  public final ActivityTestRule<MainActivity> activityTestRule =
      new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
          DependencyFactory.setCurrentFirebaseUser(
              new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
          DependencyFactory.setCurrentViewModelClass(FakeViewModel.class);
        }
      };

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.CAMERA);

  @Rule
  public GrantPermissionRule permissionRule2 =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

  @After
  public void tearDown() {
    DependencyFactory.setCurrentFirebaseUser(null);
    DependencyFactory.setCurrentViewModelClass(null);
  }

  public FavorEditingView launchFragment(Favor favor) throws Throwable {
    MainActivity activity = activityTestRule.getActivity();
    NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
    if (favor != null) {
      Bundle bundle = new Bundle();
      bundle.putString(CommonTools.FAVOR_ARGS, favor.getId());
      runOnUiThread(() -> navController.navigate(R.id.action_global_favorEditingView, bundle));
    } else {
      runOnUiThread(() -> navController.navigate(R.id.action_global_favorEditingView));
    }

    Fragment navHostFragment =
        activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
    getInstrumentation().waitForIdleSync();
    return (FavorEditingView) navHostFragment.getChildFragmentManager().getFragments().get(0);
  }

  @Test
  public void testChatAndLocationButtonWorkRequestView() throws Throwable {
    // Check and click on the chat
    launchFragment(fakeFavor);

    // Go back to favor detail page
    Thread.sleep(2000);

    // Check and click on the location button
    onView(withId(R.id.location_request_view_btn)).check(matches(isDisplayed())).perform(click());
    onView(withId(R.id.fragment_map)).check(matches(isDisplayed()));
  }

  @Test
  public void addPictureWorks() throws Throwable {
    // Click on fav list tab
    FavorEditingView currentFragment = launchFragment(null);
    // inject picture
    Bitmap bm = Bitmap.createBitmap(200, 100, Bitmap.Config.RGB_565);
    Intent intent = new Intent();
    intent.putExtra("data", bm);
    runOnUiThread(() -> currentFragment.onActivityResult(2, RESULT_OK, intent));

    getInstrumentation().waitForIdleSync();

    onView(withId(R.id.image_view_request_view)).check(matches(isDisplayed()));
  }

  @Test
  public void cameraButtonCanBeClicked() throws Throwable {

    launchFragment(null);
    getInstrumentation().waitForIdleSync();
    Button cameraButton =
        activityTestRule.getActivity().findViewById(R.id.add_camera_picture_button);
    runOnUiThread(() -> cameraButton.setEnabled(true));

    Intent fakeIntent = new Intent();
    DependencyFactory.setCurrentCameraIntent(fakeIntent);

    getInstrumentation().waitForIdleSync();
    // click on button
    onView(withId(R.id.add_camera_picture_button)).check(matches(isEnabled())).perform(click());
  }

  @Test
  public void testCanHideKeyboardOnClickOutsideOfTextView() throws Throwable {
    launchFragment(fakeFavor);
    onView(withId(R.id.title_request_view)).perform(typeText("ble"));

    // click outside of text view
    UiDevice device = UiDevice.getInstance(getInstrumentation());
    device.click(1, device.getDisplayHeight() / 3);
  }

  @Test
  public void loadSavedPicture() throws Throwable {

    FavorEditingView currentFragment = launchFragment(null);
    getInstrumentation().waitForIdleSync();
    // inject picture
    Bitmap bm = Bitmap.createBitmap(200, 100, Bitmap.Config.RGB_565);
    Uri filePath = CacheUtil.getInstance().saveToInternalStorage(
            Objects.requireNonNull(currentFragment.getContext()), bm, FAVOR_ID, 0);
    getInstrumentation().waitForIdleSync();
    Bitmap actual = CacheUtil.getInstance().loadFromInternalStorage(
            Objects.requireNonNull(currentFragment.getContext())
                    .getFilesDir().getAbsolutePath() + "/" + FAVOR_ID + "/", 0).get();
    assert(actual.sameAs(bm));
    Intent intent = new Intent();
    intent.setData(filePath);
    runOnUiThread(() -> currentFragment.onActivityResult(1, RESULT_OK, intent));
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.image_view_request_view)).check(matches(isDisplayed()));
  }

  @Test
  public void snackbarShowsWhenIncorrectResultCodeOnImageUpload() throws Throwable {
    // Click on fav list tab
    FavorEditingView currentFragment = launchFragment(null);
    getInstrumentation().waitForIdleSync();
    Intent intent = new Intent();
    runOnUiThread(() -> currentFragment.onActivityResult(1, RESULT_CANCELED, intent));
    getInstrumentation().waitForIdleSync();
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.error_msg_image_request_view)));
  }

  @Test
  public void testSnackbarShowsWhenFavorCannotBeFetchedFromDatabase() throws Throwable {
    // make the collection wrapper throw an error
    // instantiate view
    FavorEditingView fragment = launchFragment(fakeFavor);
    // Try to click on edit
    FakeViewModel viewModel = (FakeViewModel) fragment.getViewModel();
    Favor failedFavor = Mockito.mock(Favor.class);
    Mockito.doThrow(new RuntimeException()).when(failedFavor).getTitle();
    runOnUiThread(() -> viewModel.setObservedFavorResult(failedFavor));
    getInstrumentation().waitForIdleSync();

    // check error message is printed
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.error_database_sync)));
  }

  public void checkRequestedView() {
    // Check fragment_favor_published_view button is gone
    onView(withId(R.id.request_button))
        .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
    // Check upload picture button is not clickable
    onView(withId(R.id.add_picture_button)).check(matches(not(isEnabled())));
    onView(withId(R.id.add_camera_picture_button)).check(matches(not(isEnabled())));
    // Check status display is correct
    onView(withId(R.id.toolbar_main_activity))
        .check(matches(isDisplayed()))
        .check(matches(hasDescendant(withText(FavorStatus.REQUESTED.toString()))));
  }

  @Test
  public void testRequestFavorFlow() {
    // Click on fav list tab
    onView(withId(R.id.nav_favorList)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // type the contents and submit
    onView(withId(R.id.title_request_view)).perform(typeText("bla"));
    onView(withId(R.id.details)).perform(typeText("bla..."));
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.favor_request_success_msg)));
    checkRequestedView();
  }

  @Test

  public void testSnackBarShowsWhenFailPostOrUpdateOrCancelToDb() throws Throwable {
    FavorEditingView favorEditingView = launchFragment(null);
    FakeViewModel fakeViewModel = (FakeViewModel) favorEditingView.getViewModel();
    runOnUiThread(() -> fakeViewModel.setThrowError(new RuntimeException()));
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.update_favor_error)));

    launchFragment(fakeFavor);
    Thread.sleep(500);
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.update_favor_error)));
  }
}
*/