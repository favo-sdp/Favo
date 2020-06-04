package ch.epfl.favo.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;

import com.google.firebase.firestore.DocumentReference;

import org.hamcrest.Matchers;
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
import ch.epfl.favo.exception.IllegalAcceptException;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.user.User;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.tabs.addFavor.FavorPublishedView;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class FavorPublishedViewTest {
  private Favor fakeFavor;
  private FakeViewModel fakeViewModel;
  private MockDatabaseWrapper mockDatabaseWrapper = new MockDatabaseWrapper<User>();

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
    DependencyFactory.setCurrentCollectionWrapper(null);
    DependencyFactory.setCurrentUserRepository(null);
  }

  public FavorPublishedView launchFragment(Favor favor) throws Throwable {

    MainActivity activity = mainActivityTestRule.getActivity();
    NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
    Bundle bundle = new Bundle();
    bundle.putString(CommonTools.FAVOR_ARGS, favor.getId());
    runOnUiThread(() -> navController.navigate(R.id.action_nav_map_to_favorPublishedView, bundle));
    Fragment navHostFragment =
        activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
    getInstrumentation().waitForIdleSync();
    return (FavorPublishedView) navHostFragment.getChildFragmentManager().getFragments().get(0);
  }

  @Before
  public void setup() throws Throwable {
    fakeFavor = FakeItemFactory.getFavor();
    fakeViewModel = (FakeViewModel) launchFragment(fakeFavor).getViewModel();
  }

  @Test
  public void testSnackbarShowsWhenFavorCannotBeFetchedFromDatabase() throws Throwable {

    Favor failedFavor = Mockito.mock(Favor.class);
    Mockito.doThrow(new RuntimeException()).when(failedFavor).getTitle();
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(failedFavor));
    getInstrumentation().waitForIdleSync();

    // check error message is printed
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.error_database_sync)));
  }

  @Test
  public void testChatAndLocationButtonWorkDetailView() {
    // Check and click on the chat
    onView(withId(R.id.chat_button)).check(matches(isDisplayed())).perform(click());
    onView(withId(R.id.fragment_chat)).check(matches(isDisplayed()));

    // Go back to favor detail page
    pressBack();

    // Check and click on the location button
    onView(withId(R.id.location)).check(matches(isDisplayed())).perform(click());
    onView(withId(R.id.fragment_map)).check(matches(isDisplayed()));
  }

  @Test
  public void testCommitButtonShowsFailSnackBar() throws Throwable {
    // check that detailed view is indeed opened
    onView(allOf(withId(R.id.fragment_favor_published), withParent(withId(R.id.nav_host_fragment))))
        .check(matches(isDisplayed()));
    checkRequestedView();

    runOnUiThread(() -> fakeViewModel.setThrowError(new RuntimeException()));
    onView(withId(R.id.commit_complete_button)).perform(click());
    Thread.sleep(500);
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.update_favor_error)));
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
    checkToolbar(FavorStatus.ACCEPTED_BY_OTHER.toString());
  }

  @Test
  public void testOnShareFavorClicked() throws Throwable {
    // click accept button
    // Thread.sleep(2000);
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    getInstrumentation().waitForIdleSync();

    onView(withText(R.string.invite_text)).perform(click());
    getInstrumentation().waitForIdleSync();

    // check that share intent is indeed opened
    onView(allOf(withId(android.R.id.title), withText("Share"), isDisplayed()));

    // click back button
    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    mDevice.pressBack();
  }

  @Test
  public void testCanReportFavor() throws Throwable {

    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(1000);
    onView(withText(R.string.report_favor_text)).check(matches(isDisplayed())).perform(click());
  }

  @Test
  public void testAcceptFlow() throws Throwable {
    // click accept button
    // Thread.sleep(2000);
    onView(withId(R.id.commit_complete_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.favor_respond_success_msg)));
    onView(withId(R.id.commit_complete_button)).check(matches(not(isDisplayed())));
    onView(withId(R.id.chat_button)).check(matches(isDisplayed()));
    checkToolbar(FavorStatus.REQUESTED.toString());

    fakeFavor = FakeItemFactory.getFavor();
    fakeFavor.setAccepterId(DependencyFactory.getCurrentFirebaseUser().getUid());
    fakeFavor.setStatusIdToInt(FavorStatus.ACCEPTED);
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(fakeFavor));

    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.commit_complete_button))
        .check(matches(allOf(isDisplayed(), withText(R.string.complete_favor))));
    onView(withId(R.id.chat_button)).check(matches(isDisplayed()));
    checkToolbar(FavorStatus.ACCEPTED.toString());
  }

  @Test
  public void testCancelFlowByAccepter() throws Throwable {
    // favor is cancelled by accepter
    fakeFavor.setStatusIdToInt(FavorStatus.ACCEPTED);
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(fakeFavor));
    openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
    getInstrumentation().waitForIdleSync();
    onView(withText(R.string.cancel_request)).check(matches(isDisplayed())).perform(click());
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
    UserUtil.getSingleInstance().updateCollectionWrapper(mockDatabaseWrapper);
    // accept favor
    fakeFavor.setStatusIdToInt(FavorStatus.ACCEPTED);
    fakeFavor.setAccepterId(TestConstants.USER_ID);
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(fakeFavor));
    checkCompletedOrAcceptedView(FavorStatus.ACCEPTED);
    // complete firstly by accepter
    onView(withId(R.id.commit_complete_button))
        .check(matches(withText(R.string.complete_favor)))
        .perform(click());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(500);
    //    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.favor_complete_success_msg)));
    checkCompletedOrAcceptedView(FavorStatus.COMPLETED_ACCEPTER);
  }

  @Test
  public void testSuccessfullyCompleteFlow() throws Throwable {
    fakeFavor.setStatusIdToInt(FavorStatus.COMPLETED_REQUESTER);
    fakeFavor.setAccepterId(DependencyFactory.getCurrentFirebaseUser().getUid());
    runOnUiThread(() -> fakeViewModel.setObservedFavorResult(fakeFavor));

    checkCompletedOrAcceptedView(FavorStatus.COMPLETED_REQUESTER);
    Thread.sleep(2000);
    onView(withId(R.id.commit_complete_button))
        .check(matches(withText(R.string.complete_favor)))
        .perform(click());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(2000);
    checkCompletedSuccessfullyView();
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.favor_complete_success_msg)));
  }

  @Test
  public void testClickOnRequesterTextNavigateToUserInfoPage_NoUserAccountFound() {

    DocumentReference documentReference = Mockito.mock(DocumentReference.class);
    // TODO: mock failed task
    // Mockito.doReturn(new Task<>() {
    //  }).when(documentReference).get();
    mockDatabaseWrapper.setMockDocumentReference(documentReference);
    UserUtil.getSingleInstance().updateCollectionWrapper((mockDatabaseWrapper));
    // onView(withId(R.id.user_name_published_view)).perform(click());
    onView(withId(R.id.fragment_favor_published)).check(matches(isDisplayed()));
  }

  @Test
  public void testFavorIsNotAcceptedIfSurpassedLimit() throws Throwable {
    fakeViewModel.numActiveAcceptedFavor = 1;
    onView(withId(R.id.commit_complete_button)).perform(click());
    getInstrumentation().waitForIdleSync();
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.illegal_accept_error)));
    // Try clicking again
    Thread.sleep(1000);
    onView(withId(R.id.commit_complete_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.illegal_accept_error)));
  }

  // removing this test because favors in the second tab will concern the user directly and it's not
  // possible to accept a favor from there anymore
  public void checkToolbar(String expectedDisplay) {
    onView(withId(R.id.toolbar_main_activity))
        .check(matches(hasDescendant(withText(expectedDisplay))));
  }

  public void checkRequestedView() {
    // Check edit button is there
    onView(withId(R.id.chat_button)).check(matches(isDisplayed()));
    // Check commit/complete button is there
    onView(withId(R.id.commit_complete_button))
        .check(matches(allOf(isEnabled(), withText(R.string.commit_favor))));
    // Check status display is correct
    checkToolbar(FavorStatus.REQUESTED.toString());
  }

  public void checkCompletedSuccessfullyView() {
    onView(withId(R.id.commit_complete_button)).check(matches(not(isDisplayed())));
    onView(withId(R.id.chat_button)).check(matches(not(isDisplayed())));
    checkToolbar(FavorStatus.SUCCESSFULLY_COMPLETED.toString());
  }

  public void checkCompletedOrAcceptedView(FavorStatus status) {
    if (status == FavorStatus.COMPLETED_REQUESTER || status == FavorStatus.ACCEPTED)
      onView(withId(R.id.commit_complete_button))
          .check(matches(Matchers.allOf(isDisplayed(), withText(R.string.complete_favor))));
    else // if completed by accepter, complete button is changed to non-clickable waiting button
    onView(withId(R.id.commit_complete_button)).check(matches(not(isDisplayed())));
    onView(withId(R.id.chat_button)).check(matches(isDisplayed()));
    checkToolbar(status.toString());
  }

  public void checkCancelledView(FavorStatus status) {
    onView(withId(R.id.chat_button)).check(matches(not(isDisplayed())));
    // Check cancel button is not clickable
    onView(withId(R.id.commit_complete_button)).check(matches(not(isDisplayed())));
    // Check updated status string
    checkToolbar(status.toString());
  }
}
