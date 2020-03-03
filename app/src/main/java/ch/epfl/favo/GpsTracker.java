package ch.epfl.favo;

import android.Manifest;
import android.app.Service;
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

import ch.epfl.favo.exception.NoPermissionGrantedException;
import ch.epfl.favo.exception.NotImplementedException;

/**
 * This will wrap the getting system location function with some permission checking,
 * before using this class, two lines need to be added to AndroidManifest.xml file:
 *     <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 *     <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
 * Sometimes, the location permission is not automatically granted when installing app on phone,
 * you need to grant it on your phone manually. When permission is not granted, app tends to close
 * immediately after launching map view. And when running on a virtual device, the position does
 * not seems consist with expectation. But this will not happen on a real phone.
 */
public class GpsTracker extends Service implements LocationListener {

    private final Context context;
    boolean isGPSEnabled =false;
    boolean isNetworkEnabled =false;

    Location location;
    protected LocationManager locationManager;

    public GpsTracker(Context context){
        this.context=context;
    }

    public GpsTracker(){
        this.context = null;
    }

    /**
     * @throws NoPermissionGrantedException Should check if location permissions are granted
     * @throws RuntimeException Should check if location is finally found
     * @return the location of phone
     */
    public  Location getLocation() throws NoPermissionGrantedException, RuntimeException {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
        isNetworkEnabled=locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ){

            if(isGPSEnabled){
                if(location==null){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,10,this);
                    if(locationManager!=null){
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        Log.d("report", "position found by gps");
                    }
                }
            }
            // if location is not found from GPS then it will be found from network
            if(location==null){
                if(isNetworkEnabled){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000,10,this);
                    if(locationManager!=null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        Log.d("report", "position found by network");
                    }
                }
            }
            if(location == null) throw new RuntimeException("Permission is granted, but no position is found");
        }
        else
            throw new NoPermissionGrantedException("No location permission granted");
        return location;
    }

    // followings are the default method if we implement LocationListener //
    public void onLocationChanged(Location location){
        throw new NotImplementedException();
    }

    public void onStatusChanged(String Provider, int status, Bundle extras){
        throw new NotImplementedException();
    }
    public void onProviderEnabled(String Provider){
        throw new NotImplementedException();
    }
    public void onProviderDisabled(String Provider){
        throw new NotImplementedException();
    }
    public IBinder onBind(Intent arg0){
        return null;
    }

}
