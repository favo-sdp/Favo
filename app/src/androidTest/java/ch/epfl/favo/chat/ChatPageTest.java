package ch.epfl.favo.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import ch.epfl.favo.FakeChatUtil;
import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.FakePictureUtil;
import ch.epfl.favo.FakeUserUtil;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.TestConstants;
import ch.epfl.favo.TestUtils;
import ch.epfl.favo.cache.CacheUtil;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.user.User;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.MockDatabaseWrapper;
import ch.epfl.favo.view.MockGpsTracker;

import static android.app.Activity.RESULT_OK;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static ch.epfl.favo.TestUtils.childAtPosition;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.mockito.ArgumentMatchers.anyDouble;

public class ChatPageTest {

  private MockDatabaseWrapper mockDatabaseWrapper = new MockDatabaseWrapper<User>();

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
    FavorUtil.getSingleInstance()
        .updateCollectionWrapper(
            DependencyFactory.getCurrentCollectionWrapper(
                TestConstants.TEST_COLLECTION, Favor.class));
    DependencyFactory.setCurrentUserRepository(new FakeUserUtil());
    DependencyFactory.setCurrentPictureUtility(new FakePictureUtil());
  }

  @After
  public void tearDown() throws ExecutionException, InterruptedException {
    TestUtils.cleanupDatabase();
    DependencyFactory.setCurrentFirebaseUser(null);
    DependencyFactory.setCurrentGpsTracker(null);
    DependencyFactory.setCurrentCollectionWrapper(null);
    FavorUtil.getSingleInstance()
        .updateCollectionWrapper(
            DependencyFactory.getCurrentCollectionWrapper("favors", Favor.class));
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
    onView(withText(R.string.set_location_no))
        .inRoot(isDialog())
        .check(matches(isDisplayed()))
        .perform(click());
    getInstrumentation().waitForIdleSync();

    // wait for snackbar
    Thread.sleep(5000);

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
    Thread.sleep(1000);
    // Fill in text views with fake message
    onView(withId(R.id.messageEdit)).perform(typeText(message));
  }

  @Test
  public void testSendMessageWithSendButton() throws InterruptedException {
    String message = "Fake message";
    typeMessage(message);

    // send message
    onView(withId(R.id.sendButton)).perform(click());

    Thread.sleep(1000);

    // check message is displayed
    onView(withText(message)).check(matches(isDisplayed()));
  }

  @Test
  public void testSendMessageWithKeyboard() throws InterruptedException {
    String message = "Fake message";
    typeMessage(message);

    onView(withId(R.id.messageEdit)).perform(pressImeActionButton());

    Thread.sleep(1000);

    // check message is displayed
    onView(withText(message)).check(matches(isDisplayed()));
    getInstrumentation().waitForIdleSync();
  }

  @Test
  public void testClickScreenHideKeyboard() throws InterruptedException {

    String message = "Fake message";
    typeMessage(message);

    // Click on upper left screen corner
    UiDevice device = UiDevice.getInstance(getInstrumentation());
    device.click(device.getDisplayWidth() / 2, device.getDisplayHeight() / 2);
    onView(withText(message)).check(matches(isDisplayed()));
  }

  @Test
  public void testClickOnMessageNavigateToUserInfoPage() throws InterruptedException {

    String message = "l";
    typeMessage(message);
    Thread.sleep(1000);
    onView(withId(R.id.messageEdit)).perform(pressImeActionButton());
    getInstrumentation().waitForIdleSync();
    Thread.sleep(1000);
    User testUser =
        new User(
            TestConstants.USER_ID,
            TestConstants.NAME,
            TestConstants.EMAIL,
            TestConstants.DEVICE_ID,
            null,
            null);

    mockDatabaseWrapper.setMockDocument(testUser);
    mockDatabaseWrapper.setMockResult(testUser);
    UserUtil.getSingleInstance().updateCollectionWrapper(mockDatabaseWrapper);
    Thread.sleep(3000);
    ViewInteraction recyclerView =
        onView(
            allOf(
                withId(R.id.messagesList),
                childAtPosition(withClassName(is("android.widget.RelativeLayout")), 1)));

    recyclerView.perform(actionOnItemAtPosition(0, click()));
    getInstrumentation().waitForIdleSync();
    Thread.sleep(3000);
    onView(withId(R.id.user_info_fragment)).check(matches(isDisplayed()));
  }

  @Test
  public void testShareImage() throws Throwable {
    FakeChatUtil fakeChatUtil = new FakeChatUtil();
    DependencyFactory.setCurrentChatUtility(fakeChatUtil);
    navigateToChatPage();
    Bitmap bm = Bitmap.createBitmap(200, 100, Bitmap.Config.RGB_565);

    getInstrumentation().waitForIdleSync();
    ChatPage chatPage = getChatPageReference();
    Uri filePath = saveMockPicture(bm, chatPage);
    Intent pictureIntent = new Intent();
    pictureIntent.setData(filePath);

    runOnUiThread(() -> chatPage.onActivityResult(1, RESULT_OK, pictureIntent));
    fakeChatUtil.setThrowsError(new RuntimeException());
    runOnUiThread(() -> chatPage.onActivityResult(1, RESULT_OK, pictureIntent));
    getInstrumentation().waitForIdleSync();
    Thread.sleep(1000);
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.error_send_msg_chat)));
    DependencyFactory.setCurrentChatUtility(null);
  }

  @Test
  public void testShareMyCurrentLocation() throws InterruptedException {
    ChatUtil spyChatUtil = Mockito.spy(ChatUtil.getSingleInstance());
    DependencyFactory.setCurrentChatUtility(spyChatUtil);
    navigateToChatPage();
    Bitmap mockMap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);
    ChatPage chatPage = getChatPageReference();
    Uri filePath = saveMockPicture(mockMap, chatPage);
    Mockito.doReturn(filePath.toString())
        .when(spyChatUtil)
        .generateGoogleMapsPath(anyDouble(), anyDouble());
    // share current location
    onView(withId(R.id.share_location_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();
    onView(withText(R.string.share_location_current))
        .check(matches(isDisplayed()))
        .perform(click());
    Thread.sleep(3000);
    onView(withId(R.id.chat_msg_image)).check(matches(isDisplayed()));
    DependencyFactory.setCurrentChatUtility(null);
  }

  @Test
  public void testShareMapLocation() throws InterruptedException {
    navigateToChatPage();
    // share current location
    onView(withId(R.id.share_location_button)).check(matches(isDisplayed())).perform(click());
    getInstrumentation().waitForIdleSync();

    onView(withText(R.string.share_location_propose))
        .check(matches(isDisplayed()))
        .perform(click());
    getInstrumentation().waitForIdleSync();
    onView(withId(R.id.fragment_map)).check(matches(isDisplayed()));
    pressBack();
  }

  private Uri saveMockPicture(Bitmap bm, ChatPage chatPage) {
    Uri path =
        CacheUtil.getInstance()
            .saveToInternalStorage(
                Objects.requireNonNull(chatPage.getContext()), bm, "testImage", 0);
    return Uri.fromFile(new File(path.toString()));
  }

  private ChatPage getChatPageReference() {
    Navigation.findNavController(mainActivityTestRule.getActivity(), R.id.nav_host_fragment);
    Fragment navHostFragment =
        mainActivityTestRule
            .getActivity()
            .getSupportFragmentManager()
            .findFragmentById(R.id.nav_host_fragment);
    getInstrumentation().waitForIdleSync();
    return (ChatPage) navHostFragment.getChildFragmentManager().getFragments().get(0);
  }
}
