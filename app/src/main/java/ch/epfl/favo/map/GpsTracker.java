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

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import ch.epfl.favo.common.NoPermissionGrantedException;
import ch.epfl.favo.common.NoPositionFoundException;
import ch.epfl.favo.util.LocationManagerDependencyFactory;

/**
 * This will wrap the getting system location function with some permission checking, before using
 * this class, two lines need to be added to AndroidManifest.xml file: <uses-permission
 * android:name="android.permission.ACCESS_FINE_LOCATION" /> <uses-permission
 * android:name="android.permission.ACCESS_COARSE_LOCATION" /> Sometimes, the location permission is
 * not automatically granted when installing app on phone, you need to grant it on your phone
 * manually. When permission is not granted, app tends to close immediately after launching map
 * view. And when running on a virtual device, the position does not seems consist with expectation.
 * But this will not happen on a real phone.
 */
public class GpsTracker extends FragmentActivity implements LocationListener, Locator {

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

  public static void setLastKnownLocation(Location location){
    mLastKnownLocation = location;
  }

  public Location getLocation() throws NoPermissionGrantedException, NoPositionFoundException {
    if(mLastKnownLocation != null)
      return mLastKnownLocation;
    Location location = null;
    LocationManager locationManager = LocationManagerDependencyFactory.getCurrentLocationManager(context);
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
         location = locationManager.getLastKnownLocation(provider);
      }
      if (location == null) {
        throw new NoPositionFoundException("Permission is granted, but no position is found");
      }
    } else throw new NoPermissionGrantedException("No location permission granted");
    return location;
  }

  // followings are the default method if we implement LocationListener //
  public void onLocationChanged(Location location) {
    mLastKnownLocation = location;
    // throw new NotImplementedException();
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
