package ch.epfl.favo;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.favo.testhelpers.TestConstants.EMAIL;
import static ch.epfl.favo.testhelpers.TestConstants.NAME;
import static ch.epfl.favo.testhelpers.TestConstants.PHOTO_URI;
import static ch.epfl.favo.testhelpers.TestConstants.PROVIDER;

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
    public void testCanChangeTabs() {
        //onView(withId(R.id.text1)).check(matches(withText("1")));
        //TODO: Replace with actual text in layo

        onView(withId(R.id.nav_map_button)).perform(click());
        onView(withId(R.id.nav_host_fragment)).check(matches(withId(R.id.map)));
        List<Fragment> fragments = mActivity.getSupportFragmentManager().getFragments();

    }
}
