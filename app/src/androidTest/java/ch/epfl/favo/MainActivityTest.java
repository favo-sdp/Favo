package ch.epfl.favo;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.util.DependencyFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

  @Rule
  public final ActivityTestRule<MainActivity> mainActivityActivityTestRule =
      new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
          DependencyFactory.setCurrentFirebaseUser(
              new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
        }
      };

  @Test
  public void testCanChangeTabs() throws InterruptedException {
    // onView(withId(R.id.text1)).check(matches(withText("1")));
    // TODO: Replace with actual text in layout
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.pager)).perform(swipeLeft());
    Thread.sleep(3000);
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.sign_out)).check(matches(isDisplayed()));
    onView(withId(R.id.pager)).perform(swipeRight());
    onView(withId(R.id.pager)).perform(swipeRight());
    // onView(withId(R.id.text2)).check(matches(withText("2")));
  }
}
