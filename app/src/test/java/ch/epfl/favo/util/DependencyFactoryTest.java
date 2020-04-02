package ch.epfl.favo.util;

import android.location.LocationManager;

import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.favo.FakeFirebaseUser;

import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;

public class DependencyFactoryTest {

  @Before
  public void setup() {
    new DependencyFactory();
  }

  @Test
  public void testFirebaseUserDependency() {
    FirebaseUser testUser = new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER);
    DependencyFactory.setCurrentFirebaseUser(testUser);
    assertEquals(DependencyFactory.getCurrentFirebaseUser(), testUser);
  }

  @Test
  public void testLocationManagerDependency() {
    LocationManager locationManagerMock = mock(LocationManager.class);
    DependencyFactory.setCurrentLocationManager(locationManagerMock);
    assertEquals(
            locationManagerMock, DependencyFactory.getCurrentLocationManager(null));
  }
}
