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

import ch.epfl.favo.exception.NoPermissionGrantedException;
import ch.epfl.favo.exception.NoPositionFoundException;
import ch.epfl.favo.util.DependencyFactory;

public class GpsTracker implements LocationListener, IGpsTracker {
  private static final int millisecToSec = 1000; // for converting unit from millisecond to second
  private static final int minInterval =
      60; // minimum time interval for requesting new location, unit in second
  private static final int minTime =
      10000; // minimum time interval for updating system location, unit in millisecond
  private static final int minDistance = 10; // minimum distance difference, unit in meter

  private final Context context;
  private static Location mLastKnownLocation = null;
  private static long mLastUpdate = 0;

  public GpsTracker(Context context) {
    this.context = context;
  }

  public static void setLastKnownLocation(Location location) {
    // update from mapPage
    mLastUpdate = System.currentTimeMillis();
    mLastKnownLocation = location;
  }

  /**
   * @throws NoPermissionGrantedException Should check if location permissions are granted
   * @throws RuntimeException Should check if location is finally found
   * @return the location of phone
   */
  public Location getLocation() throws NoPermissionGrantedException, NoPositionFoundException {
    if (mLastKnownLocation != null
        && ((System.currentTimeMillis() - mLastUpdate) / millisecToSec) < minInterval)
      return mLastKnownLocation;

    LocationManager locationManager = DependencyFactory.getCurrentLocationManager(context);
    boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
      if (isGPSEnabled) {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, minTime, minDistance, this);
        mLastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      } else if (isNetworkEnabled) {
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER, minTime, minDistance, this);
        mLastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
      }
    } else {
      throw new NoPermissionGrantedException("No location permission granted");
    }
    if (mLastKnownLocation == null)
      throw new NoPositionFoundException("Permission is granted, but no position is found");
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
