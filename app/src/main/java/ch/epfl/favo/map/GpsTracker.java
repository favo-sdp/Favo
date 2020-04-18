package ch.epfl.favo.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import ch.epfl.favo.common.NoPermissionGrantedException;
import ch.epfl.favo.common.NoPositionFoundException;
import ch.epfl.favo.util.DependencyFactory;

public class GpsTracker extends FragmentActivity implements LocationListener, Locator {

  private final Context context;
  private static Location mLastKnownLocation = null;
  private static long mLastUpdate = 0;

  public GpsTracker(Context context) {
    this.context = context;
  }

  /**
   * @throws NoPermissionGrantedException Should check if location permissions are granted
   * @throws RuntimeException Should check if location is finally found
   * @return the location of phone
   */

  public static void setLastKnownLocation(Location location){
    // update from mapPage, it has its own position request method based on callback
    mLastUpdate = System.currentTimeMillis();
    mLastKnownLocation = location;
  }

  public Location getLocation() throws NoPermissionGrantedException, NoPositionFoundException {
    // if last update of location is less than 5 minutes, then return last result
    if(mLastKnownLocation != null && ((System.currentTimeMillis() - mLastUpdate)/1000) < 300)
      return mLastKnownLocation;
    LocationManager locationManager = DependencyFactory.getCurrentLocationManager(context);
    boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
      String provider = null;
      if (isGPSEnabled) provider = LocationManager.GPS_PROVIDER;
      else if (isNetworkEnabled) provider = LocationManager.NETWORK_PROVIDER;
      if (provider != null) {
         locationManager.requestLocationUpdates(provider, 10000, 10, this);
        mLastKnownLocation = locationManager.getLastKnownLocation(provider);
      }
      if (mLastKnownLocation == null) {
        throw new NoPositionFoundException("Permission is granted, but no position is found");
      }
    } else throw new NoPermissionGrantedException("No location permission granted");
    mLastUpdate = System.currentTimeMillis();
    return mLastKnownLocation;
  }

  // followings are the default method if we implement LocationListener //
  public void onLocationChanged(Location location) {
    mLastKnownLocation = location;
    mLastUpdate = System.currentTimeMillis();
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
