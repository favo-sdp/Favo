package ch.epfl.favo.view;

import android.location.Location;
import android.util.Log;
import android.view.KeyEvent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.common.FavoLocation;
import ch.epfl.favo.common.NoPermissionGrantedException;
import ch.epfl.favo.common.NoPositionFoundException;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.map.Locator;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.DependencyFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class NearbyFavorListTest {
    private Favor favor = new Favor("CiaoCiao", "For test", UserUtil.currentUserId,
            new FavoLocation("mock"), Favor.Status.REQUESTED);
    @Rule
    public final ActivityTestRule<MainActivity> mainActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    DependencyFactory.setCurrentGpsTracker(new Locator() {
                        @Override
                        public Location getLocation() throws NoPermissionGrantedException, NoPositionFoundException {
                            Location location = new Location("mock");
                            location.setLatitude(6.5668);
                            location.setLongitude(46.5191);
                            return location;
                        }
                    });
                }
            };
    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @After
    public void tearDown() {
        DependencyFactory.setCurrentGpsTracker(null);
        //DependencyFactory.setCurrentCollectionWrapper(null);
    }

    private void openSearchView(){
        // switch to nearby favor list view
        try{
            Thread.sleep(3000);
            onView(withId(R.id.list_switch)).check(matches(isDisplayed())).perform(click());
            getInstrumentation().waitForIdleSync();

            //Click on searchView button
            onView(withId(R.id.nearby_searchView)).check(matches(isDisplayed())).perform(click());
            getInstrumentation().waitForIdleSync();
        } catch (Exception e){
            Log.d("ListTest", e.getMessage());
        }
    }

    @Test
    public void testSearchViewFound() {

        openSearchView();
        //type the title of fake favor
        onView(withId(R.id.nearby_searchView)).perform(typeText(favor.getTitle())).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        getInstrumentation().waitForIdleSync();

        // check query is successful and click on found item
        onView(withText(favor.getDescription())).check(matches(isDisplayed())).perform(click());
        getInstrumentation().waitForIdleSync();

        // Click on back button
        onView(withId(R.id.back_button)).check(matches(isDisplayed())).perform(click());
        getInstrumentation().waitForIdleSync();

        // press two times of back button to quit search mode
        pressBack();
        pressBack();
        // check active favors are displayed in active favor list view
        onView(withText(favor.getDescription())).check(matches(isDisplayed()));
        getInstrumentation().waitForIdleSync();
    }

    @Test
    public void testSearchViewNotFound() {
        openSearchView();

        //type the title of fake favor
        onView(withId(R.id.nearby_searchView)).perform(typeText("random words")).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        getInstrumentation().waitForIdleSync();

        // check the tip text is displayed when query failed
        onView(withId(R.id.nearby_tip))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.query_failed)));
        onView(withText(favor.getDescription())).check(doesNotExist());

        //Click on close searchView button
        pressBack();
        pressBack();
    }

    @Test
    public void testClickScreenHideKeyboard(){
        openSearchView();
        //Click on searchView button
        onView(withId(R.id.nearby_searchView)).check(matches(isDisplayed())).perform(click());
        getInstrumentation().waitForIdleSync();

        //Click on upper left screen corner
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        device.click(10,50);
        // if keyboard is not displayed, one time of pressBack will return to Favor List view
        pressBack();
        // check favor is displayed in active favor list view
        onView(withText(favor.getTitle())).check(matches(isDisplayed()));
    }

    @Test
    public void FavorDetailViewJumptoMapTest(){
        // just wait and change to listView
        openSearchView();
        pressBack();
        pressBack();

        // check test favor is found click on found item
        onView(withText(favor.getDescription())).check(matches(isDisplayed())).perform(click());
        getInstrumentation().waitForIdleSync();

        // Check and click on the location text
        onView(withId(R.id.location_accept_view_btn)).check(matches(isDisplayed())).perform(click());
    }
}

