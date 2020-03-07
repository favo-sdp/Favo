package ch.epfl.favo.util;

import android.content.Context;
import android.location.LocationManager;

import androidx.annotation.VisibleForTesting;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.Context.LOCATION_SERVICE;

public class DependencyFactory {

  private static FirebaseUser currentUser;
  private static LocationManager currentLocationManager;
  private static boolean testMode = false;

  public static LocationManager getCurrentLocationManager(Context context) {
    if (testMode) {
      return currentLocationManager;
    }
    return (LocationManager) context.getSystemService(LOCATION_SERVICE);
  }

  @VisibleForTesting
  public static void setCurrentLocationManager(LocationManager dependency) {
    testMode = true;
    currentLocationManager = dependency;
  }

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
}
