package ch.epfl.favo.view;

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
import ch.epfl.favo.favor.Favor;
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
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class SearchViewTest {
    @Rule
    public final ActivityTestRule<MainActivity> mainActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
            }
    };

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @After
    public void tearDown() {
        DependencyFactory.setCurrentGpsTracker(null);
    }

    private void typeFavors(){
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

        //Click on searchView button
        onView(withId(R.id.searchView)).check(matches(isDisplayed())).perform(click());
        getInstrumentation().waitForIdleSync();

        // check spinner is invisible and favor list is empty
        onView(withText(favor.getTitle())).check(doesNotExist());
        onView(withId(R.id.spinner)).check(matches(not(isDisplayed())));
    }


    @Test
    public void testSearchViewFound() {

        typeFavors();

        Favor favor = FakeItemFactory.getFavor();
        //type the title of fake favor
        onView(withId(R.id.searchView)).perform(typeText(favor.getTitle())).perform(pressKey(KeyEvent.KEYCODE_ENTER));
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
        // check favor is displayed in active favor list view
        onView(withText(favor.getDescription())).check(matches(isDisplayed())).perform(click());
        getInstrumentation().waitForIdleSync();
    }

    @Test
    public void testSearchViewNotFound() {

        typeFavors();

        Favor favor = FakeItemFactory.getFavor();
        //type the title of fake favor
        onView(withId(R.id.searchView)).perform(typeText("random words")).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        getInstrumentation().waitForIdleSync();

        // check the tip text is displayed when query failed
        onView(withId(R.id.tip))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.query_failed)));

        //Click on close searchView button
        pressBack();
        pressBack();
    }

    @Test
    public void testClickScreenHideKeyboard(){
        typeFavors();

        //Click on upper left screen corner
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        device.click(10,50 );

        // if keyboard displayed, one time of pressBack will return to Favor List view
        pressBack();
        Favor favor = FakeItemFactory.getFavor();
        // check favor is displayed in active favor list view
        onView(withText(favor.getDescription())).check(matches(isDisplayed())).perform(click());
    }

}
