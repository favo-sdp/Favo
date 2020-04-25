package ch.epfl.favo.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.common.CollectionWrapper;
import ch.epfl.favo.common.DatabaseUpdater;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.map.GpsTracker;
import ch.epfl.favo.map.Locator;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.viewmodel.FavorViewModel;

public class DependencyFactory {
  private static Locator currentGpsTracker;
  private static FirebaseUser currentUser;
  private static DatabaseUpdater currentCollectionWrapper;
  private static Intent currentCameraIntent;
  private static LocationManager currentLocationManager;
  private static FirebaseFirestore currentFirestore;
  private static boolean offlineMode = false;
  private static boolean testMode = false;
  private static CompletableFuture currentCompletableFuture;
  private static Settings.Secure deviceSettings;
  private static String currentFavorCollection = "favors";
  private static Class currentViewModelClass;
  private static FavorUtil currentFavorRepository;
  private static UserUtil currentUserRepository;

  @RequiresApi(api = Build.VERSION_CODES.M)
  public static boolean isOfflineMode(Context context) {
    return offlineMode || CommonTools.isOffline(context);
  }

  public static boolean isTestMode() {
    return testMode && currentCompletableFuture != null;
  }

  @VisibleForTesting
  public static void setOfflineMode(boolean value) {
    offlineMode = value;
  }

  public static FirebaseUser getCurrentFirebaseUser() {
    if (testMode) {
      return currentUser;
    }
    return FirebaseAuth.getInstance().getCurrentUser();
  }

  @VisibleForTesting
  public static void setCurrentFirebaseUser(FirebaseUser dependency) {
    testMode = true;
    currentUser = dependency;
  }

  public static Locator getCurrentGpsTracker(@Nullable Context context) {
    if (testMode && currentGpsTracker != null) {
      return currentGpsTracker;
    }
    return new GpsTracker(context);
  }

  @VisibleForTesting
  public static void setCurrentGpsTracker(Locator gpsTrackerDependency) {
    testMode = true;
    currentGpsTracker = gpsTrackerDependency;
  }

  @VisibleForTesting
  public static void setCurrentCollectionWrapper(DatabaseUpdater dependency) {
    testMode = true;
    currentCollectionWrapper = dependency;
  }

  public static DatabaseUpdater getCurrentCollectionWrapper(String collectionReference, Class cls) {
    if (testMode && currentCollectionWrapper != null) {
      return currentCollectionWrapper;
    }
    return new CollectionWrapper(collectionReference, cls);
  }

  @VisibleForTesting
  public static void setCurrentCameraIntent(Intent dependency) {
    testMode = true;
    currentCameraIntent = dependency;
  }

  public static Intent getCurrentCameraIntent() {
    if (testMode && currentCameraIntent != null) {
      return currentCameraIntent;
    }
    return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
  }

  @VisibleForTesting
  public static void setCurrentLocationManager(LocationManager dependency) {
    testMode = true;
    currentLocationManager = dependency;
  }

  public static LocationManager getCurrentLocationManager(Context context) {
    if (testMode && currentLocationManager != null) {
      return currentLocationManager;
    }
    return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
  }

  @VisibleForTesting
  public static void setCurrentFirestore(FirebaseFirestore dependency) {
    testMode = true;
    currentFirestore = dependency;
  }

  public static FirebaseFirestore getCurrentFirestore() {
    if (testMode && currentFirestore != null) {
      return currentFirestore;
    }
    return FirebaseFirestore.getInstance();
  }

  public static String getDeviceId(@Nullable ContentResolver contentResolver) {
    if (testMode || contentResolver == null) {
      return "22f523fgg3";
    }
    return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
  }

  public static <T> CompletableFuture<T> getCurrentCompletableFuture() {
    return currentCompletableFuture;
  }

  public static void setCurrentCompletableFuture(CompletableFuture currentCompletableFuture) {
    testMode = true;
    DependencyFactory.currentCompletableFuture = currentCompletableFuture;
  }

  public static String getCurrentFavorCollection() {
    return currentFavorCollection;
  }

  @VisibleForTesting
  public static void setCurrentFavorCollection(String collection) {
    currentFavorCollection = collection;
  }
  @VisibleForTesting
  public static void setCurrentViewModelClass(Class dependency){
    testMode = true;
    currentViewModelClass = dependency;
  }
  public static Class getCurrentViewModelClass(){
    if (testMode && currentViewModelClass!=null) {return currentViewModelClass;}
    return FavorViewModel.class;
  }
  @VisibleForTesting
  public static void setCurrentFavorRepository(FavorUtil dependency){
    testMode = true;
    currentFavorRepository = dependency;
  }
  public static FavorUtil getCurrentFavorRepository(){
    if (testMode && currentFavorRepository !=null) return currentFavorRepository;
    return FavorUtil.getSingleInstance();
  }
  @VisibleForTesting
  public static void setCurrentUserRepository(UserUtil dependency){
    testMode = true;
    currentUserRepository = dependency;
  }
  public static UserUtil getCurrentUserRepository(){
    if (testMode && currentUserRepository !=null) return currentUserRepository;
    return UserUtil.getSingleInstance();
  }
}
