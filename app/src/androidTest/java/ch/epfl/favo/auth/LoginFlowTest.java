//package ch.epfl.favo.auth;
//
//import android.os.RemoteException;
//import android.widget.EditText;
//
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.platform.app.InstrumentationRegistry;
//import androidx.test.rule.ActivityTestRule;
//import androidx.test.uiautomator.By;
//import androidx.test.uiautomator.UiDevice;
//import androidx.test.uiautomator.UiObject;
//import androidx.test.uiautomator.UiObject2;
//import androidx.test.uiautomator.UiObjectNotFoundException;
//import androidx.test.uiautomator.UiSelector;
//import androidx.test.uiautomator.Until;
//
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import ch.epfl.favo.R;
//import ch.epfl.favo.util.DependencyFactory;
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
//import static com.google.android.gms.common.api.CommonStatusCodes.TIMEOUT;
//
//@RunWith(AndroidJUnit4.class)
//public class LoginFlowTest {
//
//  @Rule
//  public final ActivityTestRule<SignInActivity> mActivityRule =
//      new ActivityTestRule<SignInActivity>(SignInActivity.class) {
//        @Override
//        protected void beforeActivityLaunched() {
//          DependencyFactory.setCurrentFirebaseUser(null);
//        }
//      };
//
//  @Test
//  public void testFacebookLogin()
//      throws UiObjectNotFoundException, InterruptedException, RemoteException {
//
//    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
//    mDevice.wakeUp();
//
//    getInstrumentation().waitForIdleSync();
//    Thread.sleep(5000);
//
//    // wait to be in the login page
//    if (!mDevice.findObject(new UiSelector().textContains("Sign in with Facebook")).exists()) {
//      Thread.sleep(5000);
//    }
//
//    // click on sign in with facebook
//    onView(withText("Sign in with Facebook")).check(matches(isDisplayed()));
//    onView(withText("Sign in with Facebook")).perform(click());
//
//    Thread.sleep(5000);
//
//    // click on login
//    if (mDevice.findObject(new UiSelector().clickable(true).textContains("Log In")).exists()) {
//      // insert email
//      UiObject2 editText = mDevice.findObject(By.clazz("android.widget.EditText"));
//      editText.setText("vhnwqbiihe_1583436753@tfbnw.net");
//
//      // insert password
//      UiObject input = mDevice.findObject(new UiSelector().instance(1).className(EditText.class));
//      input.setText("favo123");
//
//      mDevice.findObject(new UiSelector().clickable(true).textContains("Log In")).click();
//    }
//
//    Thread.sleep(5000);
//
//    // click on continue if present (when already logged in)
//    if (mDevice.findObject(new UiSelector().clickable(true).textContains("Continue")).exists())
//      mDevice.findObject(new UiSelector().clickable(true).textContains("Continue")).click();
//
//    Thread.sleep(1000);
//  }
//
//  @Test
//  public void testUserNotLoggedIn() {
//    getInstrumentation().waitForIdleSync();
//    // check sign-in screen is first page of the app
//    onView(withId(R.id.logo)).check(matches(isDisplayed()));
//  }
//
//  @Test
//  public void testUserNotLoggedIn_SignInWithFacebook_BackButtonPressed() {
//    getInstrumentation().waitForIdleSync();
//    onView(withText("Sign in with Facebook")).check(matches(isDisplayed()));
//
//    onView(withText("Sign in with Facebook")).perform(click());
//    getInstrumentation().waitForIdleSync();
//
//    // click back button
//    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
//    mDevice.pressBack();
//    getInstrumentation().waitForIdleSync();
//
//    // check if we're back to sign-in screen
//    onView(withId(R.id.logo)).check(matches(isDisplayed()));
//  }
//
//  @Test
//  public void testUserNotLoggedIn_SignInWithGoogle_BackButtonPressed() {
//    getInstrumentation().waitForIdleSync();
//    onView(withText("Sign in with Google")).check(matches(isDisplayed()));
//
//    onView(withText("Sign in with Google")).perform(click());
//    getInstrumentation().waitForIdleSync();
//
//    // click back button
//    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
//    mDevice.pressBack();
//    getInstrumentation().waitForIdleSync();
//
//    // check if we're back to sign-in screen
//    onView(withId(R.id.logo)).check(matches(isDisplayed()));
//  }
//
//  @Test
//  public void testUserNotLoggedIn_SignInWithEmail_BackButtonPressed() {
//    getInstrumentation().waitForIdleSync();
//    onView(withText("Sign in with email")).check(matches(isDisplayed()));
//
//    onView(withText("Sign in with email")).perform(click());
//    getInstrumentation().waitForIdleSync();
//
//    // click back button
//    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
//    mDevice.pressBack();
//    getInstrumentation().waitForIdleSync();
//    mDevice.pressBack();
//    getInstrumentation().waitForIdleSync();
//
//    // check if we're back to sign-in screen
//    onView(withId(R.id.logo)).check(matches(isDisplayed()));
//  }
//
//  @Test
//  public void testSignInOffline() throws UiObjectNotFoundException, InterruptedException {
//
//    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
//    mDevice.openQuickSettings();
//
//    // remove connection
//    mDevice.findObject(new UiSelector().text("AndroidWifi")).click();
//    mDevice.findObject(new UiSelector().text("Mobile data")).click();
//
//    if (mDevice.findObject(new UiSelector().text("Turn off")).exists())
//      mDevice.findObject(new UiSelector().text("Turn off")).click();
//
//    // go back to the app
//    mDevice.pressBack();
//    if (!mDevice.findObject(new UiSelector().textContains("Sign in with google")).exists())
//      mDevice.pressBack();
//
//    mDevice.wait(Until.hasObject(By.desc("Sign in with google")), TIMEOUT);
//    getInstrumentation().waitForIdleSync();
//    onView(withText("Sign in with Google")).check(matches(isDisplayed()));
//    onView(withText("Sign in with Google")).perform(click());
//
//    getInstrumentation().waitForIdleSync();
//
//    // should be still in the sign-in page because no connection
//    onView(withId(R.id.logo)).check(matches(isDisplayed()));
//
//    mDevice.openQuickSettings();
//
//    // put connection back
//    mDevice.findObject(new UiSelector().text("Wi-Fi")).click();
//    mDevice.findObject(new UiSelector().text("Mobile data")).click();
//
//    mDevice.pressBack();
//    if (!mDevice.findObject(new UiSelector().textContains("Sign in with google")).exists())
//      mDevice.pressBack();
//  }
//}
