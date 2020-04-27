package ch.epfl.favo.auth;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.MockUserUtil;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.MockGpsTracker;

import static android.app.Activity.RESULT_OK;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static ch.epfl.favo.util.DependencyFactory.setCurrentFirebaseUser;

@RunWith(AndroidJUnit4.class)
public class SignInActivityInstrumentedTest {
  private MockUserUtil mockUserUtil = new MockUserUtil();
  @Rule
  public final ActivityTestRule<SignInActivity> activityTestRule =
          new ActivityTestRule<SignInActivity>(SignInActivity.class) {
            @Override
            protected void beforeActivityLaunched() {
//              DependencyFactory.setCurrentFirebaseUser(
//                      new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
              DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
              DependencyFactory.setCurrentUserRepository(mockUserUtil);
            }
          };
  @After
  public void tearDown() throws ExecutionException, InterruptedException {
    DependencyFactory.setCurrentGpsTracker(null);
    setCurrentFirebaseUser(null);
    DependencyFactory.setCurrentUserRepository(null);
  }
  @Test
  public void testSignInFlow() throws Throwable {
    setCurrentFirebaseUser(
                      new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
    handleSignInResponse(RESULT_OK);
  }
  @Test
  public void testSignInFlowWhenUserNotFound() throws Throwable{
    setCurrentFirebaseUser(
            new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
    mockUserUtil.setFindUserFail(true);
    handleSignInResponse(RESULT_OK);
  }

  private void handleSignInResponse(int result) throws Throwable {
      runOnUiThread(() -> activityTestRule.getActivity().onActivityResult(SignInActivity.RC_SIGN_IN,result,null));

  }
}
