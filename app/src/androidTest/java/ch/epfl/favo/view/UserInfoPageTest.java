package ch.epfl.favo.view;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.TestConstants;
import ch.epfl.favo.user.User;
import ch.epfl.favo.util.DependencyFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static ch.epfl.favo.util.FavorFragmentFactory.USER_ARGS;

@RunWith(AndroidJUnit4.class)
public class UserInfoPageTest {

  private MockDatabaseWrapper mockDatabaseWrapper = new MockDatabaseWrapper<User>();
  private User testUser =
      new User(
          TestConstants.USER_ID,
          TestConstants.NAME,
          TestConstants.EMAIL,
          TestConstants.DEVICE_ID,
          null,
          null);

  @Rule
  public final ActivityTestRule<MainActivity> mainActivityTestRule =
      new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
          DependencyFactory.setCurrentFirebaseUser(
              new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
          DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
          DependencyFactory.setCurrentCollectionWrapper(mockDatabaseWrapper);
          mockDatabaseWrapper.setMockDocument(testUser);
          mockDatabaseWrapper.setMockResult(testUser);
        }
      };

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

  @Before
  public void setUp() throws Throwable {
    launchFragment();
  }

  @After
  public void tearDown() {
    DependencyFactory.setCurrentGpsTracker(null);
    DependencyFactory.setCurrentFirebaseUser(null);
    DependencyFactory.setCurrentCollectionWrapper(null);
  }

  public void launchFragment() throws Throwable {
    MainActivity activity = mainActivityTestRule.getActivity();
    NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
    Bundle bundle = new Bundle();
    bundle.putString(USER_ARGS, testUser.getId());
    runOnUiThread(() -> navController.navigate(R.id.action_nav_map_to_userInfoView, bundle));
  }

  @Test
  public void testDisplayUserData() throws InterruptedException {

    getInstrumentation().waitForIdleSync();

    onView(withId(R.id.user_info_fragment)).check(matches(isDisplayed()));

    onView(withId(R.id.user_info_fragment)).perform(swipeUp());

    Thread.sleep(1000);

    onView(withId(R.id.display_name)).check(matches(withText(TestConstants.NAME)));
    onView(withId(R.id.display_email)).check(matches(withText(TestConstants.EMAIL)));
  }

  @Test
  public void testReportUser() throws InterruptedException {
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.user_info_fragment)).check(matches(isDisplayed()));

    onView(withId(R.id.report_user)).perform(scrollTo(), click());

    Thread.sleep(4000);
    onView(withId(R.id.report_user)).perform(click());
    Thread.sleep(1000);
    onView(withText(R.string.report_message)).check(matches(isDisplayed()));
  }

  @Test
  public void testMakePositiveFeedback() throws InterruptedException {
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.user_info_fragment)).check(matches(isDisplayed()));

    onView(withId(R.id.like_button)).perform(scrollTo(), click());

    Thread.sleep(1000);
    onView(withId(R.id.like_button)).perform(click());
    Thread.sleep(1000);
    onView(withText(R.string.feedback_message)).check(matches(isDisplayed()));
  }

  @Test
  public void testMakeNegativeFeedback() throws InterruptedException {
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.user_info_fragment)).check(matches(isDisplayed()));

    onView(withId(R.id.dislike_button)).perform(scrollTo(), click());

    Thread.sleep(1000);
    onView(withId(R.id.dislike_button)).perform(click());
    Thread.sleep(1000);
    onView(withText(R.string.feedback_message)).check(matches(isDisplayed()));
  }
}
