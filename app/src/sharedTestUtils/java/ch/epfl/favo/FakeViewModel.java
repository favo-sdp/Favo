package ch.epfl.favo;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.user.User;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.viewmodel.IFavorViewModel;

public class FakeViewModel extends ViewModel implements IFavorViewModel {
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

  private CompletableFuture<Void> getSuccessfulCompletableFuture() {
    return new CompletableFuture<Void>() {
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
  public CompletableFuture acceptFavor(Favor favor) {
    if (isThrowingError) return failedResult;
    favor.setAccepterId(DependencyFactory.getCurrentFirebaseUser().getUid());
    favor.setStatusIdToInt(FavorStatus.ACCEPTED);
    observedFavorResult.setValue(favor);
    return getSuccessfulCompletableFuture();
  }

  @Override
  public CompletableFuture completeFavor(Favor favor, boolean isRequested) {
    if (isThrowingError) return failedResult;
    if (favor.getStatusId() == FavorStatus.COMPLETED_REQUESTER.toInt()
        || favor.getStatusId() == FavorStatus.COMPLETED_ACCEPTER.toInt())
      favor.setStatusIdToInt(FavorStatus.SUCCESSFULLY_COMPLETED);
    else
      favor.setStatusIdToInt(
          isRequested ? FavorStatus.COMPLETED_REQUESTER : FavorStatus.COMPLETED_ACCEPTER);
    observedFavorResult.setValue(favor);
    return getSuccessfulCompletableFuture();
  }

  @Override
  public CompletableFuture cancelFavor(Favor favor, boolean isRequested) {
    if (isThrowingError) return failedResult;
    favor.setStatusIdToInt(
        isRequested ? FavorStatus.CANCELLED_REQUESTER : FavorStatus.CANCELLED_ACCEPTER);
    observedFavorResult.setValue(favor);
    return getSuccessfulCompletableFuture();
  }

  @Override
  public CompletableFuture deleteFavor(Favor favor) {
    if (isThrowingError) return failedResult;
    return getSuccessfulCompletableFuture();
  }

  @Override
  public CompletableFuture reEnableFavor(Favor favor) {
    if (isThrowingError) return failedResult;
    favor.setStatusIdToInt(FavorStatus.REQUESTED);
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

  @Override
  public CompletableFuture<Bitmap> loadPictureFromLocal(Context context, Favor favor) {
    return null;
  }

  @Override
  public void savePictureToLocal(Context context, Favor favor, Bitmap picture) {}

  private MutableLiveData<Map<String, Favor>> favorsAroundMeResult = getMapLiveData();
  private MutableLiveData<User> observedUser = getUserMutableLiveData();

  private MutableLiveData<User> getUserMutableLiveData() {
    return new MutableLiveData<User>() {
      {
        setValue(FakeItemFactory.getUser());
      }
    };
  }

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
    Favor observedFavor = new Favor(favorId, " ", " ", TestConstants.REQUESTER_ID, null, 0, 5);
    observedFavor.updateToOther(FakeItemFactory.getFavor());
    setObservedFavorResult(observedFavor);
    return observedFavorResult;
  }

  @VisibleForTesting
  public void setFavorValue(Favor favor) {
    observedFavorResult.setValue(favor);
  }

  @Override
  public LiveData<Favor> getObservedFavor() {
    return observedFavorResult;
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
