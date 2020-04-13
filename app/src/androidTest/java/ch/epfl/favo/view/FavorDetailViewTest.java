package ch.epfl.favo.view;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.FavorFragmentFactory;

import static androidx.navigation.Navigation.findNavController;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class FavorDetailViewTest {
  private Favor fakeFavor;
  private MockDatabaseWrapper mockDatabaseWrapper = new MockDatabaseWrapper<Favor>();
  private NavController navController;

  @Rule
  public final ActivityTestRule<MainActivity> mainActivityTestRule =
      new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
          DependencyFactory.setCurrentFirebaseUser(
              new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
          DependencyFactory.setCurrentCollectionWrapper(mockDatabaseWrapper);
        }
      };

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

  @Before
  public void setUp() {
    fakeFavor = FakeItemFactory.getFavor();
    UserUtil.currentUserId = "USER";
    navController = findNavController(mainActivityTestRule.getActivity(), R.id.nav_host_fragment);
    Bundle bundle = new Bundle();
    bundle.putParcelable(FavorFragmentFactory.FAVOR_ARGS, fakeFavor);
    navController.navigate(R.id.action_global_favorDetailView, bundle);
  }

  @After
  public void tearDown() {
    DependencyFactory.setCurrentFirebaseUser(null);
    DependencyFactory.setCurrentCollectionWrapper(null);
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
  public void testAcceptButtonShowsSnackBarAndUpdatesDisplay() {
    CompletableFuture successfulResult = new CompletableFuture();
    successfulResult.complete(null);
    mockDatabaseWrapper.setMockDocument(fakeFavor); // set favor in db
    mockDatabaseWrapper.setMockResult(successfulResult);
    FavorUtil.getSingleInstance().updateCollectionWrapper(mockDatabaseWrapper);
    onView(withId(R.id.accept_button)).perform(click());
    getInstrumentation().waitForIdleSync();
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.favor_respond_success_msg)));
    onView(withId(R.id.status_text_accept_view))
        .check(matches(withText(Favor.Status.ACCEPTED.getPrettyString())));
  }

  @Test
  public void testAcceptButtonShowsFailSnackBar() throws InterruptedException {
    CompletableFuture failedResult = new CompletableFuture();
    failedResult.completeExceptionally(new RuntimeException());
    // mockDatabaseWrapper.setMockDocument(fakeFavor); // set favor in db
    mockDatabaseWrapper.setMockResult(failedResult);
    FavorUtil.getSingleInstance().updateCollectionWrapper(mockDatabaseWrapper);
    onView(withId(R.id.accept_button)).perform(click());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(500);
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.update_favor_error)));
  }

  @Test
  public void testFavorFailsToBeAcceptedIfPreviouslyAccepted() {
    onView(withId(R.id.accept_button))
        .check(matches(isDisplayed()))
        .check(matches(withText(R.string.accept_favor)));
    // Another user accepts favor
    Favor anotherFavorWithSameId = FakeItemFactory.getFavor();
    anotherFavorWithSameId.setStatusId(Favor.Status.ACCEPTED);
    mockDatabaseWrapper.setMockDocument(anotherFavorWithSameId);
    FavorUtil.getSingleInstance().updateCollectionWrapper(mockDatabaseWrapper);
    onView(withId(R.id.accept_button)).perform(click());
    getInstrumentation().waitForIdleSync();
    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.favor_remotely_changed_msg)));
    // check update text matches Accepted by other
    onView(withId(R.id.status_text_accept_view))
        .check(matches(withText(Favor.Status.ACCEPTED_BY_OTHER.getPrettyString())));
  }

  @Test
  public void testFavorCanBeCancelled() throws InterruptedException {
    mockDatabaseWrapper.setMockDocument(fakeFavor);
    CompletableFuture successfulResult = new CompletableFuture();
    successfulResult.complete(null);
    mockDatabaseWrapper.setMockResult(successfulResult);
    FavorUtil.getSingleInstance().updateCollectionWrapper(mockDatabaseWrapper);
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.accept_button)).perform(click());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(500);
    onView(withId(R.id.accept_button))
        .check(matches(withText(R.string.cancel_accept_button_display)))
        .perform(click());
    getInstrumentation().waitForIdleSync();
    // check display is updated
    onView(withId(R.id.status_text_accept_view))
        .check(matches(withText(Favor.Status.CANCELLED_ACCEPTER.getPrettyString())));

    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.favor_cancel_success_msg)));
    getInstrumentation().waitForIdleSync();
  }

  @Test
  public void testFavorShowsFailureSnackbarIfCancelFails() throws InterruptedException {
    mockDatabaseWrapper.setMockDocument(fakeFavor);
    mockDatabaseWrapper.setThrowError(false);
    FavorUtil.getSingleInstance().updateCollectionWrapper(mockDatabaseWrapper);
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.accept_button)).perform(click());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(500);
    // now inject throwable to see reaction in the UI
    mockDatabaseWrapper.setThrowError(true);
    FavorUtil.getSingleInstance().updateCollectionWrapper(mockDatabaseWrapper);
    onView(withId(R.id.accept_button))
        .check(matches(withText(R.string.cancel_accept_button_display)))
        .perform(click());
    getInstrumentation().waitForIdleSync();

    // check snackbar shows
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.update_favor_error)));
    getInstrumentation().waitForIdleSync();
    // check display is updated
    onView(withId(R.id.status_text_accept_view))
        .check(matches(withText(Favor.Status.ACCEPTED.getPrettyString())));
  }

  @Test
  public void testAcceptingFavorUpdatesListView() {
    mockDatabaseWrapper.setMockDocument(fakeFavor);
    mockDatabaseWrapper.setThrowError(false);
    FavorUtil.getSingleInstance().updateCollectionWrapper(mockDatabaseWrapper);
    // navigate to list view from main activity
    onView(withId(R.id.accept_button)).check(matches(isDisplayed())).perform(click());
    // press back
    pressBack();
    getInstrumentation().waitForIdleSync();
    navController.navigate(R.id.action_nav_map_to_nav_favorlist);
    onView(withText(fakeFavor.getTitle())).check(matches(isDisplayed())).perform(click());

    getInstrumentation().waitForIdleSync();
    onView(
            allOf(
                withId(R.id.fragment_favor_accept_view),
                withParent(withId(R.id.nav_host_fragment))))
        .check(matches(isDisplayed()));
  }
}
