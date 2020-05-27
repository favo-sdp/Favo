package ch.epfl.favo.gps;

import android.location.Location;

public class FavoLocation extends Location {
  public static final double EARTH_RADIUS = 6371.0;

  public FavoLocation() {
    super("FavoLocation");
  }

  public FavoLocation(String provider) {
    super(provider);
  }

  public FavoLocation(Location l) {
    super(l);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    /* Check if o is an instance of Complex or not
    "null instanceof [type]" also returns false */
    if (!(o instanceof FavoLocation)) {
      return false;
    }
    FavoLocation other = (FavoLocation) o;

    return this.getLongitude() == other.getLongitude() && this.getLatitude() == other.getLatitude();
  }
}
