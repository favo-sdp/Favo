package ch.epfl.favo.util;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import ch.epfl.favo.common.CollectionWrapper;
import ch.epfl.favo.common.DatabaseUpdater;
import ch.epfl.favo.map.GpsTracker;
import ch.epfl.favo.map.Locator;

public class DependencyFactory {
  private static Locator currentGpsTracker;
  private static FirebaseUser currentUser;
  private static DatabaseUpdater currentDatabaseUpdater;
  private static Intent currentCameraIntent;
  private static LocationManager currentLocationManager;
  private static FirebaseFirestore currentFirestore;
  private static boolean offlineMode = false;
  private static boolean testMode = false;


  @RequiresApi(api = Build.VERSION_CODES.M)
  public static boolean isOfflineMode(Context context) {
    return offlineMode || CommonTools.isOffline(context);
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
  public static void setCurrentDatabaseUpdater(DatabaseUpdater dependency) {
    testMode = true;
    currentDatabaseUpdater = dependency;
  }

  public static DatabaseUpdater getCurrentDatabaseUpdater(String collectionReference, Class cls) {
    if (testMode && currentDatabaseUpdater != null) {
      return currentDatabaseUpdater;
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
}
