package ch.epfl.favo.view;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.FavorFragmentFactory;
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
          DependencyFactory.setCurrentCollectionWrapper(new MockDatabaseWrapper());
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
    DependencyFactory.setCurrentGpsTracker(null);
    DependencyFactory.setCurrentCollectionWrapper(null);
  }

  @Test
  public void addFavorShowsSnackBar() {
    // Click on fav list tab
    onView(withId(R.id.nav_favor_list_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed())).perform(click());

    getInstrumentation().waitForIdleSync();

    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.favor_request_success_msg)));
  }

  //  @Test
  //  public void cameraPermissionTest() throws Throwable {
  //
  //    launchFragment(new FavorRequestView());
  //    getInstrumentation().waitForIdleSync();
  //    InstrumentationRegistry.getInstrumentation()
  //            .getUiAutomation().revokeRuntimePermission(
  //            InstrumentationRegistry.getInstrumentation().getTargetContext().getPackageName(),
  //            Manifest.permission.CAMERA);
  //    Button cameraButton =
  //            activityTestRule.getActivity().findViewById(R.id.add_camera_picture_button);
  //    runOnUiThread(() -> cameraButton.setEnabled(true));
  //    onView(withId(R.id.add_camera_picture_button)).perform(click());
  //    sleep(1000);
  //    //launches permission intent
  //    // click allow button
  //    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
  //    mDevice.findObject(new UiSelector().textMatches("Allow").enabled(true)).click();
  //  }

  @Test
  public void addPictureWorks() throws Throwable {
    // Click on fav list tab
    FavorRequestView currentFragment = new FavorRequestView();
    launchFragment(currentFragment);
    // inject picture
    Bitmap bm = Bitmap.createBitmap(200, 100, Bitmap.Config.RGB_565);
    Intent intent = new Intent();
    intent.putExtra("data", bm);
    runOnUiThread(() -> currentFragment.onActivityResult(2, RESULT_OK, intent));
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.image_view_request_view)).check(matches(isDisplayed()));
  }

  public void launchFragment(FavorRequestView currentFragment) {
    FragmentTransaction ft =
        activityTestRule.getActivity().getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.nav_host_fragment, currentFragment);
    ft.addToBackStack(null);
    ft.commit();
  }

  @Test
  public void cameraButtonCanBeClicked() throws Throwable {

    FavorRequestView currentFragment = new FavorRequestView();
    launchFragment(currentFragment);
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
  public void testCanHideKeyboardOnClickOutsideOfTextView() {
    FavorRequestView currentFragment = new FavorRequestView();
    launchFragment(currentFragment);
    onView(withId(R.id.title_request_view)).perform(typeText("bla"));
    onView(withId(R.id.request_button)).perform(click());
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.edit_favor_button)).perform(click());
    onView(withId(R.id.title_request_view)).perform(typeText("ble"));

    // click outside of text view
    UiDevice device = UiDevice.getInstance(getInstrumentation());
    device.click(10, device.getDisplayHeight() / 2);
    // check button is visible
    onView(withId(R.id.edit_favor_button)).check(matches(isDisplayed()));
  }

  @Test
  public void loadSavedPicture() throws Throwable {
    // Click on fav list tab
    FavorRequestView currentFragment = new FavorRequestView();
    launchFragment(currentFragment);
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
    launchFragment(currentFragment);
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
        .check(matches(withText(FavorStatus.REQUESTED.toString())));
  }

  @Test
  public void testViewIsCorrectlyUpdatedWhenFavorHasBeenCompleted() throws Throwable {
    Favor fakeFavor = FakeItemFactory.getFavor();
    FavorRequestView fragment = new FavorRequestView();
    fakeFavor.setStatusId(FavorStatus.ACCEPTED.toInt());
    launchFragmentWithFakeFavor(fragment, fakeFavor);
    getInstrumentation().waitForIdleSync();
    checkAcceptedView();
    fakeFavor.setStatusId(FavorStatus.SUCCESSFULLY_COMPLETED.toInt());
    View v = activityTestRule.getActivity().getCurrentFocus();
    runOnUiThread(
        () -> {
          fragment.displayFavorInfo(v);
        });
    getInstrumentation().waitForIdleSync();
    checkCompletedView();
  }

  public void checkCompletedView() {
    onView(withId(R.id.add_camera_picture_button)).check(matches(not(isEnabled())));
    onView(withId(R.id.edit_favor_button)).check(matches(not(isEnabled())));
    onView(withId(R.id.cancel_favor_button)).check(matches(not(isEnabled())));
    onView(withId(R.id.favor_status_text))
        .check(matches(withText(FavorStatus.SUCCESSFULLY_COMPLETED.toString())));
  }

  public void checkAcceptedView() {
    onView(withId(R.id.add_camera_picture_button)).check(matches(not(isEnabled())));
    onView(withId(R.id.add_picture_button)).check(matches(not(isEnabled())));
    onView(withId(R.id.edit_favor_button)).check(matches(not(isEnabled())));
    onView(withId(R.id.cancel_favor_button)).check(matches((isEnabled())));
    onView(withId(R.id.favor_status_text))
        .check(matches(isDisplayed()))
        .check(matches(withText(FavorStatus.ACCEPTED.toString())));
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
        .check(matches(withText(FavorStatus.CANCELLED_REQUESTER.toString())));
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
