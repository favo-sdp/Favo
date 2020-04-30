package ch.epfl.favo.view;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.R;
import ch.epfl.favo.TestConstants;
import ch.epfl.favo.auth.SignInActivity;
import ch.epfl.favo.user.User;
import ch.epfl.favo.util.DependencyFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static org.hamcrest.core.StringEndsWith.endsWith;

@RunWith(AndroidJUnit4.class)
public class UserAccountPageTest {

  private MockDatabaseWrapper mockDatabaseWrapper = new MockDatabaseWrapper<User>();

  @Rule
  public final ActivityTestRule<SignInActivity> mActivityRule =
      new ActivityTestRule<>(SignInActivity.class, true, false);

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
    DependencyFactory.setCurrentCollectionWrapper(mockDatabaseWrapper);
    mockDatabaseWrapper.setMockDocument(
        new User(
            TestConstants.USER_ID,
            TestConstants.NAME,
            TestConstants.EMAIL,
            TestConstants.DEVICE_ID,
            null,
            null));
  }

  @After
  public void tearDown() {
    DependencyFactory.setCurrentFirebaseUser(null);
    DependencyFactory.setCurrentGpsTracker(null);
    DependencyFactory.setCurrentCollectionWrapper(null);
  }

  @Test
  public void testUserNotLoggedIn() {
    // just to test that sign-in activity handled by the library is correctly displayed without
    // errors
    mActivityRule.launchActivity(null);
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
    DependencyFactory.setCurrentFirebaseUser(
        new FakeFirebaseUser(null, EMAIL, PHOTO_URI, PROVIDER));
    DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());

    mActivityRule.launchActivity(null);
    navigateToAccountTab();
    onView(withId(R.id.user_name)).check(matches(withText(EMAIL.split("@")[0])));
  }

  @Test
  public void testUserAlreadyLoggedIn_displayUserData_missingEmail() {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(null, "", PHOTO_URI, PROVIDER));
    DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());

    mActivityRule.launchActivity(null);
    navigateToAccountTab();
    onView(withId(R.id.user_email)).check(matches(withText("No email")));
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
    onView(withId(R.id.delete_account)).perform(click());
    // give time to display the dialog
    getInstrumentation().waitForIdleSync();
    onView(withText(endsWith("?"))).check(matches(isDisplayed()));
    onView(withId(android.R.id.button2)).inRoot(isDialog()).check(matches(isDisplayed()));
    onView(withId(android.R.id.button1)).inRoot(isDialog()).check(matches(isDisplayed()));
    onView(withId(android.R.id.button2)).perform(click());
    onView(withId(R.id.delete_account)).check(matches(isDisplayed()));
  }

  @Test
  public void testUserAlreadyLoggedIn_deleteAccount_alertShowed_confirmOperation() {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, null, PROVIDER));
    DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
    mActivityRule.launchActivity(null);
    navigateToAccountTab();
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.delete_account)).perform(click());
    // give time to display the dialog
    getInstrumentation().waitForIdleSync();
    onView(withText(endsWith("?"))).check(matches(isDisplayed()));
    onView(withId(android.R.id.button2)).inRoot(isDialog()).check(matches(isDisplayed()));
    onView(withId(android.R.id.button1)).inRoot(isDialog()).check(matches(isDisplayed()));
    DependencyFactory.setCurrentFirebaseUser(null);
    onView(withId(android.R.id.button1)).perform(click());
  }
}
