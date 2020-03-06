package ch.epfl.favo.map;
/*
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;

//import androidx.core.content.ContextCompat;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import ch.epfl.favo.common.NoPermissionGrantedException;
import ch.epfl.favo.common.NoPositionFoundException;
import ch.epfl.favo.common.NotImplementedException;
import ch.epfl.favo.util.LocationManagerDependencyFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

*/
public class MapUnitTest {


/*
  //  Context contextMock = mock(Context.class);
//    LocationManager locationManagerMock = mock(LocationManager.class);

    @Test
    public void NoLocationFoundTest() {
        Context contextMock = mock(Context.class);
        LocationManager locationManagerMock = mock(LocationManager.class);
        LocationManagerDependencyFactory.setCurrentLocationManager(locationManagerMock);
        // Given a mocked Context injected into the object under test...
        when(locationManagerMock.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true);
        when(locationManagerMock.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(true);
        assertThrows(NoPositionFoundException.class, () -> new GpsTracker(contextMock).getLocation());
    }
/*

    @Test
    public void NoPermissionTest() {
        LocationManagerDependencyFactory.setCurrentLocationManager(locationManagerMock);
        when(ContextCompat.checkSelfPermission(contextMock,
                Manifest.permission.ACCESS_FINE_LOCATION))
                .thenReturn(PackageManager.PERMISSION_DENIED);
        when(ContextCompat.checkSelfPermission(contextMock,
                Manifest.permission.ACCESS_COARSE_LOCATION))
                .thenReturn(PackageManager.PERMISSION_DENIED);
        assertThrows(NoPermissionGrantedException.class, () -> new GpsTracker(contextMock).getLocation());
    }

    @Test
    public void FinePositionFoundTest() {
        LocationManagerDependencyFactory.setCurrentLocationManager(locationManagerMock);
        when(locationManagerMock.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true);
        when(locationManagerMock.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(false);
        Location mockLocation = mock(Location.class);
        when(mockLocation.getLatitude()).thenReturn(10.0);
        when(mockLocation.getLongitude()).thenReturn(15.2);
        when(locationManagerMock.getLastKnownLocation(LocationManager.GPS_PROVIDER)).thenReturn(mockLocation);
        GpsTracker gpsTracker = new GpsTracker(contextMock);
        double latitude = gpsTracker.getLocation().getLatitude();
        double longitude = gpsTracker.getLocation().getLongitude();
        assertEquals(latitude, 10.0, 0.01);
        assertEquals(longitude, 15.2, 0.01);
    }

    @Test
    public void CoarsePositionFoundTest() {
        LocationManagerDependencyFactory.setCurrentLocationManager(locationManagerMock);
        when(locationManagerMock.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(false);
        when(locationManagerMock.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(true);
        Location mockLocation = mock(Location.class);
        when(mockLocation.getLatitude()).thenReturn(4.0);
        when(mockLocation.getLongitude()).thenReturn(5.0);
        when(locationManagerMock.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)).thenReturn(mockLocation);
        GpsTracker gpsTracker = new GpsTracker(contextMock);
        double latitude = gpsTracker.getLocation().getLatitude();
        double longitude = gpsTracker.getLocation().getLongitude();
        assertEquals(latitude, 4.0, 0.01);
        assertEquals(longitude, 5.0, 0.01);
    }
*/
/*
    @Test
    public void locationIsChanged() {
        //new GpsTracker(contextMock).onLocationChanged(mock(Location.class));
    }

    @Test
    public void StatusIsChanged() {
        final Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        assertThrows(NotImplementedException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        new GpsTracker(contextMock).onStatusChanged(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE, null);
                    }
                });
    }

    @Test
    public void ProviderIsDisabled() {
        final Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        assertThrows(NotImplementedException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        new GpsTracker(contextMock).onProviderDisabled(LocationManager.GPS_PROVIDER);
                    }
                });
    }

    @Test
    public void ProviderIsEnabled() {
        final Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        assertThrows(NotImplementedException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        new GpsTracker(contextMock).onProviderEnabled(LocationManager.GPS_PROVIDER);
                    }
                });
    }*/
}