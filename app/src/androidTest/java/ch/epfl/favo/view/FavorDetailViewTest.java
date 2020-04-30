package ch.epfl.favo.view;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.FakeViewModel;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.tabs.addFavor.FavorDetailView;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static ch.epfl.favo.util.FavorFragmentFactory.FAVOR_ARGS;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class FavorDetailViewTest {
  private Favor fakeFavor;
  private FavorDetailView detailViewFragment;
  private FakeViewModel fakeViewModel;

  @Rule
  public final ActivityTestRule<MainActivity> mainActivityTestRule =
      new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
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
  }

  public FavorDetailView launchFragment(Favor favor) throws Throwable {

    MainActivity activity = mainActivityTestRule.getActivity();
    NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
    Bundle bundle = new Bundle();
    bundle.putString(FAVOR_ARGS, favor.getId());
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
  public void testAcceptButtonShowsFailSnackBar() throws Throwable {
    // check that detailed view is indeed opened
    onView(
            allOf(
                    withId(R.id.fragment_favor_accept_view),
                    withParent(withId(R.id.nav_host_fragment))))
            .check(matches(isDisplayed()));
    checkRequestedView();

    runOnUiThread(() -> fakeViewModel.setThrowError(true));
    onView(withId(R.id.accept_button)).perform(click());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(500);
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.update_favor_error)));
  }

  @Test
  public void testFavorShowsFailureSnackbarIfCancelFails() throws Throwable {
    onView(withId(R.id.accept_button)).perform(click());
    getInstrumentation().waitForIdleSync();
    checkCompletedOrAcceptedView(FavorStatus.ACCEPTED);

    // now inject throwable to see reaction in the UI
    runOnUiThread(() -> fakeViewModel.setThrowError(true));
    onView(withId(R.id.accept_button))
        .check(matches(withText(R.string.cancel_accept_button_display)))
        .perform(click());
    getInstrumentation().waitForIdleSync();

    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.update_favor_error)));
    getInstrumentation().waitForIdleSync();
  }

  @Test
  public void testFavorFailsToBeAcceptedIfPreviouslyAccepted() throws Throwable {
    // Another user accepts favor
    Favor anotherFavorWithSameId = FakeItemFactory.getFavor();
    anotherFavorWithSameId.setStatusIdToInt(FavorStatus.ACCEPTED);
    anotherFavorWithSameId.setAccepterId("another user");
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(anotherFavorWithSameId));
    getInstrumentation().waitForIdleSync();

    // check update text matches Accepted by other
    onView(withId(R.id.status_text_accept_view))
            .check(matches(withText(FavorStatus.ACCEPTED_BY_OTHER.toString())));
  }

  @Test
  public void testFavorShowsFailureSnackbarIfDbCallbackFails() throws Throwable {
    // now inject throwable to see reaction in the UI
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(null)); // invoke error
    Thread.sleep(500);

    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.error_database_sync)));
    getInstrumentation().waitForIdleSync();
  }

  @Test
  public void testAcceptFlow() {
    // click accept button
    onView(withId(R.id.accept_button)).perform(click());
    getInstrumentation().waitForIdleSync();

    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.favor_respond_success_msg)));
    checkCompletedOrAcceptedView(FavorStatus.ACCEPTED);
  }

  @Test
  public void testCancelFlowByAccepter() throws Throwable {
    // favor is cancelled by accepter
    onView(withId(R.id.accept_button)).perform(click());
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.accept_button))
            .check(matches(withText(R.string.cancel_accept_button_display)))
            .perform(click());
    getInstrumentation().waitForIdleSync();
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.favor_cancel_success_msg)));
    checkCancelledView(FavorStatus.CANCELLED_ACCEPTER);

    // If favor is cancelled by requester, show correct view
    fakeFavor.setStatusIdToInt(FavorStatus.CANCELLED_REQUESTER);
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(fakeFavor));
    getInstrumentation().waitForIdleSync();
    checkCancelledView(FavorStatus.CANCELLED_REQUESTER);
  }

  @Test
  public void testCompleteFlowByAccepter() throws Throwable {
    // accept favor
    onView(withId(R.id.accept_button)).perform(click());
    checkCompletedOrAcceptedView(FavorStatus.ACCEPTED);

    // complete firstly by accepter
    onView(withId(R.id.complete_btn)).perform(click());
    getInstrumentation().waitForIdleSync();
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.favor_complete_success_msg)));
    checkCompletedOrAcceptedView(FavorStatus.COMPLETED_ACCEPTER);
    Thread.sleep(2000);

    // If completed firstly by requester, then click complete button
    fakeFavor.setStatusIdToInt(FavorStatus.COMPLETED_REQUESTER);
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(fakeFavor));
    getInstrumentation().waitForIdleSync();
    checkCompletedOrAcceptedView(FavorStatus.COMPLETED_REQUESTER);
    onView(withId(R.id.complete_btn)).perform(click());
    getInstrumentation().waitForIdleSync();
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.favor_complete_success_msg)));
    checkCompletedSuccessfullyView();
  }

  public void checkRequestedView() {
    // Check edit button is there
    onView(withId(R.id.complete_btn)).check(matches(not(isDisplayed())));
    // Check cancel button is there
    onView(withId(R.id.accept_button)).check(matches(allOf(isEnabled(), withText(R.string.accept_favor))));
    // Check status display is correct
    onView(withId(R.id.status_text_accept_view))
            .check(matches(isDisplayed()))
            .check(matches(withText(FavorStatus.REQUESTED.toString())));
  }

  public void checkCompletedSuccessfullyView() {
    onView(withId(R.id.complete_btn)).check(matches(not(isDisplayed())));
    onView(withId(R.id.accept_button)).check(matches(allOf(not(isEnabled()), withText(R.string.cancel_accept_button_display))));
    onView(withId(R.id.status_text_accept_view))
            .check(matches(isDisplayed()))
            .check(matches(withText(FavorStatus.SUCCESSFULLY_COMPLETED.toString())));
  }

  public void checkCompletedOrAcceptedView(FavorStatus status) {
    if (status == FavorStatus.COMPLETED_REQUESTER || status == FavorStatus.ACCEPTED)
      onView(withId(R.id.complete_btn))
              .check(matches(Matchers.allOf(isDisplayed(), withText(R.string.complete_favor))));
    else // if completed by accepter, complete button is changed to non-clickable waiting button
      onView(withId(R.id.complete_btn))
              .check(matches(Matchers.allOf(isDisplayed(), withText(R.string.wait_complete))));
    onView(withId(R.id.accept_button)).check(matches(allOf(isEnabled(), withText(R.string.cancel_accept_button_display))));
    onView(withId(R.id.status_text_accept_view))
            .check(matches(isDisplayed()))
            .check(matches(withText(status.toString())));
  }

  public void checkCancelledView(FavorStatus status) {
    onView(withId(R.id.complete_btn)).check(matches(not(isDisplayed())));
    // Check cancel button is not clickable
    onView(withId(R.id.accept_button)).check(matches(allOf(not(isEnabled()), withText(R.string.cancel_accept_button_display))));
    // Check updated status string
    onView(withId(R.id.status_text_accept_view))
            .check(matches(isDisplayed()))
            .check(matches(withText(status.toString())));
  }
}
