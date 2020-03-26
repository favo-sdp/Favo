package ch.epfl.favo.auth;

import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.firebase.ui.auth.IdpResponse;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.core.StringEndsWith.endsWith;

@RunWith(AndroidJUnit4.class)
public class SignInActivityTest {

  @Rule
  public final ActivityTestRule<SignInActivity> mActivityRule =
      new ActivityTestRule<>(SignInActivity.class);

  @Test
  public void testGenerateNotificationToken() {
    mActivityRule.getActivity().retrieveCurrentRegistrationToken();
  }

  @Test
  public void testFacebookLogin() throws UiObjectNotFoundException, InterruptedException {

    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    // wait to be in the login page
    while (!mDevice.findObject(new UiSelector().textContains("Sign in with Facebook")).exists()) {
      Thread.sleep(3000);
    }

    // click on sign in with facebook
    onView(withText("Sign in with Facebook")).check(matches(isDisplayed()));
    onView(withText("Sign in with Facebook")).perform(click());

    Thread.sleep(5000);

    // insert email
    UiObject2 editText = mDevice.findObject(By.clazz("android.widget.EditText"));
    editText.setText("vhnwqbiihe_1583436753@tfbnw.net");

    // insert password
    UiObject input = mDevice.findObject(new UiSelector()
            .instance(1)
            .className(EditText.class));
    input.setText("favo123");

    // click on login
    if (mDevice.findObject(new UiSelector().clickable(true).textContains("Log In")).exists())
      mDevice.findObject(new UiSelector().clickable(true).textContains("Log In")).click();

    Thread.sleep(3000);

    // click on continue if present (when already logged in)
    if (mDevice.findObject(new UiSelector().clickable(true).textContains("Continue")).exists())
      mDevice.findObject(new UiSelector().clickable(true).textContains("Continue")).click();

    Thread.sleep(3000);
  }

}
