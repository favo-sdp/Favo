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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;

import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.cache.CacheUtil;
import ch.epfl.favo.chat.ChatUtil;
import ch.epfl.favo.chat.IChatUtil;
import ch.epfl.favo.database.CollectionWrapper;
import ch.epfl.favo.database.ICollectionWrapper;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.favor.IFavorUtil;
import ch.epfl.favo.gps.GpsTracker;
import ch.epfl.favo.gps.IGpsTracker;
import ch.epfl.favo.user.IUserUtil;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.viewmodel.FavorViewModel;

public class DependencyFactory {
  private static IGpsTracker currentGpsTracker;
  private static FirebaseUser currentFirebaseUser;
  private static ICollectionWrapper currentCollectionWrapper;
  private static Intent currentCameraIntent;
  private static LocationManager currentLocationManager;
  private static FirebaseFirestore currentFirestore;
  private static boolean offlineMode = false;
  private static CompletableFuture currentCompletableFuture;
  private static String currentFavorCollection = "favors";
  private static Class currentViewModelClass;
  private static FavorUtil currentFavorRepository;
  private static IUserUtil currentUserRepository;
  private static FirebaseInstanceId currentFirebaseInstanceId;
  private static IPictureUtil currentPictureUtility;
  private static IChatUtil currentChatUtility;
  private static FirebaseStorage currentFirebaseStorage;
  private static CacheUtil currentCacheUtility;

  @RequiresApi(api = Build.VERSION_CODES.M)
  public static boolean isOfflineMode(Context context) {
    return offlineMode || CommonTools.isOffline(context);
  }

  @VisibleForTesting
  public static void setOfflineMode(boolean value) {
    offlineMode = value;
  }

  public static FirebaseUser getCurrentFirebaseUser() {
    if (currentFirebaseUser != null) {
      return currentFirebaseUser;
    }
    return FirebaseAuth.getInstance().getCurrentUser();
  }

  @VisibleForTesting
  public static void setCurrentFirebaseUser(FirebaseUser dependency) {
    currentFirebaseUser = dependency;
  }

  public static IGpsTracker getCurrentGpsTracker(@Nullable Context context) {
    if (currentGpsTracker != null) {
      return currentGpsTracker;
    }
    return new GpsTracker(context);
  }

  @VisibleForTesting
  public static void setCurrentGpsTracker(IGpsTracker gpsTrackerDependency) {
    currentGpsTracker = gpsTrackerDependency;
  }

  @VisibleForTesting
  public static void setCurrentCollectionWrapper(ICollectionWrapper dependency) {
    currentCollectionWrapper = dependency;
  }

  public static ICollectionWrapper getCurrentCollectionWrapper(
      String collectionReference, Class cls) {
    if (currentCollectionWrapper != null) {
      return currentCollectionWrapper;
    }
    return new CollectionWrapper(collectionReference, cls);
  }

  @VisibleForTesting
  public static void setCurrentCameraIntent(Intent dependency) {
    currentCameraIntent = dependency;
  }

  public static Intent getCurrentCameraIntent() {
    if (currentCameraIntent != null) {
      return currentCameraIntent;
    }
    return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
  }

  @VisibleForTesting
  public static void setCurrentLocationManager(LocationManager dependency) {
    currentLocationManager = dependency;
  }

  public static LocationManager getCurrentLocationManager(Context context) {
    if (currentLocationManager != null) {
      return currentLocationManager;
    }
    return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
  }

  @VisibleForTesting
  public static void setCurrentFirestore(FirebaseFirestore dependency) {
    currentFirestore = dependency;
  }

  public static FirebaseFirestore getCurrentFirestore() {
    if (currentFirestore != null) {
      return currentFirestore;
    }
    return FirebaseFirestore.getInstance();
  }

  public static String getDeviceId(@Nullable ContentResolver contentResolver) {
    if (contentResolver == null) {
      return "22f523fgg3";
    }
    return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
  }

  public static <T> CompletableFuture<T> getCurrentCompletableFuture() {
    return currentCompletableFuture;
  }

  public static void setCurrentCompletableFuture(CompletableFuture currentCompletableFuture) {
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
  public static void setCurrentViewModelClass(Class dependency) {
    currentViewModelClass = dependency;
  }

  public static Class getCurrentViewModelClass() {
    if (currentViewModelClass != null) {
      return currentViewModelClass;
    }
    return FavorViewModel.class;
  }

  @VisibleForTesting
  public static void setCurrentFavorRepository(FavorUtil dependency) {
    currentFavorRepository = dependency;
  }

  public static IFavorUtil getCurrentFavorRepository() {
    if (currentFavorRepository != null) return currentFavorRepository;
    return FavorUtil.getSingleInstance();
  }

  @VisibleForTesting
  public static void setCurrentUserRepository(IUserUtil dependency) {
    currentUserRepository = dependency;
  }

  public static IUserUtil getCurrentUserRepository() {
    if (currentUserRepository != null) return currentUserRepository;
    return UserUtil.getSingleInstance();
  }

  @VisibleForTesting
  public static void setCurrentFirebaseNotificationInstanceId(FirebaseInstanceId dependency) {
    currentFirebaseInstanceId = dependency;
  }

  public static FirebaseInstanceId getCurrentFirebaseNotificationInstanceId() {
    if (currentFirebaseInstanceId != null) return currentFirebaseInstanceId;
    return FirebaseInstanceId.getInstance();
  }

  public static IPictureUtil getCurrentPictureUtility() {
    if (currentPictureUtility != null) return currentPictureUtility;
    return PictureUtil.getInstance();
  }

  public static FirebaseStorage getCurrentFirebaseStorage() {
    if (currentFirebaseStorage != null) return currentFirebaseStorage;
    return FirebaseStorage.getInstance();
  }

  public static CacheUtil getCurrentCacheUtility() {
    if (currentPictureUtility != null) return currentCacheUtility;
    return CacheUtil.getInstance();
  }

  @VisibleForTesting
  public static void setCurrentPictureUtility(IPictureUtil pictureUtil) {
    currentPictureUtility = pictureUtil;
  }

  @VisibleForTesting
  public static void setCurrentFirebaseStorage(FirebaseStorage dependency) {
    currentFirebaseStorage = dependency;
  }

  @VisibleForTesting
  public static void setCurrentCacheUtility(CacheUtil cacheUtil) {
    currentCacheUtility = cacheUtil;
  }

  @VisibleForTesting
  public static void setCurrentChatUtility(IChatUtil chatUtil) {
    currentChatUtility = chatUtil;
  }

  public static IChatUtil getCurrentChatUtility() {
    return (currentChatUtility != null) ? currentChatUtility : ChatUtil.getSingleInstance();
  }
}
