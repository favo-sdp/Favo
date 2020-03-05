package ch.epfl.favo.models;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class LocationManagerDependencyFactory  extends Service  {

    private static boolean variableSet = false;
    private static LocationManager currentLocationManager;

    public static LocationManager getCurrentLocationManager(Context context) {
        if (variableSet) {
            return currentLocationManager;
        }
        return (LocationManager) context.getSystemService(LOCATION_SERVICE);
    }

    public static void setCurrentLocationManager(LocationManager dependency) {
        variableSet = true;
        LocationManagerDependencyFactory.currentLocationManager = dependency;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
