package ch.epfl.favo.view;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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

import ch.epfl.favo.FakeViewModel;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.tabs.MapPage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class MapPageTest {
  private MockDatabaseWrapper mockDatabaseWrapper = new MockDatabaseWrapper<Favor>();

  @Rule
  public final ActivityTestRule<MainActivity> mainActivityTestRule =
      new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
          mockDatabaseWrapper.setThrowError(true);
          //          DependencyFactory.setCurrentCollectionWrapper(mockDatabaseWrapper);
          DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
          DependencyFactory.setCurrentViewModelClass(FakeViewModel.class);
        }
      };

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

  @After
  public void tearDown() {
    DependencyFactory.setCurrentCollectionWrapper(null);
    DependencyFactory.setCurrentGpsTracker(null);
  }

  @Test
  public void RetrieveNearbyFavorsExceptionShowSnackBarTest() throws InterruptedException {
    mockDatabaseWrapper.setThrowError(true);
    FavorUtil.getSingleInstance().updateCollectionWrapper(mockDatabaseWrapper);
    onView(withId(R.id.list_switch)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    onView(withId(R.id.map_switch)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(500);
    // check snackbar shows
    // onView(withId(com.google.android.material.R.id.snackbar_text))
    //       .check(matches(withText(R.string.nearby_favors_exception)));
  }
  /*
    @Test
    public void InfoWindowClickSelfTest() throws UiObjectNotFoundException, InterruptedException {
      MapPage mapsPage = new MapPage();
      //mapsPage.updateFavorlist();
      //mapsPage.queryFavor(TestConstants.LATITUDE, TestConstants.LONGITUDE);
      //CheckContent("FavorRequest", R.string.favor_request_success_msg);
    }

    @Test
    public void InfoWindowClickOtherTest() throws InterruptedException, UiObjectNotFoundException {
        //CheckContent("Title of Favor 0", R.string.favor_respond_success_msg);
    }
  */
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
    device.click(x, (int) (y * 0.8));
    waitFor(2000);
    onView(withId(R.id.commit_complete_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(snackbar)));
  }

  @Test
  public void testNewRequestView() throws Throwable {
    launchMapFragment(MapPage.NEW_REQUEST);
    getInstrumentation().waitForIdleSync();
    Thread.sleep(500);
    onView(withId(R.id.toolbar_main_activity))
        .check(matches(hasDescendant(withText(R.string.map_request_favor))));
  }

  @Test
  public void testEditLocationView() throws Throwable {
    launchMapFragment(MapPage.EDIT_EXISTING_LOCATION);
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.toolbar_main_activity))
        .check(matches(hasDescendant(withText(R.string.map_edit_favor_loc))));
  }

  @Test
  public void testShareLocationView() throws Throwable {
    launchMapFragment(MapPage.SHARE_LOCATION);
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.toolbar_main_activity))
        .check(matches(hasDescendant(withText(R.string.map_share_loc))));
  }

  @Test
  public void testObserveLocationView() throws Throwable {
    launchMapFragment(MapPage.OBSERVE_LOCATION);
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.toolbar_main_activity))
        .check(matches(hasDescendant(withText(R.string.map_observe_loc))));
  }

  private void waitFor(int t) throws InterruptedException {
    Thread.sleep(t);
  }

  private MapPage launchMapFragment(int intentType) throws Throwable {
    MainActivity activity = mainActivityTestRule.getActivity();
    NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
    Bundle bundle = new Bundle();
    bundle.putInt(MapPage.LOCATION_ARGUMENT_KEY, intentType);
    runOnUiThread(() -> navController.navigate(R.id.action_global_nav_map, bundle));

    Fragment navHostFragment =
        activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
    getInstrumentation().waitForIdleSync();
    Thread.sleep(1000);
    return (MapPage) navHostFragment.getChildFragmentManager().getFragments().get(0);
  }
}
