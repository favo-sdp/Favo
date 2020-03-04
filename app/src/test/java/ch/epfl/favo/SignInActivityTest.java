package ch.epfl.favo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import ch.epfl.favo.testhelpers.TestHelper;

import static android.app.Activity.RESULT_OK;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricTestRunner.class)
public class SignInActivityTest {

  private SignInActivity mock;

  @Before
  public void setup() {
    TestHelper.initialize();
    mock = Robolectric.setupActivity(SignInActivity.class);
  }

  @Test()
  public void testOnActivityResult_requestCodeCorrect() {
    mock.onActivityResult(123, RESULT_OK, null);
  }

  @Test
  public void testOnActivityResult_requestCodeNotCorrect() {
    mock.onActivityResult(4, RESULT_OK, null);
  }

  @Test
  public void testOnActivityResult_resultNotOk() {
    SignInActivity mock = spy(Robolectric.setupActivity(SignInActivity.class));
    Mockito.doNothing().when(mock).showSnackbar(anyInt());
    mock.onActivityResult(123, 10, null);
  }

  @Test
  public void testOnActivityResult_resultOk() {
    mock.onActivityResult(123, RESULT_OK, null);
  }

  @Test
  public void testOnActivityResult_resultWithNullResponse() {
    // when(mock.getIdpResponseFromIntent(any(Intent.class))).thenReturn(null);
    // Shadows.shadowOf(IdpResponse.fromResultIntent());
    mock.onActivityResult(123, RESULT_OK, null);
  }

  @Test
  public void testOnActivityResult_resultWithNotNullResponse() {
    mock.onActivityResult(123, RESULT_OK, null);
  }

  @Test
  public void testOnCreate_userLoggedIn() {

    //        FirebaseUser u = TestHelper.getMockFirebaseUser();
    //        //TestHelper.MOCK_APP.get(FirebaseAuth.class).updateCurrentUser(u);
    //
    //        when(TestHelper.MOCK_APP.get(FirebaseAuth.class).getCurrentUser()).thenReturn(u);
    //
    //        assertSame(FirebaseApp.getInstance(), TestHelper.MOCK_APP);
    //
    //        //TestHelper.MOCK_APP.get(FirebaseAuth.class).updateCurrentUser(u);
    //
    //        //assertSame(TestHelper.MOCK_APP.get(FirebaseAuth.class), FirebaseAuth.getInstance());
    //        assertNotNull(FirebaseAuth.getInstance().getCurrentUser());
    //
    //        ActivityController<SignInActivity> activityController =
    // Robolectric.buildActivity(SignInActivity.class);
    //        SignInActivity activity = activityController.get();
    //        //activity.setCurrentFirebaseUser(u);
    //        //Intent intent = new Intent();
    //        //intent = new Intent(Robolectric.application.getApplicationContext(),
    // MyActivity.class);
    //        //intent.putExtra(EXTRA_KEY, "Extra value");
    //        activityController
    //          //      .newIntent(intent)
    //                .create()
    //                .visible()
    //                .start()
    //                .resume();
    //

    //        SignInActivity newMock = spy(SignInActivity.class);
    // //Mockito.spy(SignInActivity.class);
    //        FirebaseUser u = TestHelper.getMockFirebaseUser();
    //        when(newMock.getCurrentFirebaseUser()).thenReturn(u);
    //        newMock.onCreate(null);
  }

  //    @Test
  //    public void test_emailLinkProvided() {
  ////        FirebaseUser u = TestHelper.getMockFirebaseUser();
  ////        ActivityController<SignInActivity> activityController =
  // Robolectric.buildActivity(SignInActivity.class);
  ////        SignInActivity activity = activityController.get();
  ////        activity.setCurrentFirebaseUser(u);
  //        //spy(SignInActivity.class)
  //
  //        ActivityController<SignInActivity> activityController =
  // Robolectric.buildActivity(SignInActivity.class);
  //        Intent intent = new Intent(TestHelper.CONTEXT, SignInActivity.class);
  //        intent.putExtra(EXTRA_KEY, "test@link");
  //        SignInActivity activity = Robolectric.buildActivity(SignInActivity.class,
  // intent).create().get();
  //
  //        SignInActivity act = spy(SignInActivity.class);
  //        when(act.isIntentLinkValid()).thenReturn(true);
  //        when(act.getIntent().getData()).thenReturn(true);
  //
  //        act.createSignInIntent();
  //
  //
  ////        SignInActivity newMock = spy(SignInActivity.class);
  // //Mockito.spy(SignInActivity.class);
  ////        FirebaseUser u = TestHelper.getMockFirebaseUser();
  ////        when(newMock.getCurrentFirebaseUser()).thenReturn(u);
  ////        newMock.onCreate(null);
  //    }

  //    @Test
  //    public void testOnCreate_userNotLoggedIn() {
  //
  ////        scenario = launchActivity<LocationTrackerActivity>()
  ////
  ////        // WHEN
  ////        scenario.moveToState(Lifecycle.State.CREATED)
  ////
  ////        // THEN
  ////        scenario.onActivity { activity ->
  ////                assertThat(activity.locationListener).isNull()
  ////        }
  //
  //        //when(mock.getCurrentFirebaseUser()).thenReturn(null);
  //    }

  @Test
  public void testCreateSignInIntent() {
    mock.createSignInIntent();
  }
}
