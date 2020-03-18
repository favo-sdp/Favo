package ch.epfl.favo.util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class LocationManagerDependencyFactory extends Service {

  private static boolean testMode = false;
  private static LocationManager currentLocationManager;

  public static LocationManager getCurrentLocationManager(Context context) {
    if (testMode) {
      return currentLocationManager;
    }
    return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
  }

  public static void setCurrentLocationManager(LocationManager dependency) {
    testMode = true;
    LocationManagerDependencyFactory.currentLocationManager = dependency;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
