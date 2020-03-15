package ch.epfl.favo.view;

import android.graphics.Point;
import android.util.Log;
import android.view.Display;

import androidx.test.espresso.Espresso;
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
import org.openjdk.tools.javac.comp.Check;

import ch.epfl.favo.MainActivity;
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
public class MapPageTest {
      @Rule
      public final ActivityTestRule<MainActivity> mainActivityTestRule =
          new ActivityTestRule<MainActivity>(MainActivity.class) {
            @Override
            protected void beforeActivityLaunched() {
          }
      };


    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @After
    public void tearDown() {
        DependencyFactory.setCurrentFirebaseUser(null);
    }

    @Test
    public void InfoWindowClickSelfTest() throws InterruptedException, UiObjectNotFoundException {
        Espresso.closeSoftKeyboard();
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.hiddenButton));
        //CheckContent("I am Here", R.string.favor_success_msg);
    }


    @Test
    public void InfoWindowClickOtherTest() throws InterruptedException, UiObjectNotFoundException {

        //CheckContent("Title of Favor 0", R.string.favor_respond_success_msg);
    }

    public void CheckContent(String MarkerTitle, int snackbar) throws UiObjectNotFoundException, InterruptedException {
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
        int y = (int)(screenHeight * 0.43 );
        Log.d("debug", "h" + screenHeight);
        Log.d("debug ", "w " + screenWidth);
        device.click(x, (int)(y * 0.8));
        waitFor(2000);
        onView(withId(R.id.add_button))
                .check(matches(isDisplayed())).perform(click());
        getInstrumentation().waitForIdleSync();
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(snackbar)));
    }



    private void waitFor(int t) throws InterruptedException {
        Thread.sleep((long)t);
    }
}

