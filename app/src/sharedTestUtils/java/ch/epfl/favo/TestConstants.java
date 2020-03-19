package ch.epfl.favo;

import android.location.Location;
import android.net.Uri;

public class TestConstants {

  // User related test constants
  public static final String USER_ID = "3487293";
  public static final String DEVICE_ID = "23a48d9hj";
  public static final String EMAIL = "test@example.com";
  public static final String NAME = "Test Testerson";
  public static final String USERNAME = "testerson123";
  public static final String PASSWORD = "fh498f30cpe";
  public static final String PROVIDER = "test provider";
  public static final Uri PHOTO_URI = Uri.parse("http://example.com/profile.png");

  // Favor related test constants
  public static final Location LOCATION = new Location("Dummy Provider");
  public static final double RADIUS = 134.56;
  public static final double LATITUDE = 46.5, LONGITUDE = 6.6;

  // Notification related constants
  public static final String NOTIFICATION_TITLE = "title";
  public static final String NOTIFICATION_BODY = "body";
}
