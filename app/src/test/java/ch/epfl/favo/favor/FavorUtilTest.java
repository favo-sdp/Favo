package ch.epfl.favo.favor;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.FakeUserUtil;
import ch.epfl.favo.TestConstants;
import ch.epfl.favo.database.CollectionWrapper;
import ch.epfl.favo.exception.NotImplementedException;
import ch.epfl.favo.gps.FavoLocation;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.DependencyFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

/** Unit tests for favor util class */
public class FavorUtilTest {
  private CollectionWrapper mockDatabaseWrapper;
  private UserUtil mockUserUtil;
  private CompletableFuture<Void> successfulFuture =
          new CompletableFuture<Void>() {
            {
              complete(null);
            }
          };

  @Before
  public void setUp() {
    mockDatabaseWrapper = Mockito.mock(CollectionWrapper.class);
    DependencyFactory.setCurrentCollectionWrapper(mockDatabaseWrapper);
    DependencyFactory.setCurrentFirebaseUser(FakeItemFactory.getFirebaseUser());
    DependencyFactory.setCurrentUserRepository(new FakeUserUtil());
    FavorUtil.getSingleInstance().updateCollectionWrapper(mockDatabaseWrapper);

    mockUserUtil = Mockito.mock(UserUtil.class);
    Mockito.doReturn(successfulFuture)
            .when(mockUserUtil)
            .updateCoinBalance(anyString(), anyDouble());
  }

  @After
  public void tearDown() {
    DependencyFactory.setCurrentCollectionWrapper(
        new CollectionWrapper(DependencyFactory.getCurrentFavorCollection(), Favor.class));
  }

  @Test
  public void testPostFavorFlow() {
    FavorUtil.getSingleInstance().updateCollectionWrapper(mockDatabaseWrapper);

    mockUserUtil = Mockito.mock(UserUtil.class);
    Mockito.doReturn(successfulFuture)
            .when(mockUserUtil)
            .updateCoinBalance(anyString(), anyDouble());

    Favor favor = FakeItemFactory.getFavor();
    FavorUtil.getSingleInstance().requestFavor(favor);
  }

  @Test
  public void testGetSingleFavor() throws ExecutionException, InterruptedException {
    Favor fakeFavor = FakeItemFactory.getFavor();
    CompletableFuture<Favor> futureFavor = new CompletableFuture<>();
    futureFavor.complete(fakeFavor);
    Mockito.doReturn(futureFavor).when(mockDatabaseWrapper).getDocument(anyString());
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
        .updateDocument(anyString(), Mockito.anyMap());
    assertTrue(FavorUtil.getSingleInstance().updateFavor(fakeFavor).isDone());
  }

  @Test
  public void testGetSingleFavorThrowsRuntimeException() {
    Favor fakeFavor = FakeItemFactory.getFavor();
    Mockito.doThrow(new RuntimeException()).when(mockDatabaseWrapper).addDocument(any(Favor.class));
    FavorUtil.getSingleInstance().updateCollectionWrapper(mockDatabaseWrapper);
    assertThrows(Exception.class, () -> FavorUtil.getSingleInstance().requestFavor(fakeFavor));
  }

  @Test
  public void testRemoveFavor() {
    Favor fakeFavor = FakeItemFactory.getFavor();
    FavorUtil.getSingleInstance().removeFavor(fakeFavor.getId());
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
  public void testCanGetLocationBoundQuery() {
    CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
    Query mockEqualTo = Mockito.mock(Query.class);
    Query mockGreaterThan = Mockito.mock(Query.class);
    Query mockLessThan = Mockito.mock(Query.class);
    Query mockLimit = Mockito.mock(Query.class);
    Mockito.doReturn(mockCollectionReference).when(mockDatabaseWrapper).getReference();
    Mockito.doReturn(mockEqualTo)
        .when(mockCollectionReference)
        .whereEqualTo(anyString(), anyBoolean());
    Mockito.doReturn(mockGreaterThan).when(mockEqualTo).whereGreaterThan(anyString(), anyDouble());
    Mockito.doReturn(mockLessThan).when(mockGreaterThan).whereLessThan(anyString(), anyDouble());
    Mockito.doReturn(mockLimit).when(mockLessThan).limit(anyInt());
    FavorUtil.getSingleInstance().updateCollectionWrapper(mockDatabaseWrapper);
    FavoLocation location = FakeItemFactory.getFavor().getLocation();
    FavorUtil.getSingleInstance().getNearbyFavors(location, 3.0);
  }

  @Test
  public void testCanGetDocumentReference() {
    DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
    Mockito.doReturn(mockDocumentReference).when(mockDatabaseWrapper).getDocumentQuery(anyString());
    assertEquals(mockDocumentReference, FavorUtil.getSingleInstance().getFavorReference("bla"));
  }

  @Test
  public void favorCanUpdateFavorPhoto() {
    Favor fakeFavor = FakeItemFactory.getFavor();
    String newPictureUrl = TestConstants.OTHER_PICTURE_URL;
    FavorUtil.getSingleInstance().updateFavorPhoto(fakeFavor, newPictureUrl);
    assertEquals(newPictureUrl, fakeFavor.getPictureUrl());
  }

  @Test
  public void testGetAllActiveFavorsFromUser() {
    //    .orderBy("title", Query.Direction.ASCENDING)
    //            .orderBy("postedTime", Query.Direction.DESCENDING)
    //            .whereArrayContains("userIds", userId);
    CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
    Query mockOrderByTitle = Mockito.mock(Query.class);
    Query mockOrderByPostedTime = Mockito.mock(Query.class);
    Query mockWhereArrayContains = Mockito.mock(Query.class);
    Mockito.doReturn(mockCollectionReference).when(mockDatabaseWrapper).getReference();
    Mockito.doReturn(mockOrderByTitle)
        .when(mockCollectionReference)
        .orderBy(anyString(), any(Query.Direction.class));
    Mockito.doReturn(mockOrderByPostedTime)
        .when(mockOrderByTitle)
        .orderBy(anyString(), any(Query.Direction.class));
    Mockito.doReturn(mockWhereArrayContains)
        .when(mockOrderByPostedTime)
        .whereArrayContains(anyString(), any());
    FavorUtil.getSingleInstance().updateCollectionWrapper(mockDatabaseWrapper);
    FavorUtil.getSingleInstance().getAllUserFavors("someId");
  }
}
