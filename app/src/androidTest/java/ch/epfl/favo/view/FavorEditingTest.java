package ch.epfl.favo.view;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.FakeViewModel;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.TestConstants;
import ch.epfl.favo.cache.CacheUtil;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.user.User;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.tabs.addFavor.FavorEditingView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.FAVOR_ID;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class FavorEditingTest {
  private Favor fakeFavor;
  private FakeViewModel fakeViewModel;
  private MockDatabaseWrapper mockDatabaseWrapper = new MockDatabaseWrapper<User>();
  private User testUser =
      new User(
          TestConstants.USER_ID,
          "commit",
          TestConstants.EMAIL,
          TestConstants.DEVICE_ID,
          null,
          null);

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

  @Before
  public void setup() {
    fakeFavor = FakeItemFactory.getFavor();
  }

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
      bundle.putParcelable(CommonTools.FAVOR_VALUE_ARGS, favor);
      bundle.putString(CommonTools.FAVOR_SOURCE, "publishedFavor");
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
    ImageButton cameraButton =
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
    Uri filePath =
        CacheUtil.getInstance()
            .saveToInternalStorage(
                Objects.requireNonNull(currentFragment.getContext()), bm, FAVOR_ID, 0);
    getInstrumentation().waitForIdleSync();
    Bitmap actual =
        CacheUtil.getInstance()
            .loadFromInternalStorage(
                Objects.requireNonNull(currentFragment.getContext()).getFilesDir().getAbsolutePath()
                    + "/"
                    + FAVOR_ID
                    + "/",
                0)
            .get();
    assert (actual.sameAs(bm));
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
    // Favor failedFavor = Mockito.mock(Favor.class);
    // Mockito.doThrow(new RuntimeException()).when(failedFavor).getTitle();
    runOnUiThread(() -> viewModel.setObservedFavorResult(null));
    getInstrumentation().waitForIdleSync();

    // check error message is printed
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.error_database_sync)));
  }

  @Test
  public void testRequestFavorFlow() throws InterruptedException {
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

    onView(withText(R.string.set_location_no))
        .inRoot(isDialog())
        .check(matches(isDisplayed()))
        .perform(click());

    // Check status display is correct
    onView(withId(R.id.toolbar_main_activity))
        .check(matches(isDisplayed()))
        .check(matches(hasDescendant(withText(FavorStatus.REQUESTED.toString()))));
  }

  @Test
  public void testRequestFavorFlowWithLocation() throws InterruptedException {
    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // type the contents and submit
    onView(withId(R.id.title_request_view)).perform(typeText("bla"));
    onView(withId(R.id.details)).perform(typeText("bla..."));
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    onView(withText(R.string.set_location_yes)).inRoot(isDialog()).perform(click());

    Thread.sleep(1000);

    onView(withId(R.id.fragment_map)).check(matches(isDisplayed()));

    // commenting because of problem on cirrus

    //    onView(withText(R.string.done_from_request_view)).perform(click());
    //
    //    Thread.sleep(1000);
    //
    //    // Check status display is correct
    //    onView(withId(R.id.toolbar_main_activity))
    //        .check(matches(hasDescendant(withText(FavorStatus.REQUESTED.toString()))));
  }

  @Test
  public void testAcceptCommittedUser() throws Throwable {
    fakeViewModel = (FakeViewModel) launchFragment(null).getViewModel();
    requestFavor();
    DependencyFactory.setCurrentCollectionWrapper(mockDatabaseWrapper);
    mockDatabaseWrapper.setMockDocument(testUser);
    mockDatabaseWrapper.setMockResult(testUser);

    Favor favor1 = new Favor(fakeViewModel.getObservedFavor().getValue());
    favor1.setAccepterId("one helper");
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(favor1));
    Thread.sleep(1000);

    // choose one committed user, click accept
    /*onData(anything())
        .inAdapterView(withId(R.id.commit_user))
        .atPosition(0)
        .check(matches(isDisplayed()))
        .perform(click());
    Thread.sleep(1000);
    onView(withText(R.string.accept_favor)).check(matches(isDisplayed())).perform(click());

    // check view become accepted
    onView(withId(R.id.commit_complete_button))
        .check(matches(Matchers.allOf(isDisplayed(), withText(R.string.complete_favor))));
    onView(withId(R.id.toolbar_main_activity))
        .check(matches(hasDescendant(withText(FavorStatus.ACCEPTED.toString()))));
    // check accept is gone , and click user profile
    Thread.sleep(1000);
    onView(withText("commit")).check(matches(isDisplayed())).perform(click());
    Thread.sleep(1000);
    onView(withText(R.string.profile)).check(matches(isDisplayed())).perform(click());
    onView(withId(R.id.user_info_fragment)).check(matches(isDisplayed()));*/
  }

  @Test
  public void testCanReuseFavor() throws Throwable {
    fakeViewModel = (FakeViewModel) launchFragment(null).getViewModel();
    requestFavor();

    // click reuse
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    getInstrumentation().waitForIdleSync();
    onView(withText(R.string.reuse_favor)).check(matches(isDisplayed())).perform(click());
    onView(withId(R.id.fragment_favor)).check(matches(isDisplayed()));

    // check fields on request view
    onView(withId(R.id.title_request_view)).check(matches(withText(fakeFavor.getTitle())));
    onView(withId(R.id.details)).check(matches(withText(fakeFavor.getDescription())));
    onView(withId(R.id.favor_reward))
        .check(matches(withText(String.valueOf((int) fakeFavor.getReward()))));
  }

  @Test
  public void testCannotCreateFavorWithoutTitle() throws Throwable {
    fakeViewModel = (FakeViewModel) launchFragment(null).getViewModel();

    onView(withId(R.id.details)).perform(typeText(fakeFavor.getDescription()));
    onView(withId(R.id.favor_reward))
        .perform(typeText(String.valueOf((int) fakeFavor.getReward())));
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());

    onView(withId(R.id.fragment_favor)).check(matches(isDisplayed()));
  }

  @Test
  public void testCannotCreateFavorWithLimitsExceeded() throws Throwable {
    fakeViewModel = (FakeViewModel) launchFragment(null).getViewModel();

    String longString =
        "texttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttexttext";
    onView(withId(R.id.title_request_view)).perform(typeText(longString));
    onView(withId(R.id.details)).perform(typeText(longString));
    onView(withId(R.id.favor_reward))
        .perform(typeText(String.valueOf((int) fakeFavor.getReward())));
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());

    onView(withId(R.id.fragment_favor)).check(matches(isDisplayed()));
  }

  @Test
  public void testCanReportFavor() throws Throwable {
    fakeViewModel = (FakeViewModel) launchFragment(null).getViewModel();
    requestFavor();

    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    getInstrumentation().waitForIdleSync();
    onView(withText(R.string.report_favor_text)).check(matches(isDisplayed())).perform(click());
  }

  @Test
  public void testFavorGotAcceptedDuringEdit() throws Throwable {
    fakeViewModel = (FakeViewModel) launchFragment(null).getViewModel();
    requestFavor();

    // cancel favor
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    getInstrumentation().waitForIdleSync();
    onView(withText(R.string.edit_favor)).check(matches(isDisplayed())).perform(click());

    //  someone commit the old favor
    Favor favor1 = new Favor(fakeViewModel.getObservedFavor().getValue());
    favor1.setAccepterId("one committer");
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(favor1));
    Thread.sleep(1000);
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.old_favor_accepted_by_others)));

    // someone uncommit the old favor
    Favor favor2 = new Favor(fakeViewModel.getObservedFavor().getValue());
    favor2.setAccepterId("");
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(favor2));
    Thread.sleep(1000);
    getInstrumentation().waitForIdleSync();
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.old_favor_cancelled_by_others)));
  }

  @Test
  public void testRestartFavorFlow() throws Throwable {
    launchFragment(null);
    requestFavor();
    // cancel favor
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    getInstrumentation().waitForIdleSync();
    onView(withText(R.string.cancel_request)).check(matches(isDisplayed())).perform(click());

    // restart favor
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    getInstrumentation().waitForIdleSync();
    onView(withText(R.string.restart_request)).check(matches(isDisplayed())).perform(click());
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());
    onView(withText(R.string.set_location_no))
        .inRoot(isDialog())
        .check(matches(isDisplayed()))
        .perform(click());
    getInstrumentation().waitForIdleSync();

    // check favor
    onView(withId(R.id.title)).check(matches(withText(fakeFavor.getTitle())));
    onView(withId(R.id.description)).check(matches(withText(fakeFavor.getDescription())));
    Thread.sleep(1000);
    checkRequestedView();
  }

  @Test
  public void testCancelFavorWithCommitter() throws Throwable {
    fakeViewModel = (FakeViewModel) launchFragment(null).getViewModel();
    requestFavor();
    DependencyFactory.setCurrentCollectionWrapper(mockDatabaseWrapper);
    mockDatabaseWrapper.setMockDocument(testUser);
    mockDatabaseWrapper.setMockResult(testUser);
    Thread.sleep(1000);

    // someone commit the favor
    Favor favor1 = new Favor(fakeViewModel.getObservedFavor().getValue());
    favor1.setAccepterId("one committer");
    favor1.setAccepterId("second committer");
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(favor1));

    // cancel favor
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    Thread.sleep(10000);
    getInstrumentation().waitForIdleSync();
    onView(withText(R.string.cancel_request)).check(matches(isDisplayed())).perform(click());
  }

  @Test
  public void testSnackBarShowsWhenFailPostOrUpdateOrCancelToDb() throws Throwable {
    FavorEditingView favorEditingView = launchFragment(null);
    FakeViewModel fakeViewModel = (FakeViewModel) favorEditingView.getViewModel();
    runOnUiThread(() -> fakeViewModel.setThrowError(new RuntimeException()));
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());

    launchFragment(fakeFavor);
    Thread.sleep(500);
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());
  }

  public void checkRequestedView() {
    // Check edit button is there
    onView(withId(R.id.chat_button)).check(matches(isDisplayed()));
    // Check commit/complete button is not there
    onView(withId(R.id.commit_complete_button))
        .check(matches(allOf(not(isDisplayed()), withText(R.string.commit_favor))));
    // Check status display is correct
    checkToolbar(FavorStatus.REQUESTED.toString());
  }

  public void checkToolbar(String expectedDisplay) {
    onView(withId(R.id.toolbar_main_activity))
        .check(matches(hasDescendant(withText(expectedDisplay))));
  }

  public void requestFavor() {
    // type the contents and request
    onView(withId(R.id.title_request_view)).perform(typeText(fakeFavor.getTitle()));
    onView(withId(R.id.details)).perform(typeText(fakeFavor.getDescription()));
    onView(withId(R.id.favor_reward))
        .perform(typeText(String.valueOf((int) fakeFavor.getReward())));
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());
    onView(withText(R.string.set_location_no))
        .inRoot(isDialog())
        .check(matches(isDisplayed()))
        .perform(click());
    getInstrumentation().waitForIdleSync();
  }
}
