package ch.epfl.favo.favorList;

import android.view.View;
import android.widget.EditText;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.FakeUserUtil;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.TestConstants;
import ch.epfl.favo.TestUtils;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.MockGpsTracker;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class  FavorPageTest {

  @Rule
  public final ActivityTestRule<MainActivity> mainActivityTestRule =
          new ActivityTestRule<MainActivity>(MainActivity.class) {
            @Override
            protected void beforeActivityLaunched() {
              DependencyFactory.setCurrentFirebaseUser(
                      new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
              DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
            }
          };

  @Rule
  public GrantPermissionRule permissionRule =
          GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

  @Before
  public void setUp() {
    DependencyFactory.setCurrentFavorCollection(TestConstants.TEST_COLLECTION);
    DependencyFactory.setCurrentUserRepository(new FakeUserUtil());
  }

  @After
  public void tearDown() throws ExecutionException, InterruptedException {
    TestUtils.cleanupFavorsCollection();
    DependencyFactory.setCurrentFirebaseUser(null);
    DependencyFactory.setCurrentGpsTracker(null);
    DependencyFactory.setCurrentUserRepository(null);
    // DependencyFactory.setCurrentFavorCollection("favors");
  }

  public static ViewAction withCustomConstraints(
          final ViewAction action, final Matcher<View> constraints) {
    return new ViewAction() {
      @Override
      public Matcher<View> getConstraints() {
        return constraints;
      }

      @Override
      public String getDescription() {
        return action.getDescription();
      }

      @Override
      public void perform(UiController uiController, View view) {
        action.perform(uiController, view);
      }
    };
  }

  @Test
  public void testFavorPageElements() {
    // click on favors tab
    onView(withId(R.id.nav_favorList)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // check that tab 2 is indeed opened
    onView(allOf(withId(R.id.fragment_favors), withParent(withId(R.id.nav_host_fragment))))
            .check(matches(isDisplayed()));

    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed()));

    getInstrumentation().waitForIdleSync();

    onView(withText(R.string.favor_no_active_favor)).check(matches(isDisplayed()));

    onView(withId(R.id.swipe_refresh_layout))
            .perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));

    onView(withId(R.id.archived_toggle)).check(matches(isDisplayed())).perform(click());

    getInstrumentation().waitForIdleSync();

    onView(withText(R.string.favor_no_archived_favor)).check(matches(isDisplayed()));

    onView(withId(R.id.swipe_refresh_layout))
            .perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));

    onView(withId(R.id.active_toggle)).perform(click());
  }


  @Test
  public void testFavorRequestUpdatesListView() throws InterruptedException {
    // Click on favors tab
    onView(withId(R.id.nav_favorList)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(2000);

    // Click on new favor tab
    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Fill in text views with fake favor
    Favor favor = FakeItemFactory.getFavor();

    onView(withId(R.id.title_request_view)).perform(typeText(favor.getTitle()));
    onView(withId(R.id.details)).perform(typeText(favor.getDescription()));
    //
    // Click on request button
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());
    onView(withText(R.string.set_location_no))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(1000);
    // Click on back button
    pressBack();
    getInstrumentation().waitForIdleSync();

    onView(withId(R.id.swipe_refresh_layout))
            .perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));

    // wait to refresh
    Thread.sleep(2000);

    // check favor is displayed in active favor list view
    onView(withText(favor.getTitle())).check(matches(isDisplayed()));
  }

  @Test
  public void testItemMenu() throws InterruptedException {
    onView(withId(R.id.nav_favorList)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(2000);

    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    Favor favor = FakeItemFactory.getFavor();
    onView(withId(R.id.title_request_view)).perform(typeText(favor.getTitle()));
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());
    onView(withText(R.string.set_location_no)).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(1000);

    pressBack(); getInstrumentation().waitForIdleSync();

    onView(withId(R.id.swipe_refresh_layout)).perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));
    getInstrumentation().waitForIdleSync();

    // Check the following elements are displayed
    onView(withId(R.id.item_title)).check(matches(isDisplayed()));
    onView(withId(R.id.item_requester)).check(matches(isDisplayed()));
    onView(withId(R.id.item_coins)).check(matches(isDisplayed()));
    onView(withId(R.id.item_menu_btn)).check(matches(isDisplayed())).perform(click());

    onView(withText(R.string.view)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();
    pressBack(); getInstrumentation().waitForIdleSync();

    onView(withId(R.id.item_menu_btn)).check(matches(isDisplayed())).perform(click());
    onView(withText(R.string.cancel)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    onView(withId(R.id.swipe_refresh_layout)).perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));
    onView(withId(R.id.tip)).check(matches(isDisplayed()));
  }


  @Test
  public void testFavorCancelUpdatesActiveAndArchivedListView() throws InterruptedException {
    // Click on favors tab
    onView(withId(R.id.nav_favorList)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Click on new favor tab
    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Fill in text views with fake favor
    Favor favor = FakeItemFactory.getFavor();

    onView(withId(R.id.title_request_view)).perform(typeText(favor.getTitle()));
    onView(withId(R.id.details)).perform(typeText(favor.getDescription()));

    // Click on request button
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());
    onView(withText(R.string.set_location_no))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click());
    Thread.sleep(4000); // wait for snackbar to hide

    // Click on cancel button
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    getInstrumentation().waitForIdleSync();
    onView(withText(R.string.cancel_request)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Go back
    pressBack();

    onView(withId(R.id.swipe_refresh_layout))
            .perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));

    Thread.sleep(2000);

    // Check favor is not displayed in active list
    onView(withText(favor.getTitle())).check(doesNotExist());

    // go to archived list
    onView(withId(R.id.archived_toggle)).perform(click());
    getInstrumentation().waitForIdleSync();

    onView(withId(R.id.swipe_refresh_layout))
            .perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));

    Thread.sleep(2000);

    // check favor is displayed in archived favor list view
    onView(withText(favor.getTitle())).check(matches(isDisplayed()));
  }

  @Test
  public void testDeletedFavorUpdatesListView() throws InterruptedException {
    // Click on favors tab
    onView(withId(R.id.nav_favorList)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Click on new favor tab
    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Fill in text views with fake favor
    Favor favor = FakeItemFactory.getFavor();

    onView(withId(R.id.title_request_view)).perform(typeText(favor.getTitle()));
    onView(withId(R.id.details)).perform(typeText(favor.getDescription()));

    // Click on request button
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());
    onView(withText(R.string.set_location_no))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(1000); // wait for snackbar to hide

    // Click on cancel button
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    onView(withText(R.string.cancel_request)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(1000);

    // Click on delete button
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    Thread.sleep(1000);

    onView(withText(R.string.delete_favor))
            .check(matches(withText(R.string.delete_favor)))
            .perform(click());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(1000);

    onView(withId(R.id.swipe_refresh_layout))
            .perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));

    Thread.sleep(1000);

    // Check favor is not displayed in active list
    onView(withText(favor.getTitle())).check(doesNotExist());

    // go to archived list
    onView(withId(R.id.archived_toggle)).perform(click());
    getInstrumentation().waitForIdleSync();

    onView(withId(R.id.swipe_refresh_layout))
            .perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));

    Thread.sleep(1000);

    // check favor is displayed in archived favor list view
    onView(withText(favor.getTitle())).check(doesNotExist());
  }

  @Test
  public void testSearchViewFound() throws InterruptedException {

    requestFavorAndSearch();

    Favor favor = FakeItemFactory.getFavor();

    onView(isAssignableFrom(EditText.class)).perform(typeText(favor.getTitle()));
    Thread.sleep(3000);

    getInstrumentation().waitForIdleSync();

    onView(withId(R.id.swipe_refresh_layout))
            .perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));
    getInstrumentation().waitForIdleSync();
    Thread.sleep(1000);
    // check query is successful and click on found item
    onView(withId(R.id.tip)).check(matches(not(isDisplayed())));
    getInstrumentation().waitForIdleSync();

    // Click on back button twice
    pressBack();
    pressBack();
    getInstrumentation().waitForIdleSync();
  }

  @Test
  public void testSearchViewNotFound() throws InterruptedException {

    requestFavorAndSearch();

    // type the title of fake favor
    onView(isAssignableFrom(EditText.class))
            .perform(typeText("random words"), pressImeActionButton());

    Thread.sleep(2000);

    // check the tip text is displayed when query failed
    onView(withId(R.id.tip))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.query_failed)));
  }

  @Test
  public void testClickScreenHideKeyboard() throws InterruptedException {
    requestFavorAndSearch();
    getInstrumentation().waitForIdleSync();
    Thread.sleep(1000);

    // Click on upper left screen corner
    UiDevice device = UiDevice.getInstance(getInstrumentation());
    device.click(device.getDisplayWidth() / 2, device.getDisplayHeight() / 2);
    getInstrumentation().waitForIdleSync();
    Thread.sleep(1000);
    // if keyboard hidden, one time of pressBack will return to Favor List view
    onView(withId(R.id.hamburger_menu_button)).check(matches(isDisplayed())).perform(click());
    Favor favor = FakeItemFactory.getFavor();
    getInstrumentation().waitForIdleSync();
    Thread.sleep(1000);

    // check favor is displayed in active favor list view
    onView(withText(favor.getTitle())).check(matches(isDisplayed()));
  }

  private void requestFavorAndSearch() throws InterruptedException {
    // Click on favors tab
    onView(withId(R.id.nav_favorList)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Click on new favor tab
    onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    // Fill in text views with fake favor
    Favor favor = FakeItemFactory.getFavor();

    onView(withId(R.id.title_request_view)).perform(typeText(favor.getTitle()));
    onView(withId(R.id.details)).perform(typeText(favor.getTitle()));

    // Click on fragment_favor_published_view button
    onView(withId(R.id.request_button)).check(matches(isDisplayed())).perform(click());
    onView(withText(R.string.set_location_no))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(1000);
    // Click on back button
    pressBack();
    getInstrumentation().waitForIdleSync();

    // Click on searchView button
    onView(withId(R.id.search_item)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    Thread.sleep(2000);

    // check item is displayed
    onView(withText(favor.getTitle())).check(matches(isDisplayed()));
  }
}