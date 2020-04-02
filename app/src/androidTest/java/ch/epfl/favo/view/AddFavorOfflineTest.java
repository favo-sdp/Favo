package ch.epfl.favo.view;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.tabs.MapsPage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;

@RunWith(AndroidJUnit4.class)
public class AddFavorOfflineTest {

  @Rule
  public final ActivityTestRule<MainActivity> mainActivityTestRule =
      new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
          DependencyFactory.setCurrentFirebaseUser(
              new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
          DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
          DependencyFactory.setCurrentDatabaseUpdater(new MockDatabaseWrapper());
          DependencyFactory.setOfflineMode(true);
          MapsPage.firstTime = true;
        }
      };

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

  @After
  public void tearDown() {
    DependencyFactory.setCurrentFirebaseUser(null);
    DependencyFactory.setOfflineMode(false);
  }

  @Test
  public void testAddFavorOffline() {

    // check dialog is shown
    onView(withText(R.string.offline_mode_dialog_title)).check(matches(isDisplayed()));

    // click on ok button to dismiss the dialog
    onView(withText(android.R.string.yes)).perform(click());

    // Click on fav list tab
    onView(withId(R.id.nav_favor_list_button)).check(matches(isDisplayed())).perform(click());

    getInstrumentation().waitForIdleSync();

    // click on create new favor
    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed())).perform(click());

    getInstrumentation().waitForIdleSync();

    // check that different message is displayed
    onView(withText(R.string.request_favor_draft)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // check snackbar shows with the different message
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.save_draft_message)));

  }
}
