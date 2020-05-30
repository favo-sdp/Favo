package ch.epfl.favo.view;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.FakeUserUtil;
import ch.epfl.favo.FakeViewModel;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.TestConstants;
import ch.epfl.favo.user.User;
import ch.epfl.favo.util.DependencyFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;

@RunWith(AndroidJUnit4.class)
public class SettingsPageTest {

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
          testUser.setNotificationRadius(TestConstants.NOTIFICATION_RADIUS);
          testUser.setChatNotifications(TestConstants.DEFAULT_NOTIFICATION_PREFERENCE);
          testUser.setUpdateNotifications(TestConstants.DEFAULT_NOTIFICATION_PREFERENCE);
          mockDatabaseWrapper.setMockDocument(testUser);
          mockDatabaseWrapper.setMockResult(testUser);
          new FakeUserUtil().setFindUserResult(testUser);
          DependencyFactory.setCurrentUserRepository(new FakeUserUtil());
          DependencyFactory.setCurrentViewModelClass(FakeViewModel.class);
        }
      };

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

  public void navigateToSettingsTab() {

    // direct to the account tab
    onView(withId(R.id.hamburger_menu_button)).perform(click());
    getInstrumentation().waitForIdleSync();
    // Click on account icon
    onView(withId(R.id.nav_settings)).perform(click());
    getInstrumentation().waitForIdleSync();
  }

  @After
  public void tearDown() {
    DependencyFactory.setCurrentFirebaseUser(null);
    DependencyFactory.setCurrentGpsTracker(null);
    DependencyFactory.setCurrentCollectionWrapper(null);
    DependencyFactory.setCurrentViewModelClass(null);
  }

  @Test
  public void testNearbyFavorNotificationsSwitch() {
    navigateToSettingsTab();

    // enable switch
    clickPreference(R.string.notification_title);

    // disable switch
    clickPreference(R.string.notification_title);

    onView(withId(androidx.preference.R.id.recycler_view)).check(matches(isDisplayed()));
  }

  @Test
  public void testChatNotificationsSwitch() {
    navigateToSettingsTab();

    // enable switch
    clickPreference(R.string.notification_chat_title);

    // disable switch
    clickPreference(R.string.notification_chat_title);

    onView(withId(androidx.preference.R.id.recycler_view)).check(matches(isDisplayed()));
  }

  @Test
  public void testUpdateNotificationsSwitch() {
    navigateToSettingsTab();

    // enable switch
    clickPreference(R.string.notification_update_title);

    // disable switch
    clickPreference(R.string.notification_update_title);

    onView(withId(androidx.preference.R.id.recycler_view)).check(matches(isDisplayed()));
  }

  private void clickPreference(int resId) {
    onView(withId(androidx.preference.R.id.recycler_view))
        .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(resId)), click()));
  }
}
