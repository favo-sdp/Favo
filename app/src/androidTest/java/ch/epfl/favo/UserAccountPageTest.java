package ch.epfl.favo;

import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.auth.SignInActivity;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.testhelpers.FakeFirebaseUser;
import ch.epfl.favo.view.tabs.UserAccountPage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static ch.epfl.favo.testhelpers.TestConstants.*;

@RunWith(AndroidJUnit4.class)
public class UserAccountPageTest {

  @Rule
  public final ActivityTestRule<SignInActivity> mActivityRule =
      new ActivityTestRule<>(SignInActivity.class, true, false);

  @Test
  public void testUserAlreadyLoggedIn_displayUserData() {
    DependencyFactory.setCurrentFirebaseUser(
        new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
    mActivityRule.launchActivity(null);
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.user_name)).check(matches(withText(NAME)));
    onView(withId(R.id.user_email)).check(matches(withText(EMAIL)));
    onView(withId(R.id.user_providers)).check(matches(withText(endsWith(PROVIDER))));
  }

  @Test
  public void testUserAlreadyLoggedIn_displayUserDataMissingName() {
    DependencyFactory.setCurrentFirebaseUser(
        new FakeFirebaseUser(null, EMAIL, PHOTO_URI, PROVIDER));
    mActivityRule.launchActivity(null);
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.user_name)).check(matches(withText(EMAIL.split("@")[0])));
  }

  @Test
  public void testUserAlreadyLoggedIn_displayUserDataMissingEmail() {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(null, "", PHOTO_URI, PROVIDER));
    mActivityRule.launchActivity(null);
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.user_email)).check(matches(withText("No email")));
  }

  @Test
  public void testUserAlreadyLoggedIn_displayUserDataMissingPhoto() {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, null, PROVIDER));
    mActivityRule.launchActivity(null);
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.user_name)).check(matches(withText(NAME)));
    onView(withId(R.id.user_email)).check(matches(withText(EMAIL)));
    onView(withId(R.id.user_providers)).check(matches(withText(endsWith(PROVIDER))));
  }

  @Test
  public void testUserAlreadyLoggedIn_signOut() {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, null, PROVIDER));
    mActivityRule.launchActivity(null);
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.pager)).perform(swipeLeft());
    DependencyFactory.setCurrentFirebaseUser(null);
    onView(withId(R.id.sign_out)).perform(click());
  }

  @Test
  public void testUserAlreadyLoggedIn_deleteAccountNotConfirmed() throws InterruptedException {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, null, PROVIDER));
    mActivityRule.launchActivity(null);
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.pager)).perform(swipeLeft());
    DependencyFactory.setCurrentFirebaseUser(null);
    onView(withId(R.id.delete_account)).perform(click());
  }

  // Not testing delete account because weird problems happen when buttons are pressed, need to
  // investigate on the problem

}
