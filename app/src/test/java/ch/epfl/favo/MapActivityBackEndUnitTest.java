package ch.epfl.favo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.test.mock.MockContext;

import androidx.core.content.ContextCompat;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import ch.epfl.favo.exceptions.NoPermissionGrantedException;
import ch.epfl.favo.exceptions.NoPositionFoundException;
import ch.epfl.favo.exceptions.NotImplementedException;
import ch.epfl.favo.models.GpsTracker;
import ch.epfl.favo.models.LocationManagerDependencyFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest(ContextCompat.class)
public class MapActivityBackEndUnitTest {

    @Mock
    LocationManager locationManagerMock = mock(LocationManager.class);
    MockContext contextMock = mock(MockContext.class);

    private void setMockLocation(double latitude, double longitude, float accuracy) {

        LocationManager lm = locationManagerMock;

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onLocationChanged(Location location) {
            }
        });


        lm.addTestProvider(LocationManager.GPS_PROVIDER,
                "requiresNetwork" == "",
                "requiresSatellite" == "",
                "requiresCell" == "",
                "hasMonetaryCost" == "",
                "supportsAltitude" == "",
                "supportsSpeed" == "",
                "supportsBearing" == "",
                android.location.Criteria.POWER_LOW,
                android.location.Criteria.ACCURACY_FINE);

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);

        newLocation.setLatitude(latitude);
        newLocation.setLongitude(longitude);
        newLocation.setAccuracy(accuracy);

        lm.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
        lm.setTestProviderStatus(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
        lm.setTestProviderLocation(LocationManager.GPS_PROVIDER, newLocation);
    }

    @Test
    public void NoLocationFoundTest() {
        LocationManagerDependencyFactory.setCurrentLocationManager(locationManagerMock);
        PowerMockito.mockStatic(ContextCompat.class);
        PowerMockito.when(ContextCompat.checkSelfPermission(any(Context.class), anyString()))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        // Given a mocked Context injected into the object under test...
        when(locationManagerMock.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true);
        when(locationManagerMock.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(true);
        assertThrows(NoPositionFoundException.class, () -> new GpsTracker().getLocation());
    }


    @Test
    public void NoPositionPermissionTest() {
        LocationManagerDependencyFactory.setCurrentLocationManager(locationManagerMock);
        PowerMockito.mockStatic(ContextCompat.class);
        PowerMockito.when(ContextCompat.checkSelfPermission(any(Context.class), anyString()))
                .thenReturn(PackageManager.PERMISSION_DENIED);
        assertThrows(NoPermissionGrantedException.class, () -> new GpsTracker().getLocation());
    }

    @Test
    public void PositionFoundTest() {
        LocationManagerDependencyFactory.setCurrentLocationManager(locationManagerMock);
        PowerMockito.mockStatic(ContextCompat.class);
        PowerMockito.when(ContextCompat.checkSelfPermission(any(Context.class), anyString()))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(locationManagerMock.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true);
        when(locationManagerMock.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(true);
        Location mockLocation = mock(Location.class);
        when(mockLocation.getLatitude()).thenReturn(10.0);
        when(mockLocation.getLongitude()).thenReturn(15.2);
        when(locationManagerMock.getLastKnownLocation(LocationManager.GPS_PROVIDER)).thenReturn(mockLocation);
        GpsTracker gpsTracker = new GpsTracker();
        double latitude = gpsTracker.getLocation().getLatitude();
        double longitude = gpsTracker.getLocation().getLongitude();
        assertEquals(latitude, 10.0, 0.01);
        assertEquals(longitude, 15.2, 0.01);
    }

    @Test
    public void locationIsChanged() {
        /* Set a mock location for debugging purposes */
        //setMockLocation(15.387653, 73.872585, 500);
        final Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        assertThrows(NotImplementedException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        new GpsTracker().onLocationChanged(newLocation);
                    }
                });
    }

    @Test
    public void StatusIsChanged() {
        /* Set a mock location for debugging purposes */
        //setMockLocation(15.387653, 73.872585, 500);
        final Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        assertThrows(NotImplementedException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        new GpsTracker().onStatusChanged(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE, null);
                    }
                });
    }

    @Test
    public void ProviderIsDisabled() {
        /* Set a mock location for debugging purposes */
        //setMockLocation(15.387653, 73.872585, 500);
        final Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        assertThrows(NotImplementedException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        new GpsTracker().onProviderDisabled(LocationManager.GPS_PROVIDER);
                    }
                });
    }

    @Test
    public void ProviderIsEnabled() {
        /* Set a mock location for debugging purposes */
        //setMockLocation(15.387653, 73.872585, 500);
        final Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        assertThrows(NotImplementedException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        new GpsTracker().onProviderEnabled(LocationManager.GPS_PROVIDER);
                    }
                });
    }
}
