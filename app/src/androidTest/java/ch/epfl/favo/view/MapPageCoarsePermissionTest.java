package ch.epfl.favo.view;

import android.Manifest;
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
public class MapPageCoarsePermissionTest {
    @Rule
    public final ActivityTestRule<MainActivity> mainActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                }
            };

    @After
    public void tearDown() {
        DependencyFactory.setCurrentFirebaseUser(null);
    }

    @Rule public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION);

    @Test
    public void InfoWindowClickOtherTest() throws InterruptedException, UiObjectNotFoundException {

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        //UiObject marker = device.findObject(new UiSelector().descriptionContains("Title of Favor 1"));
        //marker.click();
        waitFor(1000);
        Display display = mainActivityTestRule.getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        int x = (screenWidth / 2);
        int y = (int)(screenHeight * 0.43 );

        device.click(x, y);
        waitFor(1000);
        getInstrumentation().waitForIdleSync();
        device.click(x, (int)(y * 0.66));
        waitFor(2000);
        onView(withId(R.id.add_button));
              //  .check(matches(isDisplayed())).perform(click());
        getInstrumentation().waitForIdleSync();
        onView(withId(com.google.android.material.R.id.snackbar_text));
             //   .check(matches(withText(R.string.favor_success_msg)));
    }

    private void waitFor(int t) throws InterruptedException {
        Thread.sleep((long)t);
    }
}


