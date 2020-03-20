package ch.epfl.favo.view;

import android.location.Location;

import androidx.fragment.app.FragmentTransaction;
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
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.tabs.addFavor.FavorDetailView;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class FavorDetailViewTest {
    private Favor fakeFavor;
    @Rule
    public final ActivityTestRule<MainActivity> mainActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
                }
            };

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void setUP(){
        Location mockLocation = createLocation(37  ,-122,3.0f);
        fakeFavor = new Favor("Title","Desc","0",mockLocation,0);
    }

   Location createLocation(double lat, double lng, float accuracy) {
        // Create a new Location
        Location newLocation = new Location("flp");
        newLocation.setLatitude(lat);
        newLocation.setLongitude(lng);
        newLocation.setAccuracy(accuracy);
        return newLocation;
    }

    @After
    public void tearDown() {
        DependencyFactory.setCurrentFirebaseUser(null);
    }


    @Test
    public void favorDetailViewIsLaunched(){
        FavorDetailView fragment = FavorDetailView.newInstance(fakeFavor);

        FragmentTransaction transaction = mainActivityTestRule.getActivity()
                .getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        //check that detailed view is indeed opened
        onView(allOf(withId(R.id.fragment_favor_accept_view), withParent(withId(R.id.nav_host_fragment))))
                .check(matches(isDisplayed()));

        //Check clicking on the button
        onView(withId(R.id.accept_button)).check(matches(isDisplayed()))
                .perform(click());

        //check snackbar shows
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.favor_respond_success_msg)));
    }






}
