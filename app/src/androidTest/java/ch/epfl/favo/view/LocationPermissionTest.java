package ch.epfl.favo.view;

import android.Manifest;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertFalse;
/*
public class LocationPermissionTest {
    @Rule
    public final ActivityTestRule<MainActivity> mainActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class) {};
    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION);
    @Test
    public void getLocationPermissionAllowTest() throws UiObjectNotFoundException {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject allowButton = device.findObject(new UiSelector().textContains("allow").clickable(true) );
        allowButton.click();
        assertFalse(allowButton.exists());
    }
    @Test
    public void getLocationPermissionDenyTest() throws UiObjectNotFoundException, InterruptedException {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject allowButton = device.findObject(new UiSelector().textContains("deny").clickable(true) );
        allowButton.click();
        //check snack bar shows
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.no_location_permission)));
    }
}
*/