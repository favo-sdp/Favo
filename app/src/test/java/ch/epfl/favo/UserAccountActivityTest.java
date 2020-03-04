package ch.epfl.favo;

import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import ch.epfl.favo.testhelpers.TestHelper;

import static org.mockito.Mockito.spy;

@RunWith(RobolectricTestRunner.class)
public class UserAccountActivityTest {

  private UserAccountActivity mock;

  @Before
  public void setup() {
    TestHelper.initialize();
  }

  @Test
  public void testDisplayUserData() {
    FirebaseUser u = TestHelper.createMockFirebaseUser();
    ActivityController<UserAccountActivity> activityController =
        Robolectric.buildActivity(UserAccountActivity.class);
    UserAccountActivity activity = activityController.get();
    activity.displayUserData(u);
  }

  @Test
  public void testDeleteAccount() {

    UserAccountActivity activity = spy(UserAccountActivity.class);

    //
    // when(activity.getCurrentAuthUIInstance()).thenReturn(AuthUI.getInstance(TestHelper.MOCK_APP));
    //        AuthUI mAuthUi = AuthUI.getInstance(TestHelper.MOCK_APP);
    //        when(activity.getCurrentAuthUIInstance()).thenReturn(mAuthUi);
    //        when(mAuthUi.signOut(TestHelper.CONTEXT).addOnCompleteListener()).thenReturn(ciao);
    //        activity.signOut(null);
    //
    // when(AuthUI.getInstance(TestHelper.MOCK_APP).signOut(any()).addOnCompleteListener(any())).thenReturn();
    //
    //        when(mMockAuth.sendSignInLinkToEmail(any(String.class),
    // any(ActionCodeSettings.class)))
    //                .thenReturn(AutoCompleteTask.<Void>forSuccess(null));

    // Intent intent = new Intent();
    // intent = new Intent(Robolectric.application.getApplicationContext(), MyActivity.class);
    // intent.putExtra(EXTRA_KEY, "Extra value");
    //        activityController
    //                //      .newIntent(intent)
    //                .create()
    //                .visible()
    //                .start()
    //                .resume();
  }

  //    @Test
  //    public void testUserAccountPopulates() {
  //
  //        UserAccountActivity user = new UserAccountActivity();
  //
  //        when(mockContext.getString(R.string.hello_world))
  //                .thenReturn(FAKE_STRING);
  //        //mAuthUi.signOut().addOnCompleteListener()
  //        user.deleteAccountClicked(null);
  //
  //        //user.showUserData(getMockFirebaseUser(), CONTEXT);
  //
  //
  //
  //    }

}
