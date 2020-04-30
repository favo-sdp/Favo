package ch.epfl.favo.gps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import ch.epfl.favo.exception.NoPermissionGrantedException;
import ch.epfl.favo.exception.NoPositionFoundException;
import ch.epfl.favo.util.DependencyFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GpsUnitTests extends FragmentActivity {

  private Context contextMock = mock(Context.class);
  private LocationManager locationManagerMock = mock(LocationManager.class);

  @Before
  public void setUP() {
    GpsTracker.setLastKnownLocation(null);
  }

  @Test
  public void NoLocationFoundTest() {
    DependencyFactory.setCurrentLocationManager(locationManagerMock);
    // Given a mocked Context injected into the object under test...
    when(locationManagerMock.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(false);
    when(locationManagerMock.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(false);
    assertThrows(NoPositionFoundException.class, () -> new GpsTracker(contextMock).getLocation());
  }

  @Test
  public void NoPermissionTest() {
    DependencyFactory.setCurrentLocationManager(locationManagerMock);
    when(ContextCompat.checkSelfPermission(contextMock, Manifest.permission.ACCESS_FINE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_DENIED);
    when(ContextCompat.checkSelfPermission(contextMock, Manifest.permission.ACCESS_COARSE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_DENIED);
    assertThrows(
        NoPermissionGrantedException.class, () -> new GpsTracker(contextMock).getLocation());
  }

  @Test
  public void FinePositionFoundTest() {
    DependencyFactory.setCurrentLocationManager(locationManagerMock);
    when(locationManagerMock.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true);
    when(locationManagerMock.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(false);
    Location mockLocation = mock(Location.class);
    when(mockLocation.getLatitude()).thenReturn(10.0);
    when(mockLocation.getLongitude()).thenReturn(15.2);
    when(locationManagerMock.getLastKnownLocation(LocationManager.GPS_PROVIDER))
        .thenReturn(mockLocation);
    GpsTracker gpsTracker = new GpsTracker(contextMock);
    double latitude = gpsTracker.getLocation().getLatitude();
    double longitude = gpsTracker.getLocation().getLongitude();
    assertEquals(latitude, 10.0, 0.01);
    assertEquals(longitude, 15.2, 0.01);
  }

  @Test
  public void CoarsePositionFoundTest() {
    DependencyFactory.setCurrentLocationManager(locationManagerMock);
    when(locationManagerMock.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(false);
    when(locationManagerMock.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(true);
    Location mockLocation = mock(Location.class);
    when(mockLocation.getLatitude()).thenReturn(4.0);
    when(mockLocation.getLongitude()).thenReturn(5.0);
    when(locationManagerMock.getLastKnownLocation(LocationManager.NETWORK_PROVIDER))
        .thenReturn(mockLocation);
    GpsTracker gpsTracker = new GpsTracker(contextMock);
    double latitude = gpsTracker.getLocation().getLatitude();
    double longitude = gpsTracker.getLocation().getLongitude();
    assertEquals(4.0, latitude, 0.01);
    assertEquals(5.0, longitude, 0.01);
  }

  @Test
  public void locationIsChanged() {
    GpsTracker gpsTracker = new GpsTracker(contextMock);
    gpsTracker.onLocationChanged(mock(Location.class));
    gpsTracker.onStatusChanged(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE, null);
    gpsTracker.onProviderDisabled(LocationManager.GPS_PROVIDER);
    gpsTracker.onProviderEnabled(LocationManager.GPS_PROVIDER);
    Intent intent = mock(Intent.class);
    assertNull(gpsTracker.onBind(intent));
  }
}
