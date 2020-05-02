package ch.epfl.favo;

import android.graphics.Bitmap;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.viewmodel.FavorDataController;

public class FakeViewModel extends ViewModel implements FavorDataController {
  private CompletableFuture failedResult;
  private boolean isThrowingError = false;
  private boolean showFavor = false;

  public void setThrowError(Exception throwableObject) {
    isThrowingError = true;
    failedResult =
        new CompletableFuture<Favor>() {
          {
            completeExceptionally(new CompletionException(throwableObject));
          }
        };
  }

  private CompletableFuture getSuccessfulCompletableFuture() {
    return new CompletableFuture() {
      {
        complete(null);
      }
    };
  }

  @Override
  public CompletableFuture requestFavor(Favor favor) {

    if (isThrowingError) return failedResult;
    observedFavorResult.setValue(favor);
    return getSuccessfulCompletableFuture();
  }

  @Override
  public CompletableFuture updateFavorForCurrentUser(
      Favor favor, boolean isRequested, int activeFavorsCountChange) {
    if (isThrowingError) return failedResult;
    observedFavorResult.setValue(favor);
    return getSuccessfulCompletableFuture();
  }

  @Override
  public CompletableFuture acceptFavor(Favor favor) {
    if (isThrowingError) return failedResult;
    favor.setAccepterId(DependencyFactory.getCurrentFirebaseUser().getUid());
    favor.setStatusIdToInt(FavorStatus.ACCEPTED);
    observedFavorResult.setValue(favor);
    return getSuccessfulCompletableFuture();
  }

  @Override
  public CompletableFuture cancelFavor(Favor favor, boolean isRequested) {
    if (isThrowingError) return failedResult;
    favor.setStatusIdToInt(isRequested?FavorStatus.CANCELLED_REQUESTER:FavorStatus.CANCELLED_ACCEPTER);
    observedFavorResult.setValue(favor);
    return getSuccessfulCompletableFuture();
  }

  @Override
  public void uploadOrUpdatePicture(Favor favor, Bitmap picture) {}

  @Override
  public CompletableFuture<Bitmap> downloadPicture(Favor favor) {
    return new CompletableFuture<Bitmap>() {
      {
        complete(null);
      }
    };
  }

  private MutableLiveData<Map<String, Favor>> favorsAroundMeResult = getMapLiveData();

  private MutableLiveData<Map<String, Favor>> getMapLiveData() {

    return new MutableLiveData<Map<String, Favor>>() {
      {
        setValue(FakeItemFactory.getFavorListMap());
      }
    };
  }

  @Override
  public LiveData<Map<String, Favor>> getFavorsAroundMe(Location loc, double radius) {
    return favorsAroundMeResult;
  }

  @Override
  public LiveData<Map<String, Favor>> getFavorsAroundMe() {
    return favorsAroundMeResult;
  }

  private MutableLiveData<Favor> observedFavorResult =
      new MutableLiveData<Favor>() {
        {
          setValue(FakeItemFactory.getFavor());
        }
      };

  public void setObservedFavorResult(Favor favor) {
    observedFavorResult.setValue(favor);
  }

  @Override
  public LiveData<Favor> setObservedFavor(String favorId) {
    Favor observedFavor = new Favor(favorId, " ", " ", TestConstants.REQUESTER_ID, null, 0);
    observedFavor.updateToOther(FakeItemFactory.getFavor());
    setObservedFavorResult(observedFavor);
    return observedFavorResult;
  }

  @Override
  public LiveData<Favor> getObservedFavor() {
    return observedFavorResult;
  }

  public void setFavorValue(Favor favor) {
    observedFavorResult.setValue(favor);
  }

  @Override
  public void setShowObservedFavor(Boolean show) {
    showFavor = show;
  }

  @Override
  public boolean isShowObservedFavor() {
    return showFavor;
  }

}
