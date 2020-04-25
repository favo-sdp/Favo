package ch.epfl.favo.gps;

import android.location.Location;

import ch.epfl.favo.exception.NoPermissionGrantedException;
import ch.epfl.favo.exception.NoPositionFoundException;

public interface Locator {
  Location getLocation() throws NoPermissionGrantedException, NoPositionFoundException;
}
