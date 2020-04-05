package ch.epfl.favo.favor;

import android.location.Location;

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

/** Unit tests for favor util class */
public class FavorUtilTest {
  private CollectionWrapper mockDatabaseWrapper;

  @Before
  public void setUp() throws Exception {
    mockDatabaseWrapper = Mockito.mock(CollectionWrapper.class);
  }

  @After
  public void tearDown() throws Exception {
    DependencyFactory.setCurrentCollectionWrapper(null);
  }

  @Test
  public void testPostFavorFlow() {
    DependencyFactory.setCurrentCollectionWrapper(mockDatabaseWrapper);
    Favor favor = FakeItemFactory.getFavor();
    FavorUtil.getSingleInstance().postFavor(favor);
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
  public void favorCanRetrieveAllFavorsInGivenRadius() {

    Location loc = TestConstants.LOCATION;
    double radius = TestConstants.RADIUS;
    assertThrows(
        NotImplementedException.class,
        () -> FavorUtil.getSingleInstance().retrieveAllFavorsInGivenRadius(loc, radius));
  }

  @Test
  public void favorCanRetrieveAllPastFavors() {
    assertThrows(
        NotImplementedException.class,
        () -> FavorUtil.getSingleInstance().retrieveAllPastFavorsForGivenUser("id"));
  }
}
