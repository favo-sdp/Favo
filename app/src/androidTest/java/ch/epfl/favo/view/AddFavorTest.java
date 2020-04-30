package ch.epfl.favo.view;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
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
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.FakeViewModel;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.tabs.addFavor.FavorRequestView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static ch.epfl.favo.util.FavorFragmentFactory.FAVOR_ARGS;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

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

  public FavorRequestView launchFragment(Favor favor) throws Throwable {
    MainActivity activity = activityTestRule.getActivity();
    NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
    if (favor != null) {
      Bundle bundle = new Bundle();
      bundle.putString(FAVOR_ARGS, favor.getId());
      runOnUiThread(() -> navController.navigate(R.id.action_global_favorRequestView, bundle));
    } else {
      runOnUiThread(() -> navController.navigate(R.id.action_global_favorRequestView));
    }

    Fragment navHostFragment =
        activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
    getInstrumentation().waitForIdleSync();
    return (FavorRequestView) navHostFragment.getChildFragmentManager().getFragments().get(0);
  }

  @Test
  public void addPictureWorks() throws Throwable {
    // Click on fav list tab
    FavorRequestView currentFragment = launchFragment(null);
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
    onView(withId(R.id.edit_favor_button)).check(matches(isDisplayed())).perform(click());
    onView(withId(R.id.title_request_view)).perform(typeText("ble"));

    // click outside of text view
    UiDevice device = UiDevice.getInstance(getInstrumentation());
    device.click(1, device.getDisplayHeight() / 3);
    // check button is visible
    onView(withId(R.id.edit_favor_button)).check(matches(isDisplayed()));
  }

  @Test
  public void loadSavedPicture() throws Throwable {

    FavorRequestView currentFragment = launchFragment(null);
    getInstrumentation().waitForIdleSync();
    // inject picture
    Bitmap bm = Bitmap.createBitmap(200, 100, Bitmap.Config.RGB_565);
    Uri filePath = saveImageToInternalStorage(currentFragment.getContext(), bm);
    Intent intent = new Intent();
    intent.setData(filePath);
    runOnUiThread(() -> currentFragment.onActivityResult(1, RESULT_OK, intent));
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.image_view_request_view)).check(matches(isDisplayed()));
  }

  @Test
  public void snackbarShowsWhenIncorrectResultCodeOnImageUpload() throws Throwable {
    // Click on fav list tab
    FavorRequestView currentFragment = launchFragment(null);
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
    FavorRequestView fragment = launchFragment(fakeFavor);
    // Try to click on edit
    FakeViewModel viewModel = (FakeViewModel) fragment.getViewModel();
    runOnUiThread(() -> viewModel.setObservedFavorResult(null));
    getInstrumentation().waitForIdleSync();

    // check error message is printed
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.error_database_sync)));
  }

  public void checkEditView() {
    // Check upload picture button is  clickable
    //onView(withId(R.id.add_picture_button)).check(matches(isEnabled()));
    //onView(withId(R.id.add_camera_picture_button)).check(matches(isEnabled()));
    // Check edit button text is changed to confirm update
    onView(withId(R.id.edit_favor_button)).check(matches(withText(R.string.confirm_favor_edit)));
    // Check status display is correct
    // Check cancel button is there
    onView(withId(R.id.cancel_favor_button)).check(matches(allOf(isDisplayed(), isEnabled())));
    onView(withId(R.id.favor_status_text))
        .check(matches(isDisplayed()))
        .check(matches(withText(FavorStatus.EDIT.toString())));
  }

  public void checkRequestedView() {
    // Check request button is gone
    onView(withId(R.id.request_button))
        .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
    // Check upload picture button is not clickable
    onView(withId(R.id.add_picture_button)).check(matches(not(isEnabled())));
    onView(withId(R.id.add_camera_picture_button)).check(matches(not(isEnabled())));
    // Check edit button is there
    onView(withId(R.id.edit_favor_button))
        .check(matches(allOf(isDisplayed(), isEnabled(), withText(R.string.edit_favor))));
    // Check cancel button is there
    onView(withId(R.id.cancel_favor_button)).check(matches(allOf(isDisplayed(), isEnabled())));
    // Check status display is correct
    onView(withId(R.id.favor_status_text))
        .check(matches(isDisplayed()))
        .check(matches(withText(FavorStatus.REQUESTED.toString())));
  }

  public void checkCompletedSuccessfullyView() {
    onView(withId(R.id.add_camera_picture_button)).check(matches(not(isEnabled())));
    onView(withId(R.id.add_picture_button)).check(matches(not(isEnabled())));
    onView(withId(R.id.edit_favor_button))
        .check(matches(allOf(isEnabled(), withText(R.string.restart_request))));
    onView(withId(R.id.cancel_favor_button)).check(matches(not(isEnabled())));
    onView(withId(R.id.favor_status_text))
        .check(matches(isDisplayed()))
        .check(matches(withText(FavorStatus.SUCCESSFULLY_COMPLETED.toString())));
  }

  public void checkCompletedOrAcceptedView(FavorStatus status) {
    onView(withId(R.id.add_camera_picture_button)).check(matches(not(isEnabled())));
    onView(withId(R.id.add_picture_button)).check(matches(not(isEnabled())));
    // if completed by accepter, edit button is changed to complete button
    if (status == FavorStatus.COMPLETED_ACCEPTER || status == FavorStatus.ACCEPTED)
      onView(withId(R.id.edit_favor_button))
          .check(matches(allOf(isEnabled(), withText(R.string.complete_favor))));
    else // if completed by requester, edit button is changed to non-clickable waiting button
    onView(withId(R.id.edit_favor_button))
          .check(matches(allOf(not(isEnabled()), withText(R.string.wait_complete))));
    onView(withId(R.id.cancel_favor_button)).check(matches((isEnabled())));
    onView(withId(R.id.favor_status_text))
        .check(matches(isDisplayed()))
        .check(matches(withText(status.toString())));
  }

  public void checkCancelledView(FavorStatus status) {
    // Check upload picture button is not clickable
    onView(withId(R.id.add_picture_button))
        .check(matches(isDisplayed()))
        .check(matches(not(isEnabled())));
    onView(withId(R.id.add_camera_picture_button)).check(matches(not(isEnabled())));
    onView(withId(R.id.edit_favor_button))
        .check(matches(allOf(isEnabled(), withText(R.string.restart_request))));
    // Check cancel button is not clickable
    onView(withId(R.id.cancel_favor_button)).check(matches(not(isEnabled())));
    // Check updated status string
    onView(withId(R.id.favor_status_text))
        .check(matches(isDisplayed()))
        .check(matches(withText(status.toString())));
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
  public void testEditFavorFlow() throws Throwable {
    launchFragment(fakeFavor); // requested status
    // Click on edit, check contains right text
    onView(withId(R.id.edit_favor_button))
        .check(matches(allOf(isDisplayed(), withText(R.string.edit_favor))))
        .perform(click());
    Thread.sleep(1000);
    checkEditView();

    // click on edit(confirm) again
    onView(withId(R.id.edit_favor_button)).perform(click());
    getInstrumentation().waitForIdleSync();

    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.favor_edit_success_msg)));
    checkRequestedView();
  }

  @Test
  public void testCompleteFlowByRequester() throws Throwable {
    Favor fakeFavor = FakeItemFactory.getFavor();
    FavorRequestView favorRequestView = launchFragment(fakeFavor);
    FakeViewModel fakeViewModel = (FakeViewModel) favorRequestView.getViewModel();

    // when favor is firstly completed by requester
    fakeFavor.setStatusIdToInt(FavorStatus.ACCEPTED);
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(fakeFavor));
    getInstrumentation().waitForIdleSync();
    checkCompletedOrAcceptedView(FavorStatus.ACCEPTED);
    // click on complete button
    onView(withId(R.id.edit_favor_button)).perform(click());
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.favor_complete_success_msg)));
    checkCompletedOrAcceptedView(FavorStatus.COMPLETED_REQUESTER);
    Thread.sleep(2000);

    // If favor is firstly completed by accepter, then click complete button
    fakeFavor.setStatusIdToInt(FavorStatus.COMPLETED_ACCEPTER);
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(fakeFavor));
    getInstrumentation().waitForIdleSync();
    checkCompletedOrAcceptedView(FavorStatus.COMPLETED_ACCEPTER);
    onView(withId(R.id.edit_favor_button)).perform(click());
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.favor_complete_success_msg)));
    checkCompletedSuccessfullyView();
  }

  @Test
  public void testCancelFlowByRequester() throws Throwable {
    Favor fakeFavor = FakeItemFactory.getFavor();
    FavorRequestView favorRequestView = launchFragment(fakeFavor); // requested status
    FakeViewModel fakeViewModel = (FakeViewModel) favorRequestView.getViewModel();

    // Click on cancel, cancelled by requester
    onView(withId(R.id.cancel_favor_button))
        .check(matches(isDisplayed()))
        .check(matches(withText(R.string.cancel_request))) // check contains right text
        .perform(click());
    fakeFavor.setStatusIdToInt(FavorStatus.CANCELLED_REQUESTER);
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(fakeFavor));
    getInstrumentation().waitForIdleSync();
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.favor_cancel_success_msg)));
    checkCancelledView(FavorStatus.CANCELLED_REQUESTER);

    // If firstly Cancelled by accepter, show correct view
    fakeFavor.setStatusIdToInt(FavorStatus.CANCELLED_ACCEPTER);
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(fakeFavor));
    getInstrumentation().waitForIdleSync();
    checkCancelledView(FavorStatus.CANCELLED_ACCEPTER);
  }

  @Test
  public void testSnackBarShowsWhenFailPostOrUpdateOrCancelToDb() throws Throwable {
    FavorRequestView favorRequestView = launchFragment(null);
    FakeViewModel fakeViewModel = (FakeViewModel) favorRequestView.getViewModel();
    runOnUiThread(() -> fakeViewModel.setThrowError(true));
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.update_favor_error)));

    launchFragment(fakeFavor);
    Thread.sleep(500);
    onView(withId(R.id.edit_favor_button)).check(matches(isDisplayed())).perform(click());
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.update_favor_error)));

    onView(withId(R.id.cancel_favor_button)).check(matches(isDisplayed())).perform(click());
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.update_favor_error)));
  }

  @Test
  public void testSnackBarShowsWhenFailCompleteToDb() throws Throwable {
    Favor fakeFavor = FakeItemFactory.getFavor();
    FavorRequestView favorRequestView = launchFragment(fakeFavor);
    FakeViewModel fakeViewModel = (FakeViewModel) favorRequestView.getViewModel();

    // when favor is firstly completed by requester
    fakeFavor.setStatusIdToInt(FavorStatus.ACCEPTED);
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(fakeFavor));
    runOnUiThread(() -> fakeViewModel.setThrowError(true));
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.edit_favor_button))
        .check(matches(allOf(isDisplayed(), withText(R.string.complete_favor))))
        .perform(click());
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.update_favor_error)));

    // when favor is firstly completed by acceptor
    fakeFavor.setStatusIdToInt(FavorStatus.COMPLETED_ACCEPTER);
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(fakeFavor));
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.edit_favor_button))
        .check(matches(allOf(isDisplayed(), withText(R.string.complete_favor))))
        .perform(click());
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.update_favor_error)));
  }

  public static Uri saveImageToInternalStorage(Context mContext, Bitmap bitmap) {

    String mImageName = "snap.jpg";

    ContextWrapper wrapper = new ContextWrapper(mContext);

    File file = wrapper.getDir("Images", MODE_PRIVATE);

    file = new File(file, mImageName);

    try {

      OutputStream stream;

      stream = new FileOutputStream(file);

      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

      stream.flush();

      stream.close();

    } catch (IOException e) {
      e.printStackTrace();
    }

    Uri mImageUri = Uri.parse(file.getAbsolutePath());

    return mImageUri;
  }
}
