package ch.epfl.favo.auth;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.R;
import ch.epfl.favo.util.DependencyFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class SignInActivityTest {

  @Rule
  public final ActivityTestRule<SignInActivity> mActivityRule =
      new ActivityTestRule<>(SignInActivity.class, false);

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

  @After
  public void tearDown() {
    DependencyFactory.setCurrentFirebaseUser(null);
  }

  @Test
  public void testUserNotLoggedIn() {
    getInstrumentation().waitForIdleSync();
    // check sign-in screen is first page of the app
    onView(withId(R.id.logo)).check(matches(isDisplayed()));
  }

  @Test
  public void testUserNotLoggedIn_SignInWithFacebook_BackButtonPressed() {
    getInstrumentation().waitForIdleSync();
    onView(withText("Sign in with Facebook")).check(matches(isDisplayed()));

    onView(withText("Sign in with Facebook")).perform(click());
    getInstrumentation().waitForIdleSync();

    // click back button
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    mDevice.pressBack();
    getInstrumentation().waitForIdleSync();

    // check if we're back to sign-in screen
    onView(withId(R.id.logo)).check(matches(isDisplayed()));
  }

  @Test
  public void testUserNotLoggedIn_SignInWithGoogle_BackButtonPressed() {
    getInstrumentation().waitForIdleSync();
    onView(withText("Sign in with Google")).check(matches(isDisplayed()));

    onView(withText("Sign in with Google")).perform(click());
    getInstrumentation().waitForIdleSync();

    // click back button
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    mDevice.pressBack();
    getInstrumentation().waitForIdleSync();

    // check if we're back to sign-in screen
    onView(withId(R.id.logo)).check(matches(isDisplayed()));
  }

  @Test
  public void testUserNotLoggedIn_SignInWithEmail_BackButtonPressed() {
    getInstrumentation().waitForIdleSync();
    onView(withText("Sign in with email")).check(matches(isDisplayed()));

    onView(withText("Sign in with email")).perform(click());
    getInstrumentation().waitForIdleSync();

    // click back button
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    mDevice.pressBack();
    getInstrumentation().waitForIdleSync();
    mDevice.pressBack();
    getInstrumentation().waitForIdleSync();

    // check if we're back to sign-in screen
    onView(withId(R.id.logo)).check(matches(isDisplayed()));
  }
}
