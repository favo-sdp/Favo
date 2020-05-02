package ch.epfl.favo.gps;

import android.location.Location;

import org.junit.Test;

import ch.epfl.favo.gps.FavoLocation;

import static org.junit.Assert.assertEquals;

public class FavoLocationTest {

  private static final String PROVIDER = "PROVIDER";

  @Test
  public void defaultNoArgConstructor() {
    FavoLocation location = new FavoLocation();
    assertEquals(null, location.getProvider());
  }

  @Test
  public void providerConstructor() {
    FavoLocation location = new FavoLocation(PROVIDER);
    assertEquals(null, location.getProvider());
  }

  @Test
  public void locationConstructor() {
    FavoLocation location = new FavoLocation(new Location(PROVIDER));
    assertEquals(null, location.getProvider());
  }
}
