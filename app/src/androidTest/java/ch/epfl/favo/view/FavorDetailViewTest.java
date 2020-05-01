package ch.epfl.favo.view;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.FakeViewModel;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.TestConstants;
import ch.epfl.favo.common.IllegalRequestException;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.user.User;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.tabs.addFavor.FavorDetailView;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class FavorDetailViewTest {
  private Favor fakeFavor;
  private FavorDetailView detailViewFragment;
  private FakeViewModel fakeViewModel;
  private MockDatabaseWrapper mockDatabaseWrapper = new MockDatabaseWrapper<User>();

  @Rule
  public final ActivityTestRule<MainActivity> mainActivityTestRule =
      new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
          Log.d("pasS", "FavorDetailView test");
          DependencyFactory.setCurrentFirebaseUser(
              new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
          DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
          DependencyFactory.setCurrentViewModelClass(FakeViewModel.class);
        }
      };

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

  @After
  public void tearDown() {
    DependencyFactory.setCurrentGpsTracker(null);
    DependencyFactory.setCurrentFirebaseUser(null);
    DependencyFactory.setCurrentViewModelClass(null);
    DependencyFactory.setCurrentCollectionWrapper(null);
  }

  public FavorDetailView launchFragment(Favor favor) throws Throwable {

    MainActivity activity = mainActivityTestRule.getActivity();
    NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
    Bundle bundle = new Bundle();
    bundle.putString(CommonTools.FAVOR_ARGS, favor.getId());
    runOnUiThread(() -> navController.navigate(R.id.action_nav_map_to_favorDetailView, bundle));
    Fragment navHostFragment =
        activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
    getInstrumentation().waitForIdleSync();
    return (FavorDetailView) navHostFragment.getChildFragmentManager().getFragments().get(0);
  }

  @Before
  public void setup() throws Throwable {
    fakeFavor = FakeItemFactory.getFavor();
    detailViewFragment = launchFragment(fakeFavor);
    fakeViewModel = (FakeViewModel) detailViewFragment.getViewModel();
  }

  @Test
  public void favorDetailViewIsLaunched() {
    // check that detailed view is indeed opened

    onView(
            allOf(
                withId(R.id.fragment_favor_accept_view),
                withParent(withId(R.id.nav_host_fragment))))
        .check(matches(isDisplayed()));
  }

  @Test
  public void testAcceptButtonShowsSnackBarAndUpdatesDisplay() throws Throwable {
    onView(withId(R.id.accept_button)).perform(click());
    Log.d("pasS", "during Detail Test 4");
    getInstrumentation().waitForIdleSync();
    Log.d("pasS", "during Detail Test 5");
    checkToolbar(FavorStatus.ACCEPTED.toString());
    Log.d("pasS", "during Detail Test 6");
    Thread.sleep(500);
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.favor_respond_success_msg)));
  }

  @Test
  public void testAcceptButtonShowsFailSnackBar() throws Throwable {
    runOnUiThread(() -> fakeViewModel.setThrowError(new RuntimeException()));
    onView(withId(R.id.accept_button)).perform(click());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(500);
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.update_favor_error)));
  }

  @Test
  public void testFavorFailsToBeAcceptedIfPreviouslyAccepted() throws Throwable {
    onView(withId(R.id.accept_button))
        .check(matches(isDisplayed()))
        .check(matches(withText(R.string.accept_favor)));
    // Another user accepts favor
    Favor anotherFavorWithSameId = FakeItemFactory.getFavor();
    anotherFavorWithSameId.setStatusIdToInt(FavorStatus.ACCEPTED);
    anotherFavorWithSameId.setAccepterId("another user");
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(anotherFavorWithSameId));
    getInstrumentation().waitForIdleSync();
    // check update text matches Accepted by other
    checkToolbar(FavorStatus.ACCEPTED_BY_OTHER.toString());
  }

  @Test
  public void testFavorFailsToBeAcceptedIfPreviouslyCancelled() throws Throwable {
    onView(withId(R.id.accept_button))
        .check(matches(isDisplayed()))
        .check(matches(withText(R.string.accept_favor)));
    // Another user accepts favor
    Favor anotherFavorWithSameId = FakeItemFactory.getFavor();
    anotherFavorWithSameId.setStatusIdToInt(FavorStatus.CANCELLED_REQUESTER);
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(anotherFavorWithSameId));
    getInstrumentation().waitForIdleSync();
    // check update text matches Accepted by other
    checkToolbar(FavorStatus.CANCELLED_REQUESTER.toString());
  }

  @Test
  public void testFavorCanBeCancelled() throws InterruptedException {
    onView(withId(R.id.accept_button)).perform(click());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(500); // wait for snackbar
    onView(withId(R.id.accept_button))
        .check(matches(withText(R.string.cancel_accept_button_display)))
        .perform(click());
    getInstrumentation().waitForIdleSync();
    // check display is updated
    checkToolbar(FavorStatus.CANCELLED_ACCEPTER.toString());

    Thread.sleep(500);
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.favor_cancel_success_msg)));
    getInstrumentation().waitForIdleSync();
  }

  @Test
  public void testFavorShowsFailureSnackbarIfCancelFails() throws Throwable {
    Log.d("pasS", "failure test");
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.accept_button)).perform(click());
    getInstrumentation().waitForIdleSync();

    // now inject throwable to see reaction in the UI
    runOnUiThread(() -> fakeViewModel.setThrowError(new RuntimeException()));
    onView(withId(R.id.accept_button))
        .check(matches(withText(R.string.cancel_accept_button_display)))
        .perform(click());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(500);
    // check display is updated
    checkToolbar(FavorStatus.ACCEPTED.toString());

    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.update_favor_error)));
    getInstrumentation().waitForIdleSync();
  }

  @Test
  public void testFavorShowsFailureSnackbarIfDbCallbackFails() throws Throwable {

    // now inject throwable to see reaction in the UI
    Favor failedFavor = Mockito.mock(Favor.class);
    Mockito.doThrow(new RuntimeException()).when(failedFavor).getTitle();
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(failedFavor)); // invoke error
    Thread.sleep(500);
    // check display is updated

    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.error_database_sync)));
    getInstrumentation().waitForIdleSync();
  }

  @Test
  public void testSuccessfullyCompletedView() throws Throwable {
    fakeFavor.setStatusIdToInt(FavorStatus.SUCCESSFULLY_COMPLETED);
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(fakeFavor));
    String expectedDisplay = FavorStatus.SUCCESSFULLY_COMPLETED.toString();
    checkToolbar(expectedDisplay);
  }

  @Test
  public void testClickOnRequesterTextNavigateToUserInfoPage() throws InterruptedException {
    User testUser =
        new User(
            TestConstants.USER_ID,
            TestConstants.NAME,
            TestConstants.EMAIL,
            TestConstants.DEVICE_ID,
            null,
            null);

    DependencyFactory.setCurrentCollectionWrapper(mockDatabaseWrapper);
    mockDatabaseWrapper.setMockDocument(testUser);
    mockDatabaseWrapper.setMockResult(testUser);

    onView(withId(R.id.requester_name)).perform(click());

    Thread.sleep(1000);

    onView(withId(R.id.user_info_fragment)).check(matches(isDisplayed()));
  }

  // removing this test because favors in the second tab will concern the user directly and it's not
  // possible to accept a favor from there anymore
  public void checkToolbar(String expectedDisplay) {
    onView(withId(R.id.toolbar_main_activity))
        .check(matches(hasDescendant(withText(expectedDisplay))));
  }

  @Test
  public void testFavorIsNotAcceptedIfSurpassedLimit() throws Throwable {
    runOnUiThread(
        () -> fakeViewModel.setThrowError(new IllegalRequestException("illegal operation!")));
    onView(withId(R.id.accept_button)).perform(click());
    getInstrumentation().waitForIdleSync();
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.illegal_accept_error)));
  }
}
