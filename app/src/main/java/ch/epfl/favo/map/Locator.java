package ch.epfl.favo.map;

import android.location.Location;

import ch.epfl.favo.common.NoPermissionGrantedException;
import ch.epfl.favo.common.NoPositionFoundException;

public interface Locator {
    Location getLocation () throws NoPermissionGrantedException, NoPositionFoundException;
}
