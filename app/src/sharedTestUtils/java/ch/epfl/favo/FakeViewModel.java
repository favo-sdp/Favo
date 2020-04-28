package ch.epfl.favo;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.viewmodel.FavorDataController;

public class FakeViewModel extends ViewModel implements FavorDataController {
  private boolean throwError = false;
  private CompletableFuture result = getSuccessfulCompletableFuture();
  private boolean showFavor = false;
  private Favor observedFavor;

  public void setThrowError(boolean t) {
    throwError = t;
    if (throwError){
      result = getFailedCompletableFuture();
    }
  }


  private CompletableFuture getFailedCompletableFuture() {
    return new CompletableFuture<Favor>() {
      {
        completeExceptionally(new RuntimeException());
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
  public CompletableFuture postFavor(Favor favor) {
    return result;
  }

  @Override
  public CompletableFuture updateFavor(Favor favor) {
    if (!throwError) setObservedFavorResult(favor);
    return result;
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
  /**TODO: this looks like needing change **/
  @Override
  public LiveData<Favor> setObservedFavor(String favorId) {
    observedFavor = new Favor(favorId, " ", " ", null, null, 0);
    observedFavor.updateToOther(FakeItemFactory.getFavor());
    setObservedFavorResult(observedFavor);
    return observedFavorResult;
  }

  @Override
  public MutableLiveData<Favor> getObservedFavor() {
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
