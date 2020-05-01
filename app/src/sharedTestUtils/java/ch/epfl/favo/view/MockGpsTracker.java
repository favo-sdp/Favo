package ch.epfl.favo.view;

import android.location.Location;

import ch.epfl.favo.common.NoPermissionGrantedException;
import ch.epfl.favo.common.NoPositionFoundException;
import ch.epfl.favo.map.IGpsTracker;

import static ch.epfl.favo.TestConstants.LOCATION;

public class MockGpsTracker implements IGpsTracker {
  private Location mLocation = LOCATION;

  @Override
  public Location getLocation() throws NoPermissionGrantedException, NoPositionFoundException {
    return mLocation;
  }

  public void setLocation(Location location) {
    this.mLocation = location;
  }
}
