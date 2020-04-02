package ch.epfl.favo.view;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

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
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.FavorFragmentFactory;
import ch.epfl.favo.view.tabs.addFavor.FavorRequestView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class AddFavorTest {

  @Rule
  public final ActivityTestRule<MainActivity> activityTestRule =
      new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
          DependencyFactory.setCurrentFirebaseUser(
              new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
          DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
          DependencyFactory.setCurrentDatabaseUpdater(new MockDatabaseWrapper());
        }
      };

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

  @After
  public void tearDown() {
    DependencyFactory.setCurrentFirebaseUser(null);
    DependencyFactory.setCurrentGpsTracker(null);
    DependencyFactory.setCurrentDatabaseUpdater(null);
  }

  @Test
  public void addFavorShowsSnackBar() {
    // Click on fav list tab
    onView(withId(R.id.nav_favor_list_button)).check(matches(isDisplayed())).perform(click());

    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.new_favor)).check(matches(isDisplayed())).perform(click());

    getInstrumentation().waitForIdleSync();

    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.favor_request_success_msg)));
  }

  @Test
  public void addPictureWorks() throws Throwable {
    // Click on fav list tab
    FavorRequestView currentFragment = new FavorRequestView();
    FragmentTransaction ft =
        activityTestRule.getActivity().getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.nav_host_fragment, currentFragment);
    ft.addToBackStack(null);
    ft.commit();
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
    FavorRequestView currentFragment = new FavorRequestView();
    FragmentTransaction ft =
        activityTestRule.getActivity().getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.nav_host_fragment, currentFragment);
    ft.addToBackStack(null);
    ft.commit();
    getInstrumentation().waitForIdleSync();
    Button cameraButton =
        activityTestRule.getActivity().findViewById(R.id.add_camera_picture_button);
    runOnUiThread(() -> cameraButton.setEnabled(true));
    Intent fakeIntent = new Intent();
    DependencyFactory.setCurrentCameraIntent(fakeIntent);
    onView(withId(R.id.add_camera_picture_button)).check(matches(isEnabled())).perform(click());
    // nothing should happen
    getInstrumentation().waitForIdleSync();
  }

  @Test
  public void loadSavedPicture() throws Throwable {
    // Click on fav list tab
    FavorRequestView currentFragment = new FavorRequestView();
    FragmentTransaction ft =
        activityTestRule.getActivity().getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.nav_host_fragment, currentFragment);
    ft.addToBackStack(null);
    ft.commit();
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
    FavorRequestView currentFragment = new FavorRequestView();
    FragmentTransaction ft =
        activityTestRule.getActivity().getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.nav_host_fragment, currentFragment);
    ft.addToBackStack(null);
    ft.commit();
    getInstrumentation().waitForIdleSync();
    Intent intent = new Intent();
    runOnUiThread(() -> currentFragment.onActivityResult(1, RESULT_CANCELED, intent));
    getInstrumentation().waitForIdleSync();
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.error_msg_image_request_view)));
  }

  @Test
  public void requestedFavorViewIsUpdatedCorrectly() {
    Favor fakeFavor = FakeItemFactory.getFavor();
    launchFragmentWithFakeFavor(new FavorRequestView(), fakeFavor);
    getInstrumentation().waitForIdleSync();
    // Check request button is gone
    onView(withId(R.id.request_button))
        .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

    // Check upload picture button is not clickable
    onView(withId(R.id.add_picture_button)).check(matches(not(isEnabled())));

    // Check status display is correct
    onView(withId(R.id.favor_status_text))
        .check(matches(isDisplayed()))
        .check(matches(withText(fakeFavor.getStatusId().getPrettyString())));
  }

  @Test
  public void testViewIsCorrectlyUpdatedWhenFavorHasBeenCompleted() throws Throwable {
    Favor fakeFavor = FakeItemFactory.getFavor();
    FavorRequestView fragment = new FavorRequestView();
    fakeFavor.updateStatus(Favor.Status.ACCEPTED);
    launchFragmentWithFakeFavor(fragment, fakeFavor);
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.add_camera_picture_button)).check(matches(not(isEnabled())));
    onView(withId(R.id.add_picture_button)).check(matches(not(isEnabled())));
    onView(withId(R.id.edit_favor_button)).check(matches(not(isEnabled())));
    onView(withId(R.id.cancel_favor_button)).check(matches((isEnabled())));
    onView(withId(R.id.favor_status_text))
        .check(matches(isDisplayed()))
        .check(matches(withText(fakeFavor.getStatusId().getPrettyString())));
    fakeFavor.updateStatus(Favor.Status.SUCCESSFULLY_COMPLETED);
    runOnUiThread(
        new Runnable() {
          @Override
          public void run() {
            fragment.displayFavorInfo();
          }
        });
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.add_camera_picture_button)).check(matches(not(isEnabled())));
    onView(withId(R.id.edit_favor_button)).check(matches(not(isEnabled())));
    onView(withId(R.id.cancel_favor_button)).check(matches(not(isEnabled())));
    onView(withId(R.id.favor_status_text))
        .check(matches(withText(fakeFavor.getStatusId().getPrettyString())));
  }

  @Test
  public void testEditFavorFlow() {
    Favor fakeFavor = FakeItemFactory.getFavor();
    launchFragmentWithFakeFavor(new FavorRequestView(), fakeFavor);
    getInstrumentation().waitForIdleSync();
    // Click on edit
    onView(withId(R.id.edit_favor_button))
        .check(matches(isDisplayed()))
        .check(matches(withText(R.string.edit_favor))) // check contains right text
        .perform(click());
    getInstrumentation().waitForIdleSync();
    // Check edit button text is changed to confirm update
    onView(withId(R.id.edit_favor_button)).check(matches(withText(R.string.confirm_favor_edit)));

    // click on edit again
    onView(withId(R.id.edit_favor_button)).perform(click());
    getInstrumentation().waitForIdleSync();

    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.favor_edit_success_msg)));
  }

  @Test
  public void cancelActiveFavorUpdatesViewCorrectly() {
    Favor fakeFavor = FakeItemFactory.getFavor();
    launchFragmentWithFakeFavor(new FavorRequestView(), fakeFavor);
    getInstrumentation().waitForIdleSync();

    // Click on cancel
    onView(withId(R.id.cancel_favor_button))
        .check(matches(isDisplayed()))
        .check(matches(withText(R.string.cancel_request))) // check contains right text
        .perform(click());
    getInstrumentation().waitForIdleSync();
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.favor_cancel_success_msg)));

    // Check upload picture button is not clickable
    onView(withId(R.id.add_picture_button))
        .check(matches(isDisplayed()))
        .check(matches(not(isEnabled())));

    // Check cancel button is not clickable
    onView(withId(R.id.cancel_favor_button)).check(matches(not(isEnabled())));

    // Check updated status string
    onView(withId(R.id.favor_status_text))
        .check(matches(withText(Favor.Status.CANCELLED_REQUESTER.getPrettyString())));
  }

  private void launchFragmentWithFakeFavor(Fragment fragment, Favor favor) {
    // Launch view
    activityTestRule.getActivity().activeFavors.put(favor.getId(), favor);
    FragmentTransaction ft =
        activityTestRule.getActivity().getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.nav_host_fragment, FavorFragmentFactory.instantiate(favor, fragment));
    ft.addToBackStack(null);
    ft.commit();
  }

  public static Uri saveImageToInternalStorage(Context mContext, Bitmap bitmap) {

    String mImageName = "snap.jpg";

    ContextWrapper wrapper = new ContextWrapper(mContext);

    File file = wrapper.getDir("Images", MODE_PRIVATE);

    file = new File(file, mImageName);

    try {

      OutputStream stream = null;

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
