package ch.epfl.favo;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.runner.RunWith;

import ch.epfl.favo.presenter.MainActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MapActivityTest {
    @Rule
    public final ActivityTestRule<MainActivity> mapActivityTestRule =
            new ActivityTestRule<>(MainActivity.class, true, false);

    public void testGetMapView() {
        onView(withId(R.id.pager)).perform(swipeLeft());
        onView(withId(R.id.pager)).perform(swipeRight());
        //onView(withId(R.id.greetingMessage)).check(matches(withText("Hello from my unit test!")));
    }

}
