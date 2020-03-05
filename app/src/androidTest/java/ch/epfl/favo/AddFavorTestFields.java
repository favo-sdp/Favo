package ch.epfl.favo;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddFavorTestFields {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void addFavorTestFields() {
        ViewInteraction tabView = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.tab_layout),
                                0),
                        1),
                        isDisplayed()));
        tabView.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.new_favor), withText("New Favor"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragment_tab2),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.title),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragment_favor),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Title"), closeSoftKeyboard());

        ViewInteraction editText = onView(
                allOf(withId(R.id.title), withText("Title"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragment_favor),
                                        0),
                                1),
                        isDisplayed()));
        editText.check(matches(withText("Title")));

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.location),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragment_favor),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("Location"), closeSoftKeyboard());

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.location), withText("Location"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragment_favor),
                                        0),
                                5),
                        isDisplayed()));
        editText2.check(matches(withText("Location")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
