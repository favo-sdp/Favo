package ch.epfl.favo;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import ch.epfl.favo.database.DatabaseWrapper;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.gps.FavoLocation;

public class TestConstants {

  // User related test constants
  public static final String USER_ID = "FAKE USER ID";
  public static final String DEVICE_ID = "23a48d9hj";
  public static final String EMAIL = "test@example.com";
  public static final String NAME = "Test Testerson";
  public static final String PROVIDER = "test provider";
  public static final Uri PHOTO_URI = Uri.parse("http://example.com/profile.png");
  public static final double NOTIFICATION_RADIUS = 1.0;
  public static final boolean DEFAULT_NOTIFICATION_PREFERENCE = false;
  public static final String PROFILE_PICTURE_ID = "APA2DQS78A7SI3Q790SZAL3FW";

  // Favor related test constants
  public static final String TITLE = "fake test title";
  public static final String DESCRIPTION = "fake test description";
  public static final String REQUESTER_ID = DatabaseWrapper.generateRandomId();
  public static final FavoLocation LOCATION = new FavoLocation(PROVIDER);
  public static final Date BIRTHDAY = new Date(0);
  public static final double REWARD = 3;
  public static final FavorStatus FAVOR_STATUS = FavorStatus.REQUESTED;
  public static final String ACCEPTER_ID = "ASDFASDFASDF";
  public static final String PICTURE_URL = "https://favo.com/picture";
  public static final String OTHER_PICTURE_URL = "https://favo.com/otherPicture";
  public static final String REQUESTER_NOTIFICATION_ID = "requesterNotificationId";
  public static final ArrayList<String> USER_IDS =
      new ArrayList<>(Arrays.asList(REQUESTER_ID, ACCEPTER_ID));
  // Notification related constants
  public static final String NOTIFICATION_TITLE = "title";
  public static final String NOTIFICATION_BODY = "body";
  public static final String FAVOR_ID = "WEZDZQD78A5SI5Q790SZAL7FW";

  // Test collection constant
  public static final String TEST_COLLECTION = "favors-test";

  // Message constants
  public static final String MESSAGE_ID = "testId";
  public static final String MESSAGE_USER_NAME = "testName";
  public static final String MESSAGE_VALUE = "testMessage";
  public static final String MESSAGE_IMAGE_PATH = "testImagePath";
  public static final String MESSAGE_USER_ID = "testUid";
  public static final String MESSAGE_LATITUDE = "1.23";
  public static final String MESSAGE_LONGITUDE = "1.25";
}
