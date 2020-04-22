package ch.epfl.favo;

import android.net.Uri;

import ch.epfl.favo.common.DatabaseWrapper;
import ch.epfl.favo.common.FavoLocation;
import ch.epfl.favo.favor.FavorStatus;

public class TestConstants {

  // User related test constants
  public static final String USER_ID = "FAKE USER ID";
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
  public static final double RADIUS = 134.56;
  public static final double LATITUDE = 46.5, LONGITUDE = 6.6;
  public static final FavorStatus FAVOR_STATUS = FavorStatus.REQUESTED;
  public static final String ACCEPTER_ID = "ASDFASDFASDF";
  public static final String PICTURE_URL = "https://favo.com/picture";
  public static final String OTHER_PICTURE_URL = "https://favo.com/otherPicture";

  // Notification related constants
  public static final String NOTIFICATION_TITLE = "title";
  public static final String NOTIFICATION_BODY = "body";
  public static final String FAVOR_ID = "WEZDZQD78A5SI5Q790SZAL7FW";

  // Test collection constant
  public static final String TEST_COLLECTION = "favors-test";
}

