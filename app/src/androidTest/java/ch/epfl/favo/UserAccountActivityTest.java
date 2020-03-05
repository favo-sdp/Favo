package ch.epfl.favo;

import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.testhelpers.FakeFirebaseUserFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringEndsWith.endsWith;

@RunWith(AndroidJUnit4.class)
public class UserAccountActivityTest {

  private static final String EMAIL = "test@example.com";
  private static final String NAME = "Test Testerson";
  private static final String PROVIDER = "test provider";
  private static final Uri PHOTO_URI = Uri.parse("http://example.com/profile.png");

  @Rule
  public final ActivityTestRule<SignInActivity> mActivityRule =
      new ActivityTestRule<>(SignInActivity.class, true, false);

  @Test
  public void testUserAlreadyLoggedIn_displayUserData() {
    DependencyFactory.setCurrentFirebaseUser(
        FakeFirebaseUserFactory.createFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
    mActivityRule.launchActivity(null);
    onView(withId(R.id.user_name)).check(matches(withText(NAME)));
    onView(withId(R.id.user_email)).check(matches(withText(EMAIL)));
    onView(withId(R.id.user_providers)).check(matches(withText(endsWith(PROVIDER))));
  }

  @Test
  public void testUserAlreadyLoggedIn_displayUserDataMissingName() {
    DependencyFactory.setCurrentFirebaseUser(
        FakeFirebaseUserFactory.createFirebaseUser(null, EMAIL, PHOTO_URI, PROVIDER));
    mActivityRule.launchActivity(null);
    onView(withId(R.id.user_name)).check(matches(withText(EMAIL.split("@")[0])));
  }

  @Test
  public void testUserAlreadyLoggedIn_displayUserDataMissingEmail() {
    DependencyFactory.setCurrentFirebaseUser(
        FakeFirebaseUserFactory.createFirebaseUser(null, "", PHOTO_URI, PROVIDER));
    mActivityRule.launchActivity(null);
    onView(withId(R.id.user_email)).check(matches(withText("No email")));
  }

  @Test
  public void testUserAlreadyLoggedIn_displayUserDataMissingPhoto() {
    DependencyFactory.setCurrentFirebaseUser(
        FakeFirebaseUserFactory.createFirebaseUser(NAME, EMAIL, null, PROVIDER));
    mActivityRule.launchActivity(null);
    onView(withId(R.id.user_name)).check(matches(withText(NAME)));
    onView(withId(R.id.user_email)).check(matches(withText(EMAIL)));
    onView(withId(R.id.user_providers)).check(matches(withText(endsWith(PROVIDER))));
  }

  @Test
  public void testUserAlreadyLoggedIn_signOut() {
    DependencyFactory.setCurrentFirebaseUser(
        FakeFirebaseUserFactory.createFirebaseUser(NAME, EMAIL, null, PROVIDER));
    mActivityRule.launchActivity(null);
    DependencyFactory.setCurrentFirebaseUser(null);
    onView(withId(R.id.sign_out)).perform(click());
    onView(withId(R.id.logo)).check(matches(isDisplayed()));
  }

  @Test
  public void testUserAlreadyLoggedIn_deleteAccountNotConfirmed() {
    DependencyFactory.setCurrentFirebaseUser(
        FakeFirebaseUserFactory.createFirebaseUser(NAME, EMAIL, null, PROVIDER));
    mActivityRule.launchActivity(null);
    DependencyFactory.setCurrentFirebaseUser(null);
    onView(withId(R.id.delete_account)).perform(click());
    onView(withId(android.R.id.button2)).perform(click());
  }

  // Not testing delete account because weird problems happen when buttons are pressed, need to
  // investigate on the problem

  //     @Test
  //     public void testUserAlreadyLoggedIn_deleteAccountConfirmed() {
  //
  // DependencyFactory.setCurrentFirebaseUser(FakeFirebaseUserFactory.createFirebaseUser(NAME,
  // EMAIL, null, PROVIDER));
  //         mActivityRule.launchActivity(null);
  //         Intents.init();
  //         DependencyFactory.setCurrentFirebaseUser(null);
  //         onView(withId(R.id.delete_account)).perform(click());
  //         onView(withText("Yes")).perform(click());
  //     }

  // @Test
  // public void testUserAlreadyLoggedIn_deleteAccountConfirmed() {}

  //
  //     @Test
  //     public void testUserAlreadyLoggedIn_deleteAccountConfirmed() {
  //         DependencyFactory.setCurrentFirebaseUser(null);
  //         onView(withId(R.id.delete_account)).perform(click());
  //         intended(hasComponent(SignInActivity.class.getName()));

  //
  //        DependencyFactory.setCurrentFirebaseUser(null);
  //         onView(withId(R.id.delete_account)).perform(click());
  //         onView(withId(android.R.id.button1)).perform(click());
  //         onView(withId(R.id.user_name)).check(matches(is(not(isDisplayed()))));
  // onView(withId(R.id.user_name)).check(matches(isDisplayed()));
  // intended(hasComponent(SignInActivity.class.getName()));
  // }

}