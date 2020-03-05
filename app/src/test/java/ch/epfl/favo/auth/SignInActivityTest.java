package ch.epfl.favo.auth;

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
    mock.onActivityResult(123, RESULT_OK, null);
  }

  @Test
  public void testOnActivityResult_resultWithNotNullResponse() {
    mock.onActivityResult(123, RESULT_OK, null);
  }

  @Test
  public void testCreateSignInIntent() {
    mock.createSignInIntent();
  }
}
