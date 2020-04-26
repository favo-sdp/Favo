package ch.epfl.favo.user;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.common.CollectionWrapper;
import ch.epfl.favo.common.Document;
import ch.epfl.favo.util.DependencyFactory;

import static org.mockito.ArgumentMatchers.anyString;

public class UserUtilTest {
  private CollectionWrapper mockCollectionWrapper;
  private CompletableFuture successfulFuture=new CompletableFuture(){{complete(null);}};
  private CompletableFuture failedFuture = new CompletableFuture();
  @Before
  public void setup(){
    DependencyFactory.setCurrentFirebaseUser(FakeItemFactory.getFirebaseUser());
    mockCollectionWrapper = Mockito.mock(CollectionWrapper.class);
    Mockito.doReturn(successfulFuture).when(mockCollectionWrapper).updateDocument(anyString(),Mockito.anyMap());
    Mockito.doReturn(successfulFuture).when(mockCollectionWrapper).addDocument(Mockito.any(Document.class));
    DependencyFactory.setCurrentCollectionWrapper(mockCollectionWrapper);
  }
  private void setFailedFuture(Throwable throwable){failedFuture.completeExceptionally(throwable);  }
  @Test
  public void changeActiveFavorCount() {
    User fakeUser = FakeItemFactory.getUser();
    fakeUser.setActiveRequestingFavors(0);
    CompletableFuture<User> userFuture = new CompletableFuture(){{complete(fakeUser);}};
    Mockito.doReturn(userFuture).when(mockCollectionWrapper).getDocument(anyString());
    UserUtil.getSingleInstance().updateCollectionWrapper(mockCollectionWrapper);
    Assert.assertTrue(UserUtil.getSingleInstance().changeActiveFavorCount(true,1).isDone());
    Assert.assertTrue(UserUtil.getSingleInstance().changeActiveFavorCount(true,User.MAX_REQUESTING_FAVORS+1).isCompletedExceptionally());
    Assert.assertTrue(UserUtil.getSingleInstance().changeActiveFavorCount(false,User.MAX_ACCEPTING_FAVORS).isDone());
    Assert.assertTrue(UserUtil.getSingleInstance().changeActiveFavorCount(false,User.MAX_ACCEPTING_FAVORS+1).isCompletedExceptionally());
  }

  @Test
  public void testUpdateUser() {
    User fakeUser = FakeItemFactory.getUser();
  }

  @Test
  public void findUser() {}

  @Test
  public void logInAccount() {}

  @Test
  public void logOutAccount() {}

  @Test
  public void deleteAccount() {}

  @Test
  public void retrieveOtherUsersInGivenRadius() {}

  @Test
  public void retrieveUserRegistrationToken() {}
}