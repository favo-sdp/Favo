package ch.epfl.favo.auth;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.R;
import ch.epfl.favo.util.DependencyFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static com.google.android.gms.common.api.CommonStatusCodes.TIMEOUT;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.core.StringEndsWith.endsWith;

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

  @Test
  public void testSignInOffline() throws UiObjectNotFoundException, InterruptedException {

    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    mDevice.openQuickSettings();
    // remove connection
    mDevice.findObject(new UiSelector().text("AndroidWifi")).click();
    mDevice.findObject(new UiSelector().text("Mobile data")).click();

    if (mDevice.findObject(new UiSelector().text("Turn off")).exists())
      mDevice.findObject(new UiSelector().text("Turn off")).click();

    // go back to the app
    mDevice.pressBack();
    mDevice.pressBack();

    mDevice.wait(Until.hasObject(By.desc("Sign in with google")), TIMEOUT);
    getInstrumentation().waitForIdleSync();
    onView(withText("Sign in with Google")).check(matches(isDisplayed()));
    onView(withText("Sign in with Google")).perform(click());

    getInstrumentation().waitForIdleSync();

    // should be still in the sign-in page because no connection
    onView(withId(R.id.logo)).check(matches(isDisplayed()));

    // put connection back
    mDevice.openQuickSettings();
    mDevice.findObject(new UiSelector().text("Wi-Fi")).click();
    mDevice.findObject(new UiSelector().text("Mobile data")).click();
    mDevice.pressBack();
    mDevice.pressBack();
  }


}
