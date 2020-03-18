package ch.epfl.favo.util;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import static junit.framework.TestCase.assertNotSame;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

public class LocationManagerDependencyFacotryTest {

    @Test
    public void testLocationManagerDependency() {
        LocationManager locationManagerMock = mock(LocationManager.class);
        LocationManagerDependencyFactory.setCurrentLocationManager(locationManagerMock);
        assertEquals(
                locationManagerMock, LocationManagerDependencyFactory.getCurrentLocationManager(null));
    }

    @Test
    public void DefaultGetCurrentLocationManager(){
        Context context = mock(Context.class);
        LocationManager locationManagerMock = mock(LocationManager.class);
        when(context.getSystemService(Context.LOCATION_SERVICE)).thenReturn(locationManagerMock);
        assertNotSame(locationManagerMock, LocationManagerDependencyFactory.getCurrentLocationManager(context));
    }

    @Test
    public void onBindTest(){
        Intent intent = mock(Intent.class);
        LocationManagerDependencyFactory locationManagerDependencyFactory = new LocationManagerDependencyFactory();
        assertNull(locationManagerDependencyFactory.onBind(intent));
    }
}
