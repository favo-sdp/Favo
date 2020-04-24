package ch.epfl.favo;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.MockGpsTracker;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.R.id.nav_about;
import static ch.epfl.favo.R.id.nav_account;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

  @Rule
  public final ActivityTestRule<MainActivity> mainActivityTestRule =
      new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
          DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
          DependencyFactory.setCurrentFirebaseUser(
              new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
        }
      };

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

  @After
  public void tearDown() {
    DependencyFactory.setCurrentGpsTracker(null);
    DependencyFactory.setCurrentFirebaseUser(null);
  }

  @Test
  public void testMapViewIsLaunched() {
    // Click on map tab
    onView(allOf(withId(R.id.nav_map), withContentDescription("Map")))
        .check(matches(isDisplayed()))
        .perform(click());
    getInstrumentation().waitForIdleSync();
    // Check that the current fragment is the map tab
    onView(withId(R.id.fragment_map)).check(matches(isDisplayed()));
  }

  @Test
  public void testFavorListViewIsLaunched() {
    // Click on favors tab
    onView(withId(R.id.nav_favorList)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();
    // check that tab 2 is indeed opened
    onView(allOf(withId(R.id.fragment_favors), withParent(withId(R.id.nav_host_fragment))))
        .check(matches(isDisplayed()));
  }

  @Test
  public void testMenuDrawerCanBeLaunchedFromMapView() {
    // Click on map tab
    onView(allOf(withId(R.id.nav_map), withContentDescription("Map")))
        .check(matches(isDisplayed()))
        .perform(click());
    getInstrumentation().waitForIdleSync();

    // Click on menu tab
    onView(withId(R.id.hamburger_menu_button)).check(matches(isDisplayed())).perform(click());

    getInstrumentation().waitForIdleSync();

    // check that menu drawer is displayed
    onView(withId(R.id.nav_view)).check(matches(isDisplayed()));
  }

  @Test
  public void testMenuDrawerCanBeLaunchedFromFavorsView() {
    // Click on map tab
    onView(withId(R.id.nav_favorList)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Click on menu tab
    onView(withId(R.id.hamburger_menu_button)).check(matches(isDisplayed())).perform(click());

    getInstrumentation().waitForIdleSync();

    // check that menu drawer is displayed
    onView(withId(R.id.nav_view)).check(matches(isDisplayed()));
  }

  @Test
  public void testAccountTabIsLaunched() {

    // Click on menu tab
    onView(withId(R.id.hamburger_menu_button)).check(matches(isDisplayed())).perform(click());

    getInstrumentation().waitForIdleSync();

    // Click on account icon
    onView(withId(nav_account)).perform(click());

    getInstrumentation().waitForIdleSync();
    // check that tab 2 is indeed opened
    onView(allOf(withId(R.id.user_info_fragment), withParent(withId(R.id.nav_host_fragment))))
        .check(matches(isDisplayed()));
  }

  @Test
  public void testSettingsTabIsLaunched() {

    // Click on menu tab
    onView(withId(R.id.hamburger_menu_button)).check(matches(isDisplayed())).perform(click());

    getInstrumentation().waitForIdleSync();

    // Click on account icon
    onView(withId(R.id.nav_settings)).perform(click());

    getInstrumentation().waitForIdleSync();
    // check that tab 2 is indeed opened
    onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
  }

  @Test
  public void testAboutTabIsLaunched() {

    // Click on menu tab
    onView(withId(R.id.hamburger_menu_button)).check(matches(isDisplayed())).perform(click());

    getInstrumentation().waitForIdleSync();

    // Click on account icon
    onView(withId(nav_about)).perform(click());

    getInstrumentation().waitForIdleSync();
    // check that tab 2 is indeed opened
    onView(allOf(withId(R.id.fragment_about), withParent(withId(R.id.nav_host_fragment))))
        .check(matches(isDisplayed()));
  }

  @Test
  public void testShareIntentIsLaunched() {

    // Click on menu tab
    onView(withId(R.id.hamburger_menu_button)).check(matches(isDisplayed())).perform(click());

    getInstrumentation().waitForIdleSync();

    // Click on account icon
    onView(withId(R.id.nav_share)).perform(click());

    getInstrumentation().waitForIdleSync();
    // check that share intent is indeed opened
    onView(allOf(withId(android.R.id.title), withText("Share"), isDisplayed()));

    // click back button
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    mDevice.pressBack();
  }

  @Test
  public void testHomeTabIsLaunched_IsMap() {

    // Click on menu tab
    onView(withId(R.id.hamburger_menu_button)).check(matches(isDisplayed())).perform(click());

    getInstrumentation().waitForIdleSync();

    onView(allOf(withId(R.id.nav_map), withContentDescription(not("Map"))))
        .check(matches(isDisplayed()))
        .perform(click());

    getInstrumentation().waitForIdleSync();
    // check that tab 2 is indeed opened
    onView(withParent(withId(R.id.nav_host_fragment))).check(matches(isDisplayed()));
  }

  @Test
  public void testBackButtonReturnsPreviousFragment_Map() {

    // Click on menu tab
    onView(withId(R.id.hamburger_menu_button)).check(matches(isDisplayed())).perform(click());

    getInstrumentation().waitForIdleSync();

    // Click on account icon
    onView(withId(nav_about)).perform(click());

    getInstrumentation().waitForIdleSync();

    // Click on back button
    pressBack();
    getInstrumentation().waitForIdleSync();

    // check that we're back on the main page
    onView(allOf(withId(R.id.nav_map), withContentDescription("Map")))
        .check(matches(isDisplayed()));
  }

  @Test
  public void testShareIntent() {

    // Click on menu tab
    onView(withId(R.id.hamburger_menu_button)).check(matches(isDisplayed())).perform(click());

    getInstrumentation().waitForIdleSync();

    // click share
    onView(withId(R.id.nav_share)).perform(click());

    getInstrumentation().waitForIdleSync();

    // check that share intent is indeed opened
    onView(allOf(withId(android.R.id.title), withText("Share"), isDisplayed()));

    // click back button
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    mDevice.pressBack();

    getInstrumentation().waitForIdleSync();

    // check that we're back on the main page
    onView(withParent(withId(R.id.nav_host_fragment))).check(matches(isDisplayed()));
  }

  @Test
  public void testBackButtonReturnsPreviousFragment_FavorList() {

    // Click on favor list tab
    onView(withId(R.id.nav_favorList)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Click on new favor to open favor request tab
    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed())).perform(click());

    getInstrumentation().waitForIdleSync();

    // Click on back button
    onView(withId(R.id.hamburger_menu_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // check that we're back on the favor list page
    onView(allOf(withId(R.id.fragment_favors), withParent(withId(R.id.nav_host_fragment))))
        .check(matches(isDisplayed()));
  }

  @Test
  public void testAndroidBackButtonReturnsPreviousFragment_FavorList() {

    // Click on favor list tab
    onView(withId(R.id.nav_favorList)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Click on new favor to open favor request tab
    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed())).perform(click());

    getInstrumentation().waitForIdleSync();

    // Click on back button
    pressBack();
    getInstrumentation().waitForIdleSync();

    // check that we're back on the favor list page
    onView(allOf(withId(R.id.fragment_favors), withParent(withId(R.id.nav_host_fragment))))
        .check(matches(isDisplayed()));
  }
}
