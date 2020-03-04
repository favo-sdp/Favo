package ch.epfl.favo;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeLeft;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;



import ch.epfl.favo.presenter.MainActivity;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public final ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);
    @Test
    public void testCanChangeTabs(){
        onView(withId(R.id.text1)).check(matches(withText("1")));
        //TODO: Replace with actual text in layout
        onView(withId(R.id.pager)).perform(swipeLeft());
        onView(withId(R.id.text2)).check(matches(withText("2")));
        onView(withId(R.id.pager)).perform(swipeLeft());
        onView(withId(R.id.text3)).check(matches(withText("3")));
    }

}