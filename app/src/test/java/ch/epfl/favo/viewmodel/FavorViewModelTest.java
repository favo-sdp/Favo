package ch.epfl.favo.viewmodel;

import android.location.Location;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.gps.FavoLocation;
import ch.epfl.favo.util.DependencyFactory;

import static org.mockito.ArgumentMatchers.any;

public class FavorViewModelTest {
  private FavorViewModel viewModel;
  private FavorUtil repository;
  private CompletableFuture successfulResult;
  private CompletableFuture failedResult;
  @Before
  public void setup(){
    repository = Mockito.mock(FavorUtil.class);
    successfulResult = new CompletableFuture(){{complete(null);}};
    failedResult = new CompletableFuture(){{completeExceptionally(new RuntimeException("mock fail"));}};
    DependencyFactory.setCurrentRepository(repository);
    DependencyFactory.setCurrentFirebaseUser(FakeItemFactory.getUser());
    viewModel = new FavorViewModel();
  }
  @After
  public void tearDown(){
    DependencyFactory.setCurrentRepository(null);
  }
  @Test
  public void testRepositoryBehaviourIsUnchangedOnPostFavor() {
    Mockito.doReturn(successfulResult).when(repository).postFavor(any(Favor.class));
    Assert.assertEquals(successfulResult,viewModel.postFavor(FakeItemFactory.getFavor()));
  }

  @Test
  public void testRepositoryDoesNotThrowErrorOnRepositoryPostFavorFailedResult() {
    Mockito.doReturn(failedResult).when(repository).postFavor(any(Favor.class));
    Assert.assertEquals(failedResult,viewModel.postFavor(FakeItemFactory.getFavor()));
  }
  @Test
  public void testUpdateBehaviourIsUnchanged(){
    Mockito.doReturn(successfulResult).when(repository).updateFavor(any(Favor.class));
    Assert.assertEquals(successfulResult,viewModel.updateFavor(FakeItemFactory.getFavor()));
  }

  @Test
  public void testNearbyFavorsListIsTransformedIntoMap() {
    //mocks
    Query mockQuery = Mockito.mock(Query.class);
    //fakes
    List<Favor> fakeList = FakeItemFactory.getFavorList();

    Mockito.doReturn(mockQuery).when(repository).getNearbyFavors(any(Location.class),Mockito.anyDouble());
    DependencyFactory.setCurrentRepository(repository);


    Location centralLocation = fakeList.get(0).getLocation();
    viewModel.getFavorsAroundMe(centralLocation,100.0);//TODO: Find a way to test a snapshot listener
  }
  @Test
  public void testFavorMapIsFilteredAccordingToLatitudeAndLocation(){
    QuerySnapshot mockQuerySnapshot = Mockito.mock(QuerySnapshot.class);
    List<Favor> fakeList = FakeItemFactory.getFavorList();
    FavoLocation mockUserLocation = Mockito.mock(FavoLocation.class);
    Mockito.doReturn(0.0).when(mockUserLocation).getLatitude();
    FavoLocation outOfBoundsLocation = Mockito.mock(FavoLocation.class);
    FavoLocation inBoundsLocation = Mockito.mock(FavoLocation.class);
    Mockito.doReturn(Math.toDegrees(11.0 / FavoLocation.EARTH_RADIUS)).when(outOfBoundsLocation).getLatitude();
    Mockito.doReturn(Math.toDegrees(5.0 / FavoLocation.EARTH_RADIUS)).when(inBoundsLocation).getLatitude();
    double radius = 10.0;
    for (Favor favor: fakeList){
      if (favor==fakeList.get(0)) favor.setLocation(inBoundsLocation);
      else  favor.setLocation(outOfBoundsLocation);
    }
    Mockito.doReturn(fakeList).when(mockQuerySnapshot).toObjects(any());
    FirebaseFirestoreException ex = null;
    Assert.assertTrue(viewModel.getNearbyFavorsFromQuery(mockUserLocation,radius,mockQuerySnapshot,ex).containsKey(fakeList.get(0).getId()));
  }
  @Test
  public void testExceptionIsThrownIfEncountered(){
    FirebaseFirestoreException mockException = Mockito.mock(FirebaseFirestoreException.class);
    Mockito.doReturn("mockMessage").when(mockException).getMessage();
    Assert.assertThrows(RuntimeException.class,()->viewModel.handleException(mockException));
    viewModel.handleException(null); //ensure nothing is thrown
  }


  @Test
  public void testSetObservedFavor() {
    //TODO: Find a way to test snapshot listener
    //mocks
    DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
    Mockito.doReturn(mockDocumentReference).when(repository).getFavorReference(Mockito.anyString());
    viewModel.setObservedFavor("sampleId");
  }

  @Test
  public void testGetObservedFavor() {
    viewModel.getObservedFavor();
  }
}