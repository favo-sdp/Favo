package ch.epfl.favo.gps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import ch.epfl.favo.exception.NoPermissionGrantedException;
import ch.epfl.favo.exception.NoPositionFoundException;
import ch.epfl.favo.util.DependencyFactory;

public class GpsTracker extends FragmentActivity implements LocationListener, IGpsTracker {

  private final Context context;
  private static Location mLastKnownLocation = null;

  public GpsTracker(Context context) {
    this.context = context;
  }

  /**
   * @throws NoPermissionGrantedException Should check if location permissions are granted
   * @throws RuntimeException Should check if location is finally found
   * @return the location of phone
   */
  public static void setLastKnownLocation(Location location) {
    // update from mapPage, it has its own position request method based on callback
    mLastKnownLocation = location;
  }

  public Location getLocation() throws NoPermissionGrantedException, NoPositionFoundException {
    LocationManager locationManager = DependencyFactory.getCurrentLocationManager(context);
    boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
      if (isGPSEnabled) {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);
        mLastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      }
      else if(isNetworkEnabled){
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);
        mLastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
      }
    } else {
      throw new NoPermissionGrantedException("No location permission granted");
    }
    if (mLastKnownLocation == null)
      throw new NoPositionFoundException("Permission is granted, but no position is found");
    return mLastKnownLocation;
  }

  // followings are the default method if we implement LocationListener //
  public void onLocationChanged(Location location) {
    mLastKnownLocation = location;
  }

  public void onStatusChanged(String Provider, int status, Bundle extras) {
    // throw new NotImplementedException();
  }

  public void onProviderEnabled(String Provider) {
    // throw new NotImplementedException();
  }

  public void onProviderDisabled(String Provider) {
    // throw new NotImplementedException();
  }

  public IBinder onBind(Intent arg0) {
    return null;
  }
}
