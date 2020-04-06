package ch.epfl.favo.view;

import android.graphics.Point;
import android.util.Log;
import android.view.Display;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.TestConstants;
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
public class MapPageTest {
  @Rule
  public final ActivityTestRule<MainActivity> mainActivityTestRule =
          new ActivityTestRule<MainActivity>(MainActivity.class) {
            @Override
            protected void beforeActivityLaunched() {
              DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
            }
          };

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

  @After
  public void tearDown() {
    DependencyFactory.setCurrentGpsTracker(null);
  }

  @Test
  public void InfoWindowClickSelfTest() throws UiObjectNotFoundException, InterruptedException {
    MapsPage mapsPage = new MapsPage();
    //mapsPage.updateFavorlist();
    //mapsPage.queryFavor(TestConstants.LATITUDE, TestConstants.LONGITUDE);
    //CheckContent("FavorRequest", R.string.favor_request_success_msg);
  }

  @Test
  public void InfoWindowClickOtherTest() throws InterruptedException, UiObjectNotFoundException {
    //CheckContent("Title of Favor 0", R.string.favor_respond_success_msg);
  }

  public void CheckContent(String MarkerTitle, int snackbar)
      throws UiObjectNotFoundException, InterruptedException {
    UiDevice device = UiDevice.getInstance(getInstrumentation());
    UiObject marker = device.findObject(new UiSelector().descriptionContains(MarkerTitle));
    marker.click();

    waitFor(1000);
    Display display = mainActivityTestRule.getActivity().getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getRealSize(size);
    int screenWidth = size.x;
    int screenHeight = size.y;
    int x = (screenWidth / 2);
    int y = (int) (screenHeight * 0.43);
    Log.d("debug", "h" + screenHeight);
    Log.d("debug ", "w " + screenWidth);
    device.click(x, (int) (y * 0.8));
    waitFor(2000);
    onView(withId(R.id.accept_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(snackbar)));
  }

  private void waitFor(int t) throws InterruptedException {
    Thread.sleep(t);
  }
}
