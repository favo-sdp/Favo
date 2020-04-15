package ch.epfl.favo.view;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.DependencyFactory;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class FavorPageTest {
  private MockDatabaseWrapper mockDatabaseWrapper = new MockDatabaseWrapper();
  @Rule
  public final ActivityTestRule<MainActivity> mainActivityTestRule =
      new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
          DependencyFactory.setCurrentFirebaseUser(
              new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
          DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
          DependencyFactory.setCurrentCollectionWrapper(new MockDatabaseWrapper());
          mockDatabaseWrapper.setMockDocument(FakeItemFactory.getFavor());
          mockDatabaseWrapper.setThrowError(false);
        }
      };

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

  @After
  public void tearDown() {
    DependencyFactory.setCurrentFirebaseUser(null);
    DependencyFactory.setCurrentGpsTracker(null);
    DependencyFactory.setCurrentCollectionWrapper(null);
  }

  @Test
  public void testFavorPageElements() {
    // click on favors tab
    onView(withId(R.id.nav_favor_list_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // check that tab 2 is indeed opened
    onView(allOf(withId(R.id.fragment_tab2), withParent(withId(R.id.nav_host_fragment))))
        .check(matches(isDisplayed()));

    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed()));

    getInstrumentation().waitForIdleSync();

    // onView(withId(R.id.favor_list)).check(matches(isDisplayed()));

    onView(withId(R.id.spinner)).perform(click());
    onData(allOf(is(instanceOf(String.class)), is("Archived"))).perform(click());
    onView(withId(R.id.spinner)).check(matches(withSpinnerText(containsString("Archived"))));

    getInstrumentation().waitForIdleSync();

    onView(withId(R.id.spinner)).perform(click());
    onData(allOf(is(instanceOf(String.class)), is("Active"))).perform(click());
    onView(withId(R.id.spinner)).check(matches(withSpinnerText(containsString("Active"))));
  }

  @Test
  public void testTextDisplayedWhenListEmpty() {
    // click on favors tab
    onView(withId(R.id.nav_favor_list_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // check that tab 2 is indeed opened
    onView(allOf(withId(R.id.fragment_tab2), withParent(withId(R.id.nav_host_fragment))))
        .check(matches(isDisplayed()));

    // check the tip text is displayed when active favor list is empty
    onView(withId(R.id.tip))
        .check(matches(isDisplayed()))
        .check(matches(withText(R.string.favor_no_active_favor)));

    // go to archived list
    onView(withId(R.id.spinner)).perform(click());
    onData(allOf(is(instanceOf(String.class)), is("Archived"))).perform(click());
    onView(withId(R.id.spinner)).check(matches(withSpinnerText(containsString("Archived"))));

    // check the tip text is displayed when archived favor list is empty
    onView(withId(R.id.tip))
        .check(matches(isDisplayed()))
        .check(matches(withText(R.string.favor_no_archived_favor)));
  }

  @Test
  public void testNewFavorButton() {
    // Click on favors tab
    onView(withId(R.id.nav_favor_list_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // check that tab 2 is indeed opened
    onView(allOf(withId(R.id.fragment_tab2), withParent(withId(R.id.nav_host_fragment))))
        .check(matches(isDisplayed()));

    // check that the new favor button is displayed and click on it
    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // check that the favor request fragment is displayed
    onView(withId(R.id.fragment_favor)).check(matches(isDisplayed()));
  }

  @Test
  public void testFavorRequestUpdatesListView() {
    // Click on favors tab
    onView(withId(R.id.nav_favor_list_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Click on new favor tab
    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Fill in text views with fake favor
    Favor favor = FakeItemFactory.getFavor();

    onView(withId(R.id.title_request_view)).perform(typeText(favor.getTitle()));
    onView(withId(R.id.details)).perform(typeText(favor.getDescription()));

    // Click on request button
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Click on back button
    onView(withId(R.id.back_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // check favor is displayed in active favor list view
    onView(withText(favor.getTitle())).check(matches(isDisplayed()));
  }


  @Test
  public void testFavorCancelUpdatesActiveAndArchivedListView() {
    // Click on favors tab
    onView(withId(R.id.nav_favor_list_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Click on new favor tab
    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Fill in text views with fake favor
    Favor favor = FakeItemFactory.getFavor();

    onView(withId(R.id.title_request_view)).perform(typeText(favor.getTitle()));
    onView(withId(R.id.details)).perform(typeText(favor.getDescription()));

    // Click on request button
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Click on cancel button
    onView(withId(R.id.cancel_favor_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Go back
    onView(withId(R.id.back_button)).perform(click());
    getInstrumentation().waitForIdleSync();

    // Check favor is not displayed in active list
    onView(withText(favor.getTitle())).check(doesNotExist());

    // go to archived list
    onView(withId(R.id.spinner)).perform(click());
    onData(allOf(is(instanceOf(String.class)), is("Archived"))).perform(click());
    getInstrumentation().waitForIdleSync();

    // check favor is displayed in archived favor list view
    onView(withText(favor.getTitle())).check(matches(isDisplayed()));
  }
}
