package ch.epfl.favo.common;

import android.location.Location;

public class FavoLocation extends Location {
  public FavoLocation() {
    super("FavoLocation");
  }

  public FavoLocation(String provider) {
    super(provider);
  }

  public FavoLocation(Location l) {
    super(l);
  }
}
