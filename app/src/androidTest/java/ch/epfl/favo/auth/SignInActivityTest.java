package ch.epfl.favo.auth;

import android.Manifest;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.R;
import ch.epfl.favo.util.DependencyFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringEndsWith.endsWith;

@RunWith(AndroidJUnit4.class)
public class SignInActivityTest {

  @Rule
  public ActivityTestRule<SignInActivity> mActivityTestRule =
      new ActivityTestRule<>(SignInActivity.class);

  @Rule
  public GrantPermissionRule mRuntimePermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION);

  @Before
  public void setup() throws InterruptedException {
    Thread.sleep(5000);
    DependencyFactory.setCurrentFirebaseUser(null);
  }

  @Test
  public void testSignInPage() throws InterruptedException {
    onView(ViewMatchers.withId(R.id.logo)).check(matches(isDisplayed()));
    onView(withText(endsWith("Sign in with Google"))).check(matches(isDisplayed()));
    onView(withText(endsWith("Sign in with Facebook"))).check(matches(isDisplayed()));
    onView(withText(endsWith("Sign in with email"))).check(matches(isDisplayed()));
  }

  @Test
  public void testActivityResultAfterSignIn() throws InterruptedException {
    onView(withId(R.id.logo)).check(matches(isDisplayed()));
    onView(withText(endsWith("Sign in with Facebook"))).check(matches(isDisplayed()));
    onView(withText(endsWith("Sign in with Facebook"))).perform(click());
    pressBack();

      // can't test that logo sign-in page is displayed because this is handled by the library
      // automatically
  }
}
