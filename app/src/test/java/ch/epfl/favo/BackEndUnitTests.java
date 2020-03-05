package ch.epfl.favo;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.test.mock.MockContext;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.mockito.Mock;

import java.util.Random;

import ch.epfl.favo.exceptions.NotImplementedException;
import ch.epfl.favo.models.FavorUtil;
import ch.epfl.favo.models.GpsTracker;
import ch.epfl.favo.models.UserUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BackEndUnitTests {
    @Test
    public void userNameIsValid() {
        String username = "as;dfjlasdfkja;skldfm";
        String pw = "valid_pw";
        assertThrows(NotImplementedException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        UserUtil.getSingleInstance().createAccount(username, pw);
                    }
                });
    }

    @Test
    public void userShouldNotLoginWithInvalidPassword() {
        String username = "valid_user";
        String pw = generateRandomString(10);
        assertThrows(NotImplementedException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        UserUtil.getSingleInstance().logInAccount(username, pw);
                    }
                });
    }


    @Test
    public void favorIsNotLongerThan300Characters() {
        String title = "Sample Favor";
        String description = generateRandomString(305);
        String location = "valid location"; //replace by valid location
        assertThrows(NotImplementedException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        FavorUtil.getSingleInstance().postFavor(title, description, location);
                    }
                });
    }

    @Test
    public void userCanLogOutOnlyIfLoggedIn() {

        assertThrows(NotImplementedException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        UserUtil.getSingleInstance().logOutAccount();
                    }
                });
    }

    @Test
    public void userCanDeleteAccountOnlyIfAccountExists() {

        assertThrows(NotImplementedException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        UserUtil.getSingleInstance().deleteAccount();
                    }
                });
    }

    public String generateRandomString(int targetStringLength) {
        String title = "sample_favor";
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        String generatedString = new Random().ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }

    @Mock
    LocationManager locationManagerMock = mock(LocationManager.class);
    MockContext contextMock = mock(MockContext.class);

    private void setMockLocation(double latitude, double longitude, float accuracy) {

        LocationManager lm = null; //= (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
            @Override
            public void onLocationChanged(Location location) {}
        });


        lm.addTestProvider (LocationManager.GPS_PROVIDER,
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
        lm.setTestProviderStatus(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE,null,System.currentTimeMillis());
        lm.setTestProviderLocation(LocationManager.GPS_PROVIDER, newLocation);
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
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