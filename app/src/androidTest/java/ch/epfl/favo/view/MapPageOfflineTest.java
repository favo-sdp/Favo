package ch.epfl.favo.view;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.tabs.MapsPage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;

@RunWith(AndroidJUnit4.class)
public class MapPageOfflineTest {

  @Rule
  public final ActivityTestRule<MainActivity> mainActivityTestRule =
      new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
          DependencyFactory.setCurrentFirebaseUser(
              new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
          DependencyFactory.setOfflineMode(true);
          MapsPage.firstTime = true;
        }
      };

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

  @After
  public void tearDown() {
    DependencyFactory.setCurrentFirebaseUser(null);
    DependencyFactory.setOfflineMode(false);
  }

  @Test
  public void testOfflineMapSupport() throws InterruptedException {
    Thread.sleep(500);

    // check snackbar shows
    onView(withText(R.string.offline_mode_snack)).check(matches(isDisplayed()));

    // click on snackbar action button
    onView(withText(R.string.offline_mode_action)).perform(click());

    getInstrumentation().waitForIdleSync();

    // check dialog is shown
    onView(withText(R.string.offline_mode_dialog_title)).check(matches(isDisplayed()));

    // click on ok button to dismiss the dialog
    onView(withText(android.R.string.yes)).perform(click());
  }

  @Test
  public void testOfflineMapSupport_ClickLink() throws InterruptedException {
    Thread.sleep(500);

    // check snackbar shows
    onView(withText(R.string.offline_mode_snack)).check(matches(isDisplayed()));

    // click on snackbar action button
    onView(withText(R.string.offline_mode_action)).perform(click());

    getInstrumentation().waitForIdleSync();

    // check dialog is shown
    onView(withText(R.string.offline_mode_dialog_title)).check(matches(isDisplayed()));

    // click on "show me how" to see instructions on the web
    onView(withText(R.string.offline_mode_dialog_link)).perform(click());

    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    mDevice.pressBack();
  }

  public void turnOffConnection() throws UiObjectNotFoundException {
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
  }

  public void turnOnConnection() throws UiObjectNotFoundException {
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    mDevice.openQuickSettings();

    // put connection back
    mDevice.findObject(new UiSelector().text("Wi-Fi")).click();
    mDevice.findObject(new UiSelector().text("Mobile data")).click();

    mDevice.pressBack();
    mDevice.pressBack();
  }
}
