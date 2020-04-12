package ch.epfl.favo.auth;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.common.CollectionWrapper;
import ch.epfl.favo.user.User;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.MockDatabaseWrapper;

import static android.app.Activity.RESULT_OK;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class SignInActivityTest {

  // These tests just want to make sure that no exception is thrown when
  // the result action of the sign-in is handled

  private SignInActivity spy;
  private Context contextMock = mock(Context.class);

  @Before
  public void setup() {
    spy = spy(SignInActivity.class);
  }

  @Test
  public void testOnActivityResult_requestCodeCorrect() {
    spy.onActivityResult(123, 3, null);
  }

  @Test
  public void testOnActivityResult_requestCodeNotCorrect() {
    spy.onActivityResult(4, RESULT_OK, null);
  }

  @Test
  public void testOnActivityResult_resultNotOk() {
    spy.onActivityResult(123, 10, null);
  }

  @Test
  public void testOnActivityResult_resultOk() {

    Mockito.doNothing().when(spy).handleSignInResponse(anyInt());
    DependencyFactory.setCurrentCollectionWrapper(new MockDatabaseWrapper());
    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, null, null));

    spy.onActivityResult(123, RESULT_OK, null);
  }
}
