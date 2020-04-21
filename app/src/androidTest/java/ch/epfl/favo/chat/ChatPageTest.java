package ch.epfl.favo.chat;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.TestConstants;
import ch.epfl.favo.TestUtils;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.MockGpsTracker;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;

public class ChatPageTest {

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
  }

  @After
  public void tearDown() throws ExecutionException, InterruptedException {
    TestUtils.cleanupDatabase();
    DependencyFactory.setCurrentFirebaseUser(null);
    DependencyFactory.setCurrentGpsTracker(null);
    DependencyFactory.setCurrentFavorCollection("favors");
  }

  private void navigateToChatPage() throws InterruptedException {
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
    getInstrumentation().waitForIdleSync();

    // wait for snackbar
    Thread.sleep(3000);

    // Click on chat button
    onView(withId(R.id.chat_button)).perform(click());
    getInstrumentation().waitForIdleSync();
  }

  @Test
  public void testChatPageLayout() throws InterruptedException {

    navigateToChatPage();

    // check title displayed
    onView(withText(FakeItemFactory.getFavor().getTitle())).check(matches(isDisplayed()));

    // check no messages displayed
    onView(withId(R.id.emptyTextView)).check(matches(isDisplayed()));

    // Go back to request page
    pressBack();

    // go back to list
    pressBack();

    getInstrumentation().waitForIdleSync();

    // Check we're back to favor page
    onView(withId(R.id.fragment_favors)).check(matches(isDisplayed()));
  }

  private void typeMessage(String message) throws InterruptedException {
    navigateToChatPage();

    // Fill in text views with fake message
    onView(withId(R.id.messageEdit)).perform(typeText(message));
  }

  @Test
  public void testSendMessageWithSendButton() throws InterruptedException {
    String message = "Fake message";
    typeMessage(message);

    // send message
    onView(withId(R.id.sendButton)).perform(click());

    // check message is displayed
    onView(withText(message)).check(matches(isDisplayed()));
  }

  @Test
  public void testSendMessageWithKeyboard() throws InterruptedException {
    String message = "Fake message";
    typeMessage(message);

    onView(withId(R.id.messageEdit)).perform(pressImeActionButton());

    // check message is displayed
    onView(withText(message)).check(matches(isDisplayed()));
  }

  @Test
  public void testClickScreenHideKeyboard() throws InterruptedException {

    String message = "Fake message";
    typeMessage(message);

    // Click on upper left screen corner
    UiDevice device = UiDevice.getInstance(getInstrumentation());
    device.click(device.getDisplayWidth() / 2, device.getDisplayHeight() / 2);
  }
}
