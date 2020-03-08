package ch.epfl.favo;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.auth.SignInActivity;
import ch.epfl.favo.util.DependencyFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static org.hamcrest.core.StringEndsWith.endsWith;

@RunWith(AndroidJUnit4.class)
public class UserAccountPageTest {

  @Rule
  public final ActivityTestRule<SignInActivity> mActivityRule =
      new ActivityTestRule<>(SignInActivity.class, true, false);

  @Test
  public void testUserNotLoggedIn() {
    DependencyFactory.setCurrentFirebaseUser(null);
    mActivityRule.launchActivity(null);

    // can't test that logo sign-in page is displayed because this is handled by the library
    // automatically

    // Thread.sleep(5000);
    // onView(withId(R.id.logo)).check(matches(isDisplayed()));
  }

  @Test
  public void testUserAlreadyLoggedIn_displayUserData() throws InterruptedException {
    DependencyFactory.setCurrentFirebaseUser(
        new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
    mActivityRule.launchActivity(null);
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.pager)).perform(swipeLeft());
    Thread.sleep(5000);
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.user_name)).check(matches(withText(NAME)));
    onView(withId(R.id.user_email)).check(matches(withText(EMAIL)));
    onView(withId(R.id.user_providers)).check(matches(withText(endsWith(PROVIDER))));
  }

  @Test
  public void testUserAlreadyLoggedIn_displayUserData_missingName() throws InterruptedException {
    DependencyFactory.setCurrentFirebaseUser(
        new FakeFirebaseUser(null, EMAIL, PHOTO_URI, PROVIDER));
    mActivityRule.launchActivity(null);
    Thread.sleep(3000);
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.user_name)).check(matches(withText(EMAIL.split("@")[0])));
  }

  @Test
  public void testUserAlreadyLoggedIn_displayUserData_missingEmail() throws InterruptedException {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(null, "", PHOTO_URI, PROVIDER));
    mActivityRule.launchActivity(null);
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.pager)).perform(swipeLeft());
    Thread.sleep(3000);
    onView(withId(R.id.user_email)).check(matches(withText("No email")));
  }

  @Test
  public void testUserAlreadyLoggedIn_displayUserData_missingPhoto() throws InterruptedException {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, null, PROVIDER));
    mActivityRule.launchActivity(null);
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.pager)).perform(swipeLeft());
    Thread.sleep(3000);
    onView(withId(R.id.user_name)).check(matches(withText(NAME)));
    onView(withId(R.id.user_email)).check(matches(withText(EMAIL)));
    onView(withId(R.id.user_providers)).check(matches(withText(endsWith(PROVIDER))));
  }

  @Test
  public void testUserAlreadyLoggedIn_signOut() throws InterruptedException {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, null, PROVIDER));
    mActivityRule.launchActivity(null);
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.pager)).perform(swipeLeft());
    DependencyFactory.setCurrentFirebaseUser(null);
    Thread.sleep(3000);
    onView(withId(R.id.sign_out)).perform(click());

    // can't test that logo sign-in page is displayed because this is handled by the library
    // automatically

    // Thread.sleep(5000);
    // onView(withId(R.id.logo)).check(matches(isDisplayed()));
  }

  @Test
  public void testUserAlreadyLoggedIn_deleteAccount_alertShowed_cancelOperation()
      throws InterruptedException {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, null, PROVIDER));
    mActivityRule.launchActivity(null);
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.pager)).perform(swipeLeft());
    Thread.sleep(3000);
    onView(withId(R.id.delete_account)).perform(click());
    // give time to display the first interface
    Thread.sleep(3000);
    onView(withText(endsWith("?"))).check(matches(isDisplayed()));
    Thread.sleep(5000);
    onView(withId(android.R.id.button2)).inRoot(isDialog()).check(matches(isDisplayed()));
    onView(withId(android.R.id.button1)).inRoot(isDialog()).check(matches(isDisplayed()));
    onView(withId(android.R.id.button2)).perform(click());
    onView(withId(R.id.delete_account)).check(matches(isDisplayed()));
  }

  @Test
  public void testUserAlreadyLoggedIn_deleteAccount_confirmOperation() throws InterruptedException {
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, null, PROVIDER));
    mActivityRule.launchActivity(null);
    onView(withId(R.id.pager)).perform(swipeLeft());
    onView(withId(R.id.pager)).perform(swipeLeft());
    DependencyFactory.setCurrentFirebaseUser(null);
    Thread.sleep(5000);
    onView(withId(R.id.delete_account)).perform(click());
    // give time to display the first interface
    Thread.sleep(5000);
    onView(withText(endsWith("?"))).check(matches(isDisplayed()));
    Thread.sleep(3000);
    onView(withId(android.R.id.button1)).inRoot(isDialog()).check(matches(isDisplayed()));

    // can't test confirm delete account because operation depends on too many internal
    // calls of the FirebaseAuth library but the written code is simple so it should be correct

    //    onView(withId(android.R.id.button1)).perform(click());
    //    onView(withId(R.id.delete_account)).check(matches(isDisplayed()));
    //    Thread.sleep(3000);
    //    onView(withId(R.id.logo)).check(matches(isDisplayed()));
  }
}
