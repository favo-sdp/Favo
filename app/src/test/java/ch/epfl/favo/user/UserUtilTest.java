package ch.epfl.favo.user;

import android.location.Location;

import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.common.CollectionWrapper;
import ch.epfl.favo.common.Document;
import ch.epfl.favo.common.NotImplementedException;
import ch.epfl.favo.util.DependencyFactory;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;

public class UserUtilTest {
  private CollectionWrapper mockCollectionWrapper;
  private CompletableFuture successfulFuture =
      new CompletableFuture() {
        {
          complete(null);
        }
      };
  private CompletableFuture failedFuture = new CompletableFuture();

  @Before
  public void setup() {
    DependencyFactory.setCurrentFirebaseUser(FakeItemFactory.getFirebaseUser());
    mockCollectionWrapper = Mockito.mock(CollectionWrapper.class);
    Mockito.doReturn(successfulFuture)
        .when(mockCollectionWrapper)
        .updateDocument(anyString(), Mockito.anyMap());
    Mockito.doReturn(successfulFuture)
        .when(mockCollectionWrapper)
        .addDocument(Mockito.any(Document.class));
    DependencyFactory.setCurrentCollectionWrapper(mockCollectionWrapper);
  }

  @Test
  public void testPostUser() {
    Assert.assertTrue(UserUtil.getSingleInstance().postUser(FakeItemFactory.getUser()).isDone());
  }

  @Test
  public void changeActiveRequestingFavorCount() {
    User fakeUser = FakeItemFactory.getUser();
    fakeUser.setActiveRequestingFavors(0);
    CompletableFuture<User> userFuture =
        new CompletableFuture() {
          {
            complete(fakeUser);
          }
        };
    Mockito.doReturn(userFuture).when(mockCollectionWrapper).getDocument(anyString());
    UserUtil.getSingleInstance().setCollectionWrapper(mockCollectionWrapper);
    Assert.assertTrue(UserUtil.getSingleInstance().changeActiveFavorCount(true, 1).isDone());
    Assert.assertTrue(
        UserUtil.getSingleInstance()
            .changeActiveFavorCount(true, User.MAX_REQUESTING_FAVORS + 1)
            .isCompletedExceptionally());
    Assert.assertTrue(
        UserUtil.getSingleInstance()
            .changeActiveFavorCount(false, User.MAX_ACCEPTING_FAVORS)
            .isDone());
    Assert.assertTrue(
        UserUtil.getSingleInstance()
            .changeActiveFavorCount(false, User.MAX_ACCEPTING_FAVORS + 1)
            .isCompletedExceptionally());
  }

  @Test
  public void testUpdateUser() {
    Mockito.doReturn(successfulFuture).when(mockCollectionWrapper).updateDocument(anyString(),anyMap());
    UserUtil.getSingleInstance().setCollectionWrapper(mockCollectionWrapper);
    Assert.assertTrue(UserUtil.getSingleInstance().updateUser(Mockito.mock(User.class)).isDone());
  }

  @Test
  public void testFindUser() {
    // check successful result
    User fakeUser = FakeItemFactory.getUser();
    CompletableFuture<User> userFuture =
        new CompletableFuture() {
          {
            complete(fakeUser);
          }
        };
    Mockito.doReturn(userFuture).when(mockCollectionWrapper).getDocument(anyString());
    UserUtil.getSingleInstance().setCollectionWrapper(mockCollectionWrapper);
    Assert.assertTrue(UserUtil.getSingleInstance().findUser("bla").isDone());
  }

  @Test
  public void testLogInAccount() {
    Assert.assertThrows(
        NotImplementedException.class,
        () -> UserUtil.getSingleInstance().logInAccount("bla", "ble"));
  }

  @Test
  public void testLogOutAccount() {
    Assert.assertThrows(
        NotImplementedException.class, () -> UserUtil.getSingleInstance().logOutAccount());
  }

  @Test
  public void testDeleteAccount() {
    Assert.assertThrows(
        NotImplementedException.class, () -> UserUtil.getSingleInstance().deleteAccount());
  }

  @Test
  public void testRetrieveOtherUsersInGivenRadius() {
    Assert.assertThrows(
        NotImplementedException.class,
        () ->
            UserUtil.getSingleInstance()
                .retrieveOtherUsersInGivenRadius(Mockito.mock(Location.class), 0.1));
  }

  @Test
  public void retrieveUserRegistrationToken() {
    // Mock Firebase objects and returning task
    FirebaseInstanceId mockFirebaseInstanceId = Mockito.mock(FirebaseInstanceId.class);
    Task<InstanceIdResult> mockInstanceIdResult = Mockito.mock(Task.class);
    Mockito.doReturn(mockInstanceIdResult).when(mockFirebaseInstanceId).getInstanceId();
    // Inject in dependency factory
    DependencyFactory.setCurrentFirebaseNotificationInstanceId(mockFirebaseInstanceId);
    // Build mock resulting instance id and its token
    InstanceIdResult mockIdResult = Mockito.mock(InstanceIdResult.class);
    String mockTokenId = "bla";
    Mockito.doReturn(mockTokenId).when(mockIdResult).getToken();
    // stub result in TaskToFutureAdapter
    CompletableFuture<InstanceIdResult> idFuture =
        new CompletableFuture<InstanceIdResult>() {
          {
            complete(mockIdResult);
          }
        };
    DependencyFactory.setCurrentCompletableFuture(idFuture);
    Assert.assertTrue(
        UserUtil.getSingleInstance()
            .retrieveUserRegistrationToken(FakeItemFactory.getUser())
            .isDone());
    DependencyFactory.setCurrentCompletableFuture(null);
    DependencyFactory.setCurrentFirebaseNotificationInstanceId(null);
  }
}
