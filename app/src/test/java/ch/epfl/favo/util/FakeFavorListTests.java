package ch.epfl.favo.util;

import android.location.Location;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.favo.TestConstants;
import ch.epfl.favo.favor.Favor;

import static org.junit.Assert.assertEquals;

public class FakeFavorListTests {
  Location location;

  @Before
  public void setup() {
    location = new Location("provider name");
    location.setLatitude(10.1);
    location.setLongitude(12.0);
  }

  @Test
  public void retrieveSingleFavorTest() {

    FakeFavorList fakeFavorList =
        new FakeFavorList(
            TestConstants.LATITUDE, TestConstants.LONGITUDE, System.currentTimeMillis());
    Favor favor0 = fakeFavorList.retrieveFavor(0, 0.001, 0.001);
    assertEquals(
        favor0.getLocation().getLatitude(),
        fakeFavorList.retrieveFavor(0, 0.001, 0.001).getLocation().getLatitude(),
        0.001);
  }

  @Test
  public void retrieveFavorListTest() {
    FakeFavorList fakeFavorList =
        new FakeFavorList(
            TestConstants.LATITUDE, TestConstants.LONGITUDE, System.currentTimeMillis());
    Favor favor0 = fakeFavorList.retrieveFavor(0, 0.001, 0.001);
    Favor favor1 = fakeFavorList.retrieveFavor(1, 0.001, -0.002);
    Favor favor2 = fakeFavorList.retrieveFavor(2, -0.002, -0.001);
    ArrayList<Favor> favorList = new ArrayList<>();
    favorList.add(favor0);
    favorList.add(favor1);
    favorList.add(favor2);
    assertEquals(
        favorList.get(0).getLocation().getLatitude(),
        fakeFavorList.retrieveFavorList().get(0).getLocation().getLatitude(),
        0.001);
  }
}
