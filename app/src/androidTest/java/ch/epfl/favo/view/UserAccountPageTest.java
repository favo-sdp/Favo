package ch.epfl.favo.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.FakePictureUtil;
import ch.epfl.favo.FakeUserUtil;
import ch.epfl.favo.FakeViewModel;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.TestConstants;
import ch.epfl.favo.auth.SignInActivity;
import ch.epfl.favo.cache.CacheUtil;
import ch.epfl.favo.user.User;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.tabs.UserAccountPage;
import ch.epfl.favo.view.tabs.addFavor.FavorEditingView;

import static android.app.Activity.RESULT_OK;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROFILE_PICTURE_ID;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringEndsWith.endsWith;

@RunWith(AndroidJUnit4.class)
public class UserAccountPageTest {

  @Rule
  public final ActivityTestRule<SignInActivity> mActivityRule =
      new ActivityTestRule<>(SignInActivity.class, true, false);

  @Rule
  public final ActivityTestRule<MainActivity> mainActivityTestRule =
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
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

  public void navigateToAccountTab() {

    // direct to the account tab
    onView(withId(R.id.hamburger_menu_button)).perform(click());
    getInstrumentation().waitForIdleSync();
    // Click on account icon
    onView(withId(R.id.nav_account)).perform(click());
    getInstrumentation().waitForIdleSync();
  }

  @Before
  public void setUp() {
    User testUser =
      new User(
        TestConstants.USER_ID,
        TestConstants.NAME,
        TestConstants.EMAIL,
        TestConstants.DEVICE_ID,
        null,
        null);

    FakeUserUtil userUtil = new FakeUserUtil();
    userUtil.setFindUserResult(testUser);
    DependencyFactory.setCurrentUserRepository(userUtil);
    DependencyFactory.setCurrentViewModelClass(FakeViewModel.class);
    FakePictureUtil fakePictureUtil = new FakePictureUtil();
    DependencyFactory.setCurrentPictureUtility(fakePictureUtil);
  }

  @After
  public void tearDown() {
    DependencyFactory.setCurrentFirebaseUser(null);
    DependencyFactory.setCurrentGpsTracker(null);
    DependencyFactory.setCurrentUserRepository(null);
    DependencyFactory.setCurrentViewModelClass(null);
    DependencyFactory.setCurrentPictureUtility(null);
  }

  @Test
  public void testUserNotLoggedIn() {
    DependencyFactory.setCurrentFirebaseUser(null);
    mActivityRule.launchActivity(null);
    // UI controlled by the Firebase UI library, view checks cannot be done properly
  }

  @Test
  public void testUserAlreadyLoggedIn_displayUserData() {

    // set mock user
    DependencyFactory.setCurrentFirebaseUser(
        new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
    DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());

    mActivityRule.launchActivity(null);

    navigateToAccountTab();

    onView(withId(R.id.user_name)).check(matches(withText(NAME)));
    onView(withId(R.id.user_email)).check(matches(withText(EMAIL)));
  }

  @Test
  public void testUserAlreadyLoggedIn_displayUserData_missingName() {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(null, EMAIL, PHOTO_URI, PROVIDER));
    DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());

    mActivityRule.launchActivity(null);
    navigateToAccountTab();
    onView(withId(R.id.user_name)).check(matches(withText("Test Testerson")));
  }

  @Test
  public void testUserAlreadyLoggedIn_displayUserData_missingEmail() {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, null, PHOTO_URI, PROVIDER));
    DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());

    mActivityRule.launchActivity(null);
    navigateToAccountTab();
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.user_email)).check(matches(withText("test@example.com")));
  }

  @Test
  public void testUserAlreadyLoggedIn_displayUserData_missingPhoto() {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, null, PROVIDER));
    DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
    mActivityRule.launchActivity(null);
    navigateToAccountTab();
    onView(withId(R.id.user_name)).check(matches(withText(NAME)));
    onView(withId(R.id.user_email)).check(matches(withText(EMAIL)));
  }

  @Test
  public void testUserAlreadyLoggedIn_signOut() {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, null, PROVIDER));
    DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
    mActivityRule.launchActivity(null);
    navigateToAccountTab();
    DependencyFactory.setCurrentFirebaseUser(null);
    onView(withId(R.id.sign_out)).perform(click());
  }

  @Test
  public void testUserAlreadyLoggedIn_deleteAccount_alertShowed_cancelOperation() {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, null, PROVIDER));
    DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
    mActivityRule.launchActivity(null);
    navigateToAccountTab();
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.delete_account)).perform(ViewActions.scrollTo()).perform(click());
    // give time to display the dialog
    getInstrumentation().waitForIdleSync();
    onView(withText(endsWith("?"))).check(matches(isDisplayed()));
    onView(withId(android.R.id.button2)).inRoot(isDialog()).check(matches(isDisplayed()));
    onView(withId(android.R.id.button1)).inRoot(isDialog()).check(matches(isDisplayed()));
    onView(withId(android.R.id.button2)).perform(click());
    onView(withId(R.id.delete_account)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()));
  }

  @Test
  public void testUserAlreadyLoggedIn_deleteAccount_alertShowed_confirmOperation() {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, null, PROVIDER));
    DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
    mActivityRule.launchActivity(null);
    navigateToAccountTab();
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.delete_account)).perform(ViewActions.scrollTo()).perform(click());
    // give time to display the dialog
    getInstrumentation().waitForIdleSync();
    onView(withText(endsWith("?"))).check(matches(isDisplayed()));
    onView(withId(android.R.id.button2)).inRoot(isDialog()).check(matches(isDisplayed()));
    onView(withId(android.R.id.button1)).inRoot(isDialog()).check(matches(isDisplayed()));
    DependencyFactory.setCurrentFirebaseUser(null);
    onView(withId(android.R.id.button1)).perform(click());
    onView(withId(R.id.delete_account)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()));
  }

  @Test
  public void testUserAlreadyLoggedIn_editProfile_alertShowed_confirmOperation() {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, null, PROVIDER));
    DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
    mActivityRule.launchActivity(null);
    navigateToAccountTab();
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.edit_profile)).perform(ViewActions.scrollTo()).perform(click());
    // give time to display the dialog
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.change_name_dialog_user_input)).check(matches(isDisplayed()));
    onView(withId(android.R.id.button2)).inRoot(isDialog()).check(matches(isDisplayed()));
    onView(withId(android.R.id.button1)).inRoot(isDialog()).check(matches(isDisplayed()));
    DependencyFactory.setCurrentFirebaseUser(null);
    onView(withId(android.R.id.button1)).perform(click());
    onView(withId(R.id.edit_profile)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()));
  }

  @Test
  public void testUserAlreadyLoggedIn_editProfile_changeName() {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, null, PROVIDER));
    DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
    mActivityRule.launchActivity(null);
    navigateToAccountTab();
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.edit_profile)).perform(ViewActions.scrollTo()).perform(click());
    // give time to display the dialog
    getInstrumentation().waitForIdleSync();

    onView(withId(R.id.change_name_dialog_user_input))
      .perform(replaceText("Mr Test"));

    onView(withId(android.R.id.button1)).perform(click());

    onView(withId(R.id.user_name)).check(matches(withText("Mr Test")));
  }

  public UserAccountPage launchFragment() throws Throwable {
    MainActivity activity = mainActivityTestRule.getActivity();
    NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);

    runOnUiThread(() -> navController.navigate(R.id.nav_account));

    Fragment navHostFragment =
      activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
    getInstrumentation().waitForIdleSync();
    return (UserAccountPage) navHostFragment.getChildFragmentManager().getFragments().get(0);
  }

  @Test
  public void addPictureWorks() throws Throwable {
    mActivityRule.launchActivity(null);
    UserAccountPage currentFragment = launchFragment();
    navigateToAccountTab();
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.edit_profile)).perform(ViewActions.scrollTo()).perform(click());
    // inject picture
    Bitmap bm = Bitmap.createBitmap(200, 100, Bitmap.Config.RGB_565);
    Intent intent = new Intent();
    intent.putExtra(FavorEditingView.CAMERA_DATA_KEY, bm);
    runOnUiThread(() -> currentFragment.onActivityResult(2, RESULT_OK, intent));

    getInstrumentation().waitForIdleSync();

    onView(withId(R.id.new_profile_picture)).check(matches(isDisplayed()));
  }

  @Test
  public void loadSavedPicture() throws Throwable {
    mActivityRule.launchActivity(null);
    UserAccountPage currentFragment = launchFragment();
    navigateToAccountTab();
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.edit_profile)).perform(ViewActions.scrollTo()).perform(click());
    getInstrumentation().waitForIdleSync();
    // inject picture
    Bitmap bm = Bitmap.createBitmap(200, 100, Bitmap.Config.RGB_565);
    Uri filePath =
      CacheUtil.getInstance()
        .saveToInternalStorage(
          Objects.requireNonNull(currentFragment.getContext()), bm, PROFILE_PICTURE_ID, 0);
    getInstrumentation().waitForIdleSync();
    Bitmap actual =
      CacheUtil.getInstance()
        .loadFromInternalStorage(
          Objects.requireNonNull(currentFragment.getContext()).getFilesDir().getAbsolutePath()
            + "/"
            + PROFILE_PICTURE_ID
            + "/",
          0)
        .get();
    assert (actual.sameAs(bm));
    Intent intent = new Intent();
    intent.setData(filePath);
    runOnUiThread(() -> currentFragment.onActivityResult(1, RESULT_OK, intent));
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.new_profile_picture)).check(matches(isDisplayed()));
  }

  @Test
  public void testUserAlreadyLoggedIn_editProfile_changeProfilePicture() throws Throwable {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, null, PROVIDER));
    DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
    mActivityRule.launchActivity(null);
    UserAccountPage currentFragment = launchFragment();
    navigateToAccountTab();
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.edit_profile)).perform(ViewActions.scrollTo()).perform(click());
    // give time to display the dialog
    getInstrumentation().waitForIdleSync();

    Bitmap bm = Bitmap.createBitmap(200, 100, Bitmap.Config.RGB_565);
    Intent intent = new Intent();
    intent.putExtra(FavorEditingView.CAMERA_DATA_KEY, bm);
    runOnUiThread(() -> currentFragment.onActivityResult(2, RESULT_OK, intent));

    onView(withId(android.R.id.button1)).perform(click());

    onView(withId(R.id.user_profile_picture)).check(matches(isDisplayed()));
  }
}
