package ch.epfl.favo.auth;

import android.os.RemoteException;
import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.util.DependencyFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SignInActivityInstrumentationTest {

  @Rule
  public final ActivityTestRule<SignInActivity> mActivityRule =
      new ActivityTestRule<>(SignInActivity.class);

  @Before
  public void setup() {
    DependencyFactory.setCurrentFirebaseUser(null);
  }

  @After
  public void tearDown() {
    DependencyFactory.setCurrentFirebaseUser(null);
  }

  @Test
  public void testGenerateNotificationToken() {
    mActivityRule.getActivity().retrieveCurrentRegistrationToken();
  }

  @Test
  public void testFacebookLogin()
      throws UiObjectNotFoundException, InterruptedException, RemoteException {

    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    mDevice.wakeUp();

    // wait to be in the login page
    while (!mDevice.findObject(new UiSelector().textContains("Sign in with Facebook")).exists()) {
      Thread.sleep(3000);
    }

    // click on sign in with facebook
    onView(withText("Sign in with Facebook")).check(matches(isDisplayed()));
    onView(withText("Sign in with Facebook")).perform(click());

    Thread.sleep(5000);

    // click on login
    if (mDevice.findObject(new UiSelector().clickable(true).textContains("Log In")).exists()) {
      // insert email
      UiObject2 editText = mDevice.findObject(By.clazz("android.widget.EditText"));
      editText.setText("vhnwqbiihe_1583436753@tfbnw.net");

      // insert password
      UiObject input = mDevice.findObject(new UiSelector().instance(1).className(EditText.class));
      input.setText("favo123");

      mDevice.findObject(new UiSelector().clickable(true).textContains("Log In")).click();
    }

    Thread.sleep(3000);

    // click on continue if present (when already logged in)
    if (mDevice.findObject(new UiSelector().clickable(true).textContains("Continue")).exists())
      mDevice.findObject(new UiSelector().clickable(true).textContains("Continue")).click();

    Thread.sleep(3000);
  }
}
