package ch.epfl.favo;

import android.net.Uri;

import ch.epfl.favo.common.DatabaseWrapper;
import ch.epfl.favo.common.FavoLocation;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;

public class TestConstants {

  // User related test constants
  public static final String USER_ID = "3487293";
  public static final String DEVICE_ID = "23a48d9hj";
  public static final String EMAIL = "test@example.com";
  public static final String NAME = "Test Testerson";
  public static final String USERNAME = "testerson123";
  public static final String PROVIDER = "test provider";
  public static final Uri PHOTO_URI = Uri.parse("http://example.com/profile.png");

  // Favor related test constants
  public static final String TITLE = "fake test title";
  public static final String DESCRIPTION = "fake test description";
  public static final String REQUESTER_ID = DatabaseWrapper.generateRandomId();
  public static final FavoLocation LOCATION = new FavoLocation(PROVIDER);
  public static final int STATUS_ID = FavorStatus.REQUESTED.toInt();
  public static final double RADIUS = 134.56;
  public static final double LATITUDE = 46.5, LONGITUDE = 6.6;
  public static final String ACCEPTER_ID = "ASDFASDFASDF";

  // Notification related constants
  public static final String NOTIFICATION_TITLE = "title";
  public static final String NOTIFICATION_BODY = "body";
  public static final String FAVOR_ID = "WEZDZQD78A5SI5Q790SZAL7FW";
}
