package ch.epfl.favo.user;

import android.location.Location;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.database.CollectionWrapper;
import ch.epfl.favo.database.Document;
import ch.epfl.favo.exception.NotImplementedException;
import ch.epfl.favo.util.DependencyFactory;

import static ch.epfl.favo.FakeItemFactory.getUser;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;

public class UserUtilTest {
  private CollectionWrapper mockCollectionWrapper;
  private CompletableFuture<Void> successfulFuture =
      new CompletableFuture<Void>() {
        {
          complete(null);
        }
      };
  private CompletableFuture<Void> failedFuture = new CompletableFuture<Void>();

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
    Query mockCollectionReference = Mockito.mock(Query.class);
    Query orderByResult = Mockito.mock(Query.class);
    Query arrayContainsResult = Mockito.mock(Query.class);
    Mockito.doReturn(mockCollectionReference).when(mockCollectionWrapper).getReference();
    Mockito.doReturn(arrayContainsResult)
        .when(orderByResult)
        .whereArrayContains(anyString(), Mockito.any());
    Mockito.doReturn(orderByResult)
        .when(mockCollectionReference)
        .orderBy(anyString(), Mockito.any(Query.Direction.class));
    DependencyFactory.setCurrentCollectionWrapper(mockCollectionWrapper);
  }

  @Test
  public void testPostUser() {
    Assert.assertTrue(UserUtil.getSingleInstance().postUser(getUser()).isDone());
  }

  @Test
  public void changeActiveRequestingFavorCount() {
    User fakeUser = getUser();
    fakeUser.setActiveRequestingFavors(0);
    CompletableFuture<User> userFuture =
        new CompletableFuture<User>() {
          {
            complete(fakeUser);
          }
        };
    Mockito.doReturn(userFuture).when(mockCollectionWrapper).getDocument(anyString());
    UserUtil.getSingleInstance().setCollectionWrapper(mockCollectionWrapper);
    Assert.assertTrue(
        UserUtil.getSingleInstance()
            .changeActiveFavorCount(FakeItemFactory.getFirebaseUser().getUid(), true, 1)
            .isDone());
    Assert.assertTrue(
        UserUtil.getSingleInstance()
            .changeActiveFavorCount(
                FakeItemFactory.getFirebaseUser().getUid(), true, User.MAX_REQUESTING_FAVORS + 1)
            .isCompletedExceptionally());
    Assert.assertTrue(
        UserUtil.getSingleInstance()
            .changeActiveFavorCount(
                FakeItemFactory.getFirebaseUser().getUid(), false, User.MAX_ACCEPTING_FAVORS, 1)
            .isDone());
    Assert.assertTrue(
        UserUtil.getSingleInstance()
            .changeActiveFavorCount(
                FakeItemFactory.getFirebaseUser().getUid(), false, User.MAX_ACCEPTING_FAVORS + 1)
            .isCompletedExceptionally());
  }

  @Test
  public void testUpdateUser() {
    Mockito.doReturn(successfulFuture)
        .when(mockCollectionWrapper)
        .updateDocument(anyString(), anyMap());
    UserUtil.getSingleInstance().setCollectionWrapper(mockCollectionWrapper);
    Assert.assertTrue(UserUtil.getSingleInstance().updateUser(getUser()).isDone());
  }

  @Test
  public void testRemoveUser() {
    Mockito.doReturn(successfulFuture).when(mockCollectionWrapper).removeDocument(anyString());
    UserUtil.getSingleInstance().setCollectionWrapper(mockCollectionWrapper);
    Assert.assertTrue(UserUtil.getSingleInstance().deleteUser(getUser()).isDone());
  }

  @Test
  public void testFindUser() {
    // check successful result
    User fakeUser = getUser();
    CompletableFuture<User> userFuture =
        new CompletableFuture<User>() {
          {
            complete(fakeUser);
          }
        };
    Mockito.doReturn(userFuture).when(mockCollectionWrapper).getDocument(anyString());
    UserUtil.getSingleInstance().setCollectionWrapper(mockCollectionWrapper);
    Assert.assertTrue(UserUtil.getSingleInstance().findUser("bla").isDone());
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
        UserUtil.getSingleInstance().retrieveUserRegistrationToken(getUser()).isDone());
    DependencyFactory.setCurrentCompletableFuture(null);
    DependencyFactory.setCurrentFirebaseNotificationInstanceId(null);
  }

  @Test
  public void testUserReference() {
    UserUtil.getSingleInstance().getCurrentUserReference("randomId");
  }
}
