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
    
    @Test
    public void NoLocationFoundTest() {
        Context contextMock = mock(Context.class);
        LocationManager locationManagerMock = mock(LocationManager.class);

        LocationManagerDependencyFactory.setCurrentLocationManager(locationManagerMock);
        PowerMockito.mockStatic(ContextCompat.class);
        PowerMockito.when(ContextCompat.checkSelfPermission(any(Context.class), anyString()))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        // Given a mocked Context injected into the object under test...
        when(locationManagerMock.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true);
        when(locationManagerMock.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(true);
        assertThrows(NoPositionFoundException.class, () -> new GpsTracker(contextMock).getLocation());
    }


    @Test
    public void NoPositionPermissionTest() {
        Context contextMock = mock(Context.class);
        LocationManager locationManagerMock = mock(LocationManager.class);
        LocationManagerDependencyFactory.setCurrentLocationManager(locationManagerMock);
        PowerMockito.mockStatic(ContextCompat.class);
        PowerMockito.when(ContextCompat.checkSelfPermission(any(Context.class), anyString()))
                .thenReturn(PackageManager.PERMISSION_DENIED);
        assertThrows(NoPermissionGrantedException.class, () -> new GpsTracker(contextMock).getLocation());
    }

    @Test
    public void PositionFoundTest() {
        Context contextMock = mock(Context.class);
        LocationManager locationManagerMock = mock(LocationManager.class);
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
        GpsTracker gpsTracker = new GpsTracker(contextMock);
        double latitude = gpsTracker.getLocation().getLatitude();
        double longitude = gpsTracker.getLocation().getLongitude();
        assertEquals(latitude, 10.0, 0.01);
        assertEquals(longitude, 15.2, 0.01);
    }

    @Test
    public void locationIsChanged() {
        Context contextMock = mock(Context.class);
        /* Set a mock location for debugging purposes */
        //setMockLocation(15.387653, 73.872585, 500);
        new GpsTracker(contextMock).onLocationChanged(mock(Location.class));

    }

    @Test
    public void StatusIsChanged() {
        Context contextMock = mock(Context.class);
        /* Set a mock location for debugging purposes */
        //setMockLocation(15.387653, 73.872585, 500);
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
        Context contextMock = mock(Context.class);
        /* Set a mock location for debugging purposes */
        //setMockLocation(15.387653, 73.872585, 500);
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
        Context contextMock = mock(Context.class);
        /* Set a mock location for debugging purposes */
        //setMockLocation(15.387653, 73.872585, 500);
        final Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        assertThrows(NotImplementedException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        new GpsTracker(contextMock).onProviderEnabled(LocationManager.GPS_PROVIDER);
                    }
                });
    }
}
