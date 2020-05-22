package ch.epfl.favo.viewmodel;

import android.graphics.Bitmap;
import android.location.Location;

import androidx.lifecycle.LiveData;

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
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.gps.FavoLocation;
import ch.epfl.favo.user.User;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.PictureUtil;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

public class FavorViewModelTest {
  private FavorViewModel viewModel;
  private FavorUtil favorRepository;
  private UserUtil userRepository;
  private CompletableFuture successfulResult;
  private CompletableFuture failedResult;
  private PictureUtil pictureUtility;
  private Bitmap bitmap;
  private DocumentReference documentReference;

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
    bitmap = Mockito.mock(Bitmap.class);
    pictureUtility = Mockito.mock(PictureUtil.class);
    documentReference = Mockito.mock(DocumentReference.class);
    DependencyFactory.setCurrentPictureUtility(pictureUtility);
    setupReturns();
  }

  private void setupReturns() {
    Mockito.doReturn(documentReference).when(userRepository).getCurrentUserReference(anyString());
    Mockito.doReturn(successfulResult).when(pictureUtility).deletePicture(Mockito.anyString());
    Mockito.doReturn(successfulResult).when(favorRepository).updateFavor(any(Favor.class));
    Mockito.doReturn(successfulResult).when(favorRepository).requestFavor(any(Favor.class));
    Mockito.doReturn(successfulResult).when(favorRepository).removeFavor(Mockito.anyString());
    Mockito.doReturn(successfulResult)
        .when(userRepository)
        .changeActiveFavorCount(anyString(), anyBoolean(), anyInt());
    Mockito.doReturn(successfulResult).when(userRepository).updateUser(any(User.class));
  }

  @After
  public void tearDown() {
    DependencyFactory.setCurrentFavorRepository(null);
  }

  @Test
  public void testRepositoryBehaviourIsUnchangedOnPostFavor() {
    Assert.assertTrue(viewModel.requestFavor(FakeItemFactory.getFavor()).isDone());
    // Assert.assertEquals(successfulResult,viewModel.requestFavor(FakeItemFactory.getFavor()));
  }

  @Test
  public void testRepositoryDoesNotThrowErrorOnUserRepositoryFailedResult() {
    Mockito.doReturn(failedResult)
        .when(userRepository)
        .changeActiveFavorCount(anyString(), anyBoolean(), anyInt());
    Assert.assertTrue(
        viewModel.requestFavor(FakeItemFactory.getFavor()).isCompletedExceptionally());
  }

  @Test
  public void testRepositoryDoesNotThrowErrorOnRepositoryPostFavorFailedResult() {
    Mockito.doReturn(failedResult).when(favorRepository).requestFavor(any(Favor.class));
    CompletableFuture<Void> voidCompletableFuture =
        viewModel.requestFavor(FakeItemFactory.getFavor());
    voidCompletableFuture.whenComplete(
        (aVoid, throwable) -> Assert.assertTrue(throwable.getCause() instanceof RuntimeException));
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
    Mockito.doReturn(Math.toDegrees(11.0 / FavoLocation.EARTH_RADIUS))
        .when(outOfBoundsLocation)
        .getLatitude();
    Mockito.doReturn(Math.toDegrees(5.0 / FavoLocation.EARTH_RADIUS))
        .when(inBoundsLocation)
        .getLatitude();
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
    // mocks
    DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
    Mockito.doReturn(mockDocumentReference)
        .when(favorRepository)
        .getFavorReference(Mockito.anyString());
    viewModel.setObservedFavor("sampleId");
    Favor fakeFavor = FakeItemFactory.getFavor();
    FavorViewModel viewModelSpy = Mockito.spy(viewModel);
    LiveData<Favor> favorLiveData = Mockito.mock(LiveData.class);
    Mockito.doReturn(fakeFavor).when(favorLiveData).getValue();
    Mockito.doReturn(favorLiveData).when(viewModelSpy).getObservedFavor();
    Mockito.doNothing().when(viewModelSpy).setFavorValue(any(Favor.class));
    Assert.assertEquals(fakeFavor, viewModelSpy.setObservedFavor(fakeFavor.getId()).getValue());
  }

  @Test
  public void testGetObservedFavor() {
    viewModel.getObservedFavor();
  }

  @Test
  public void testUploadPicture() {
    Mockito.doNothing().when(favorRepository).updateFavorPhoto(any(Favor.class), anyString());
    Mockito.when(pictureUtility.uploadPicture(any(Bitmap.class))).thenReturn(successfulResult);
    viewModel.uploadOrUpdatePicture(FakeItemFactory.getFavor(), bitmap);
  }

  @Test
  public void testDownloadPictureSuccessful() {
    Mockito.when(pictureUtility.downloadPicture(anyString())).thenReturn(successfulResult);
    Assert.assertEquals(
        successfulResult, viewModel.downloadPicture(FakeItemFactory.getFavorWithUrl()));
  }

  @Test
  public void testDownloadPictureUnsuccessful() {
    Mockito.when(pictureUtility.downloadPicture(anyString())).thenReturn(successfulResult);
    CompletableFuture<Bitmap> bitmapFuture = viewModel.downloadPicture(FakeItemFactory.getFavor());
    Assert.assertTrue(bitmapFuture.isCompletedExceptionally());
  }

  @Test
  public void testCancelFavorIsSuccessful() {
    Favor fakeFavor = FakeItemFactory.getFavor();
    fakeFavor.setAccepterId("accepter");
    viewModel.cancelFavor(fakeFavor, true);
    Assert.assertTrue(viewModel.cancelFavor(fakeFavor, true).isDone());
    fakeFavor.setAccepterId(null);
    Assert.assertTrue(viewModel.cancelFavor(fakeFavor, true).isDone());
    Assert.assertTrue(viewModel.cancelFavor(fakeFavor, false).isDone());
  }

  @Test
  public void testAcceptFavorIsSuccessful() {
    Assert.assertTrue(viewModel.acceptFavor(FakeItemFactory.getFavor(), new User()).isDone());
  }

  @Test
  public void testCompleteFavorIsSuccessful() {
    Favor fakeFavor = FakeItemFactory.getFavor();
    fakeFavor.setStatusIdToInt(FavorStatus.ACCEPTED);
    Assert.assertTrue(viewModel.completeFavor(fakeFavor, false).isDone());
    Assert.assertTrue(viewModel.completeFavor(fakeFavor, true).isDone());
    fakeFavor.setStatusIdToInt(FavorStatus.COMPLETED_ACCEPTER);
    Assert.assertTrue(viewModel.completeFavor(fakeFavor, true).isDone());
    fakeFavor.setStatusIdToInt(FavorStatus.COMPLETED_REQUESTER);
    Assert.assertTrue(viewModel.completeFavor(fakeFavor, false).isDone());
    // Should fail if favor is not currently
    Assert.assertThrows(
        IllegalStateException.class,
        () -> viewModel.completeFavor(FakeItemFactory.getFavor(), true).isCompletedExceptionally());
  }

  @Test
  public void testDeleteFavorIsSuccessful() {
    Favor fakeFavor = FakeItemFactory.getFavorWithUrl();
    viewModel.deleteFavor(fakeFavor);
  }

  @Test
  public void getObservedFavor() {
    viewModel.getObservedFavor();
  }

  @Test
  public void testSetShowObservedFavor() {
    viewModel.setShowObservedFavor(true);
  }

  @Test
  public void testIsShowObservedFavor() {
    viewModel.isShowObservedFavor();
  }
}
