package ch.epfl.favo.view;

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
