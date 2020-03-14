package ch.epfl.favo.view;

import android.Manifest;
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

        //UiObject marker = device.findObject(new UiSelector().descriptionContains("Title of Favor 1"));
        //marker.click();
      //  for(float i = 0; i < 1; i += 0.01){
      //      device.click(x, (int)(i * screenHeight));
       //     waitFor(1000);
      //  }


        Espresso.closeSoftKeyboard();
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.hiddenButton))
                .check(matches(isDisplayed()))
                .perform(click());
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


