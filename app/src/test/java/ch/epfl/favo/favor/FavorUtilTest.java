package ch.epfl.favo.favor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.TestConstants;
import ch.epfl.favo.common.CollectionWrapper;
import ch.epfl.favo.common.NotImplementedException;
import ch.epfl.favo.util.DependencyFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/** Unit tests for favor util class */
public class FavorUtilTest {
  private CollectionWrapper mockDatabaseWrapper;

  @Before
  public void setUp() {
    mockDatabaseWrapper = Mockito.mock(CollectionWrapper.class);
    DependencyFactory.setCurrentCollectionWrapper(mockDatabaseWrapper);
    FavorUtil.getSingleInstance().updateCollectionWrapper(mockDatabaseWrapper);
  }

  @After
  public void tearDown() {
    DependencyFactory.setCurrentCollectionWrapper(
        new CollectionWrapper(DependencyFactory.getCurrentFavorCollection(), Favor.class));
  }

  @Test
  public void testPostFavorFlow() {
    Favor favor = FakeItemFactory.getFavor();
    FavorUtil.getSingleInstance().requestFavor(favor);
  }

  @Test
  public void testGetSingleFavor() throws ExecutionException, InterruptedException {
    Favor fakeFavor = FakeItemFactory.getFavor();
    CompletableFuture<Favor> futureFavor = new CompletableFuture<>();
    futureFavor.complete(fakeFavor);
    Mockito.doReturn(futureFavor).when(mockDatabaseWrapper).getDocument(Mockito.anyString());
    FavorUtil.getSingleInstance().updateCollectionWrapper(mockDatabaseWrapper);

    CompletableFuture<Favor> obtainedFutureFavor =
        FavorUtil.getSingleInstance().retrieveFavor(TestConstants.FAVOR_ID);
    assertEquals(fakeFavor, obtainedFutureFavor.get());
  }

  @Test
  public void testUpdateFavor() {
    Favor fakeFavor = FakeItemFactory.getFavor();
    CompletableFuture successfulTask = new CompletableFuture<>();
    successfulTask.complete(null);
    Mockito.doReturn(successfulTask)
        .when(mockDatabaseWrapper)
        .updateDocument(Mockito.anyString(), Mockito.anyMap());
    assertTrue(FavorUtil.getSingleInstance().updateFavor(fakeFavor).isDone());
  }

  @Test
  public void testGetSingleFavorThrowsRuntimeException() {
    Favor fakeFavor = FakeItemFactory.getFavor();
    Mockito.doThrow(new RuntimeException())
        .when(mockDatabaseWrapper)
        .addDocument(Mockito.any(Favor.class));
    FavorUtil.getSingleInstance().updateCollectionWrapper(mockDatabaseWrapper);
    assertThrows(Exception.class,()->FavorUtil.getSingleInstance().requestFavor(fakeFavor));
  }

  @Test
  public void retrieveAllFavorsForGivenUser() {
    String userId = TestConstants.USER_ID;
    assertThrows(
        NotImplementedException.class,
        () -> FavorUtil.getSingleInstance().retrieveAllFavorsForGivenUser(userId));
  }

  @Test
  public void favorCanRetrieveAllActiveFavorsForGivenUser() {

    String userId = TestConstants.USER_ID;
    assertThrows(
        NotImplementedException.class,
        () -> FavorUtil.getSingleInstance().retrieveAllActiveFavorsForGivenUser(userId));
  }

  @Test
  public void favorCanRetrieveAllRequestedFavorsForGivenUser() {

    String userId = TestConstants.USER_ID;
    assertThrows(
        NotImplementedException.class,
        () -> FavorUtil.getSingleInstance().retrieveAllRequestedFavorsForGivenUser(userId));
  }

  @Test
  public void favorCanRetrieveAllAcceptedFavorsForGivenUser() {

    String userId = TestConstants.USER_ID;
    assertThrows(
        NotImplementedException.class,
        () -> FavorUtil.getSingleInstance().retrieveAllAcceptedFavorsForGivenUser(userId));
  }

  @Test
  public void favorCanRetrieveAllPastFavors() {
    assertThrows(
        NotImplementedException.class,
        () -> FavorUtil.getSingleInstance().retrieveAllPastFavorsForGivenUser("id"));
  }

  @Test
  public void favorCanUpdateFavorPhoto() {
    Favor fakeFavor = FakeItemFactory.getFavor();
    String newPictureUrl = TestConstants.OTHER_PICTURE_URL;
    FavorUtil.getSingleInstance().updateFavorPhoto(fakeFavor, newPictureUrl);
    assertEquals(newPictureUrl, fakeFavor.getPictureUrl());
  }
}
