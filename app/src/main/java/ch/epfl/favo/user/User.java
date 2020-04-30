package ch.epfl.favo.user;

import android.location.Location;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.favo.common.Document;
import ch.epfl.favo.common.FavoLocation;

/**
 * This class contains all the relevant information about users TODO: It should implement parcelable
 * so that it can be injected in views
 */
public class User implements Document {

  // String constants for Map conversion
  public static final String ID = "id";
  public static final String NAME = "name";
  public static final String EMAIL = "email";
  public static final String DEVICE_ID = "deviceId";
  public static final String NOTIFICATION_ID = "notificationId";
  public static final String REQUESTED_FAVORS = "requestedFavors";
  public static final String ACCEPTED_FAVORS = "acceptedFavors";
  public static final String COMPLETED_FAVORS = "completedFavors";
  public static final String LIKES = "likes";
  public static final String DISLIKES = "dislikes";

  private String id;
  private String name;
  private String email;
  private String deviceId;
  private String notificationId;
  private Date birthDate;
  private FavoLocation location;
  private int activeAcceptingFavors;
  private int activeRequestingFavors;
  private int requestedFavors;
  private int acceptedFavors;
  private int completedFavors;
  private int likes;
  private int dislikes;

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
    this.requestedFavors = 0;
    this.acceptedFavors = 0;
    this.likes = 0;
    this.dislikes = 0;
    this.completedFavors = 0;
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
        put(REQUESTED_FAVORS, requestedFavors);
        put(ACCEPTED_FAVORS, acceptedFavors);
        put(COMPLETED_FAVORS, completedFavors);
        put(LIKES, likes);
        put(DISLIKES, dislikes);
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

  public void setActiveAcceptingFavors(int activeAcceptingFavors) {
    this.activeAcceptingFavors = activeAcceptingFavors;
  }

  public void setActiveRequestingFavors(int activeRequestingFavors) {
    this.activeRequestingFavors = activeRequestingFavors;
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
    return activeAcceptingFavors + activeRequestingFavors < 1;
  }

  boolean canRequest() {
    return activeAcceptingFavors + activeRequestingFavors < 1;
  }

  public int getRequestedFavors() {
    return requestedFavors;
  }

  public void setRequestedFavors(int requestedFavors) {
    this.requestedFavors = requestedFavors;
  }

  public int getAcceptedFavors() {
    return acceptedFavors;
  }

  public void setAcceptedFavors(int acceptedFavors) {
    this.acceptedFavors = acceptedFavors;
  }

  public int getCompletedFavors() {
    return completedFavors;
  }

  public void setCompletedFavors(int completedFavors) {
    this.completedFavors = completedFavors;
  }

  public int getLikes() {
    return likes;
  }

  public void setLikes(int likes) {
    this.likes = likes;
  }

  public int getDislikes() {
    return dislikes;
  }

  public void setDislikes(int dislikes) {
    this.dislikes = dislikes;
  }
}
