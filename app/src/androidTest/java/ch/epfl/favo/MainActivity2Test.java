package ch.epfl.favo;

import android.app.Instrumentation;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.internal.inject.InstrumentationContext;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import ch.epfl.favo.testhelpers.FakeFirebaseUser;
import ch.epfl.favo.util.DependencyFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.R.id.nav_about;
import static ch.epfl.favo.R.id.nav_account;
import static ch.epfl.favo.testhelpers.TestConstants.EMAIL;
import static ch.epfl.favo.testhelpers.TestConstants.NAME;
import static ch.epfl.favo.testhelpers.TestConstants.PHOTO_URI;
import static ch.epfl.favo.testhelpers.TestConstants.PROVIDER;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class MainActivity2Test {
    private MainActivity2 mActivity;
    @Rule
    public final ActivityTestRule<MainActivity2> mainActivityTestRule =
            new ActivityTestRule<MainActivity2>(MainActivity2.class){
                @Override
                protected void beforeActivityLaunched() {
                    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
                }
            };

    @Test
    public void testMapViewIsLaunched() {
        //Click on map tab
        onView(withId(R.id.nav_map_button))
                .check(matches(isDisplayed()))
                        .perform(click());
        getInstrumentation().waitForIdleSync();
        //Check that the current fragment is the map tab
        onView(allOf(withId(R.id.map), withParent(withId(R.id.nav_host_fragment))))
                .check(matches(isDisplayed()));
    }
    @Test
    public void testFavorListViewIsLaunched() {
        //Click on favors tab
        onView(withId(R.id.nav_favor_list_button))
                .check(matches(isDisplayed()))
                .perform(click());
        getInstrumentation().waitForIdleSync();
        //check that tab 2 is indeed opened
        onView(allOf(withId(R.id.fragment_tab2), withParent(withId(R.id.nav_host_fragment))))
                .check(matches(isDisplayed()));
    }
    @Test
    public void testMenuDrawerCanBeLaunchedFromMapView(){
        //Click on map tab
        onView(withId(R.id.nav_map_button))
                .check(matches(isDisplayed()))
                .perform(click());
        getInstrumentation().waitForIdleSync();

        //Click on menu tab
        onView(withId(R.id.hamburger_menu_button))
                .check(matches(isDisplayed()))
                .perform(click());

        getInstrumentation().waitForIdleSync();

        //check that menu drawer is displayed
        onView(withId(R.id.nav_view))
                .check(matches(isDisplayed()));
    }
    @Test
    public void testMenuDrawerCanBeLaunchedFromFavorsView(){
        //Click on map tab
        onView(withId(R.id.nav_favor_list_button))
                .check(matches(isDisplayed()))
                .perform(click());
        getInstrumentation().waitForIdleSync();

        //Click on menu tab
        onView(withId(R.id.hamburger_menu_button))
                .check(matches(isDisplayed()))
                .perform(click());

        getInstrumentation().waitForIdleSync();

        //check that menu drawer is displayed
        onView(withId(R.id.nav_view))
                .check(matches(isDisplayed()));
    }
    @Test
    public void testAccountTabIsLaunched(){

        //Click on menu tab
        onView(withId(R.id.hamburger_menu_button))
                .check(matches(isDisplayed()))
                .perform(click());

        getInstrumentation().waitForIdleSync();

        //Click on account icon
        onView(anyOf(withText(R.string.account),
        withId(nav_account)))
        .perform(click());

        getInstrumentation().waitForIdleSync();
        //check that tab 2 is indeed opened
        onView(allOf(withId(R.id.user_info_fragment),
                withParent(withId(R.id.nav_host_fragment))))
                .check(matches(isDisplayed()));
    }
    @Test
    public void testSettingsTabIsLaunched(){

        //Click on menu tab
        onView(withId(R.id.hamburger_menu_button))
                .check(matches(isDisplayed()))
                .perform(click());

        getInstrumentation().waitForIdleSync();

        //Click on account icon
        onView(anyOf(withText(R.string.settings),
                withId(R.id.nav_settings)))
                .perform(click());

        getInstrumentation().waitForIdleSync();
        //check that tab 2 is indeed opened
        onView(allOf(withId(R.id.fragment_settings),
                withParent(withId(R.id.nav_host_fragment))))
                .check(matches(isDisplayed()));
    }
    @Test
    public void testAboutTabIsLaunched(){

        //Click on menu tab
        onView(withId(R.id.hamburger_menu_button))
                .check(matches(isDisplayed()))
                .perform(click());

        getInstrumentation().waitForIdleSync();

        //Click on account icon
        onView(anyOf(withText(R.string.about),
                withId(nav_about)))
                .perform(click());

        getInstrumentation().waitForIdleSync();
        //check that tab 2 is indeed opened
        onView(allOf(withId(R.id.fragment_about),
                withParent(withId(R.id.nav_host_fragment))))
                .check(matches(isDisplayed()));
    }
    @Test
    public void testShareTabIsLaunched(){

        //Click on menu tab
        onView(withId(R.id.hamburger_menu_button))
                .check(matches(isDisplayed()))
                .perform(click());

        getInstrumentation().waitForIdleSync();

        //Click on account icon
        onView(anyOf(withText(R.string.share),
                withId(R.id.nav_share)))
                .perform(click());

        getInstrumentation().waitForIdleSync();
        //check that tab 2 is indeed opened
        onView(allOf(withId(R.id.fragment_share),
                withParent(withId(R.id.nav_host_fragment))))
                .check(matches(isDisplayed()));
    }
}
