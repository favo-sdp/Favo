package ch.epfl.favo;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.util.DependencyFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
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
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public final ActivityTestRule<MainActivity> mainActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class){
                @Override
                protected void beforeActivityLaunched() {
                    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
                }
            };
    @After
    public void tearDown() {
        DependencyFactory.setCurrentFirebaseUser(null);
    }

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);


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
    @Test
    public void testHomeTabIsLaunched_IsMap(){

        //Click on menu tab
        onView(withId(R.id.hamburger_menu_button))
                .check(matches(isDisplayed()))
                .perform(click());

        getInstrumentation().waitForIdleSync();

        //Click on account icon
        onView(anyOf(withText(R.string.home),
                withId(R.id.nav_home)))
                .perform(click());

        getInstrumentation().waitForIdleSync();
        //check that tab 2 is indeed opened
        onView(allOf(withId(R.id.map),
                withParent(withId(R.id.nav_host_fragment))))
                .check(matches(isDisplayed()));
    }
    @Test
    public void testBackButtonReturnsPreviousFragment_Map(){

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
        //check that share fragment is indeed opened
        onView(allOf(withId(R.id.fragment_share),
                withParent(withId(R.id.nav_host_fragment))))
                .check(matches(isDisplayed()));
        //Click on back button
        onView(withId(R.id.back_button))
                .perform(click());
        getInstrumentation().waitForIdleSync();
        //check that we're back on the main page
        onView(allOf(withId(R.id.map),
                withParent(withId(R.id.nav_host_fragment))))
                .check(matches(isDisplayed()));
    }
    @Test
    public void testBackButtonReturnsPreviousFragment_FavorList(){

        //Click on favor list tab
        onView(withId(R.id.nav_favor_list_button))
                .check(matches(isDisplayed()))
                .perform(click());
        getInstrumentation().waitForIdleSync();

        //Click on new favor to open favor request tab
        onView(withId(R.id.new_favor))
                .check(matches(isDisplayed()))
                .perform(click());

        getInstrumentation().waitForIdleSync();

        //Click on back button
        onView(withId(R.id.back_button))
                .perform(click());
        getInstrumentation().waitForIdleSync();

        //check that we're back on the favor list page
        onView(allOf(withId(R.id.fragment_tab2),
                withParent(withId(R.id.nav_host_fragment))))
                .check(matches(isDisplayed()));
    }
    @Test
    public void testAndroidBackButtonReturnsPreviousFragment_FavorList(){

        //Click on favor list tab
        onView(withId(R.id.nav_favor_list_button))
                .check(matches(isDisplayed()))
                .perform(click());
        getInstrumentation().waitForIdleSync();

        //Click on new favor to open favor request tab
        onView(withId(R.id.new_favor))
                .check(matches(isDisplayed()))
                .perform(click());

        getInstrumentation().waitForIdleSync();

        //Click on back button
        pressBack();
        getInstrumentation().waitForIdleSync();


        //check that we're back on the favor list page
        onView(allOf(withId(R.id.fragment_tab2),
                withParent(withId(R.id.nav_host_fragment))))
                .check(matches(isDisplayed()));
    }
}
