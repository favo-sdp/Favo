package ch.epfl.favo.user;

import android.location.Location;

import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.favo.common.Document;
import ch.epfl.favo.common.FavoLocation;
import ch.epfl.favo.common.IllegalRequestException;

/**
 * This class contains all the relevant information about users TODO: It should implement parcelable
 * so that it can be injected in views
 */
public class User implements Document {

  public static final int MAX_ACCEPTING_FAVORS = 1;
  public static final int MAX_REQUESTING_FAVORS = 5;
  private String id;
  public static final String ID = "id";
  private String name;
  public static final String NAME = "name";
  private String email;
  public static final String EMAIL = "email";
  private String deviceId;
  public static final String DEVICE_ID = "deviceId";
  private String notificationId;
  public static final String NOTIFICATION_ID = "notificationId";
  private Date birthDate;
  public static final String BIRTH_DATE = "birthDate";
  private FavoLocation location;
  public static final String LOCATION = "location";
  private int activeAcceptingFavors;
  public static final String ACTIVE_ACCEPTING_FAVORS = "activeAcceptingFavors";
  private int activeRequestingFavors;
  public static final String ACTIVE_REQUESTING_FAVORS = "activeRequestingFavors";

  public User() {}

  public User(
      String id,
      String name,
      String email,
      String deviceId,
      Date birthDate,
      FavoLocation location) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.deviceId = deviceId;
    this.notificationId = null;
    this.birthDate = birthDate;
    this.location = location;
    this.activeAcceptingFavors = 0;
    this.activeRequestingFavors = 0;
  }

  public User(FirebaseUser firebaseUser, String deviceId, Location location) {
    this(
        firebaseUser.getUid(),
        firebaseUser.getDisplayName(),
        firebaseUser.getEmail(),
        deviceId,
        null,
        new FavoLocation(location));
  }

  // Getters
  @Override
  public String getId() {
    return id;
  }

  @Override
  public Map<String, Object> toMap() {
    return new HashMap<String, Object>() {
      {
        put(ID, id);
        put(NAME, name);
        put(EMAIL, email);
        put(DEVICE_ID, deviceId);
        put(NOTIFICATION_ID, notificationId);
        put(BIRTH_DATE, birthDate);
        put(LOCATION, location);
        put(ACTIVE_ACCEPTING_FAVORS, activeAcceptingFavors);
        put(ACTIVE_REQUESTING_FAVORS, activeRequestingFavors);
      }
    };
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public String getNotificationId() {
    return notificationId;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public Location getLocation() {
    return location;
  }

  public int getActiveAcceptingFavors() {
    return activeAcceptingFavors;
  }

  public int getActiveRequestingFavors() {
    return activeRequestingFavors;
  }

  public void setActiveAcceptingFavors(int totalAcceptingFavors) {
    if (totalAcceptingFavors < 0 || totalAcceptingFavors > MAX_ACCEPTING_FAVORS)
      throw new IllegalRequestException("Cannot accept");
    this.activeAcceptingFavors = totalAcceptingFavors;
  }

  public void setActiveRequestingFavors(int totalRequestingFavors) {
    if (totalRequestingFavors < 0 || totalRequestingFavors > MAX_REQUESTING_FAVORS)
      throw new IllegalRequestException("Cannot request");
    this.activeRequestingFavors = totalRequestingFavors;
  }

  public void setNotificationId(String notificationId) {
    this.notificationId = notificationId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  void setLocation(FavoLocation location) {
    this.location = location;
  }

  // Can only accept or request favors
  boolean canAccept() {
    return activeAcceptingFavors <= MAX_ACCEPTING_FAVORS;
  }

  boolean canRequest() {
    return activeRequestingFavors <= MAX_REQUESTING_FAVORS;
  }
}
