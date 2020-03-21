package ch.epfl.favo.util;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.favo.common.CollectionWrapper;
import ch.epfl.favo.common.DatabaseUpdater;
import ch.epfl.favo.map.GpsTracker;
import ch.epfl.favo.map.Locator;

public class DependencyFactory {
  private static Locator currentGpsTracker;
  private static FirebaseUser currentUser;
  private static DatabaseUpdater currentDatabaseUpdater;
  private static boolean testMode = false;

  public static FirebaseUser getCurrentFirebaseUser() {
    if (testMode && currentUser != null) {
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

  public static DatabaseUpdater getCurrentDatabaseUpdater(String collectionReference) {
    if (testMode && currentGpsTracker != null) {
      return currentDatabaseUpdater;
    }
    return new CollectionWrapper(collectionReference);
  }
}
