package ch.epfl.favo.viewmodel;

import android.location.Location;

import androidx.lifecycle.MutableLiveData;

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
import ch.epfl.favo.common.FavoLocation;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.DependencyFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;

public class FavorViewModelTest {
  private FavorViewModel viewModel;
  private FavorUtil favorRepository;
  private UserUtil userRepository;
  private CompletableFuture successfulResult;
  private CompletableFuture failedResult;

  @Before
  public void setup() {
    favorRepository = Mockito.mock(FavorUtil.class);
    userRepository = Mockito.mock(UserUtil.class);
    successfulResult =
        new CompletableFuture() {
          {
            complete(null);
          }
        };
    failedResult =
        new CompletableFuture() {
          {
            completeExceptionally(new RuntimeException("mock fail"));
          }
        };
    DependencyFactory.setCurrentFavorRepository(favorRepository);
    DependencyFactory.setCurrentUserRepository(userRepository);
    DependencyFactory.setCurrentFirebaseUser(FakeItemFactory.getFirebaseUser());
    viewModel = new FavorViewModel();
  }

  @After
  public void tearDown() {
    DependencyFactory.setCurrentFavorRepository(null);
  }

  @Test
  public void testRepositoryBehaviourIsUnchangedOnPostFavor() {
    Mockito.doReturn(successfulResult)
        .when(userRepository)
        .changeActiveFavorCount(anyBoolean(), anyInt());
    Mockito.doReturn(successfulResult).when(favorRepository).requestFavor(any(Favor.class));
    Assert.assertTrue(viewModel.requestFavor(FakeItemFactory.getFavor()).isDone());
    // Assert.assertEquals(successfulResult,viewModel.requestFavor(FakeItemFactory.getFavor()));
  }

  @Test
  public void testRepositoryDoesNotThrowErrorOnUserRepositoryFailedResult() {
    Mockito.doReturn(failedResult)
        .when(userRepository)
        .changeActiveFavorCount(anyBoolean(), anyInt());
    Mockito.doReturn(successfulResult).when(favorRepository).requestFavor(any(Favor.class));
    Assert.assertTrue(
        viewModel.requestFavor(FakeItemFactory.getFavor()).isCompletedExceptionally());
  }

  @Test
  public void testRepositoryDoesNotThrowErrorOnRepositoryPostFavorFailedResult() {
    Mockito.doReturn(successfulResult)
        .when(userRepository)
        .changeActiveFavorCount(anyBoolean(), anyInt());
    Mockito.doReturn(failedResult).when(favorRepository).requestFavor(any(Favor.class));
    Assert.assertTrue(
        viewModel.requestFavor(FakeItemFactory.getFavor()).isCompletedExceptionally());
  }

  @Test
  public void testUpdateBehaviourIsUnchanged() {
    Mockito.doReturn(successfulResult)
        .when(userRepository)
        .changeActiveFavorCount(anyBoolean(), anyInt());
    Mockito.doReturn(successfulResult).when(favorRepository).updateFavor(any(Favor.class));
    Assert.assertTrue(viewModel.updateFavor(FakeItemFactory.getFavor(), true, 1).isDone());
  }

  @Test
  public void testNearbyFavorsListIsTransformedIntoMap() {
    // mocks
    Query mockQuery = Mockito.mock(Query.class);
    // fakes
    List<Favor> fakeList = FakeItemFactory.getFavorList();

    Mockito.doReturn(mockQuery)
        .when(favorRepository)
        .getNearbyFavors(any(Location.class), Mockito.anyDouble());
    DependencyFactory.setCurrentFavorRepository(favorRepository);

    Location centralLocation = fakeList.get(0).getLocation();
    viewModel.getFavorsAroundMe(
        centralLocation, 100.0); // TODO: Find a way to test a snapshot listener
  }

  @Test
  public void testFavorMapIsFilteredAccordingToLatitudeAndLocation() {
    QuerySnapshot mockQuerySnapshot = Mockito.mock(QuerySnapshot.class);
    List<Favor> fakeList = FakeItemFactory.getFavorList();
    FavoLocation mockUserLocation = Mockito.mock(FavoLocation.class);
    Mockito.doReturn(0.0).when(mockUserLocation).getLatitude();
    FavoLocation outOfBoundsLocation = Mockito.mock(FavoLocation.class);
    FavoLocation inBoundsLocation = Mockito.mock(FavoLocation.class);
    Mockito.doReturn(11.0 / FavoLocation.EARTH_RADIUS).when(outOfBoundsLocation).getLatitude();
    Mockito.doReturn(5.0 / FavoLocation.EARTH_RADIUS).when(inBoundsLocation).getLatitude();
    double radius = 10.0;
    for (Favor favor : fakeList) {
      if (favor == fakeList.get(0)) favor.setLocation(inBoundsLocation);
      else favor.setLocation(outOfBoundsLocation);
    }
    Mockito.doReturn(fakeList).when(mockQuerySnapshot).toObjects(any());
    FirebaseFirestoreException ex = null;
    Assert.assertTrue(
        viewModel
            .getNearbyFavorsFromQuery(mockUserLocation, radius, mockQuerySnapshot, ex)
            .containsKey(fakeList.get(0).getId()));
  }

  @Test
  public void testExceptionIsThrownIfEncountered() {
    FirebaseFirestoreException mockException = Mockito.mock(FirebaseFirestoreException.class);
    Mockito.doReturn("mockMessage").when(mockException).getMessage();
    Assert.assertThrows(RuntimeException.class, () -> viewModel.handleException(mockException));
    viewModel.handleException(null); // ensure nothing is thrown
  }

  @Test
  public void testSetObservedFavor() {
    // TODO: Find a way to test snapshot listener
    FavorViewModel spyViewModel = Mockito.spy(viewModel);
    MutableLiveData<Favor> fakeObservedFavor = Mockito.spy(MutableLiveData.class);
    Mockito.doReturn(FakeItemFactory.getFavor()).when(fakeObservedFavor).getValue();
    Mockito.doReturn(fakeObservedFavor).when(spyViewModel).getObservedFavor();
    // mocks
    DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
    Mockito.doReturn(mockDocumentReference)
        .when(favorRepository)
        .getFavorReference(Mockito.anyString());
    viewModel.setObservedFavor("randomid"); // will trigger the db call
    Assert.assertEquals(fakeObservedFavor,spyViewModel.setObservedFavor(
        FakeItemFactory.getFavor().getId())); // will immediately return because id matches current id
  }

  @Test
  public void testGetObservedFavor() {
    viewModel.getObservedFavor();
  }
}
