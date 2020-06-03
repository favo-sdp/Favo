package ch.epfl.favo.user;

import android.location.Location;

import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.favo.database.Document;
import ch.epfl.favo.exception.IllegalAcceptException;
import ch.epfl.favo.exception.IllegalRequestException;
import ch.epfl.favo.gps.FavoLocation;

/**
 * This class contains all the relevant information about users so that it can be injected in views
 */
public class User implements Document {

  static final int MAX_ACCEPTING_FAVORS = 1;
  static final int MAX_REQUESTING_FAVORS = 5;

  // String constants for Map conversion
  static final String ID = "id";
  public static final String NAME = "name";
  public static final String EMAIL = "email";
  private static final String DEVICE_ID = "deviceId";
  static final String NOTIFICATION_ID = "notificationId";
  static final String BIRTH_DATE = "birthDate";
  static final String LOCATION = "location";
  static final String ACTIVE_REQUESTING_FAVORS = "activeRequestingFavors";
  static final String ACTIVE_ACCEPTING_FAVORS = "activeAcceptingFavors";
  private static final String REQUESTED_FAVORS = "requestedFavors";
  private static final String ACCEPTED_FAVORS = "acceptedFavors";
  private static final String COMPLETED_FAVORS = "completedFavors";
  private static final String LIKES = "likes";
  private static final String DISLIKES = "dislikes";
  private static final String BALANCE = "balance";
  private static final String NOTIFICATION_RADIUS = "notificationRadius";
  private static final String CHAT_NOTIFICATIONS = "chatNotifications";
  private static final String UPDATE_NOTIFICATIONS = "updateNotifications";

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
  private double balance;
  private double notificationRadius;
  private boolean chatNotifications;
  private boolean updateNotifications;
  private String profilePicUrl;

  public User() {
    this.activeAcceptingFavors = 0;
    this.activeRequestingFavors = 0;
    this.requestedFavors = 0;
    this.acceptedFavors = 0;
    this.likes = 0;
    this.dislikes = 0;
    this.completedFavors = 0;
    this.balance = 10.0;
    this.notificationRadius = 10.0;
    this.updateNotifications = true;
    this.chatNotifications = true;
  }

  public User(Map<String, Object> map) {
    this.id = (String) map.get(ID);
    this.name = (String) map.get(NAME);
    this.email = (String) map.get(EMAIL);
    this.deviceId = (String) map.get(DEVICE_ID);
    this.notificationId = (String) map.get(NOTIFICATION_ID);
    this.birthDate = (Date) map.get(BIRTH_DATE);
    this.location = (FavoLocation) map.get(LOCATION);
    this.activeAcceptingFavors = (int) map.get(ACTIVE_ACCEPTING_FAVORS);
    this.activeRequestingFavors = (int) map.get(ACTIVE_REQUESTING_FAVORS);
    this.requestedFavors = (int) map.get(REQUESTED_FAVORS);
    this.acceptedFavors = (int) map.get(ACCEPTED_FAVORS);
    this.completedFavors = (int) map.get(COMPLETED_FAVORS);
    this.likes = (int) map.get(LIKES);
    this.dislikes = (int) map.get(DISLIKES);
    this.balance = (double) map.get(BALANCE);
    this.notificationRadius = (double) map.get(NOTIFICATION_RADIUS);
    this.chatNotifications = (boolean) map.get(CHAT_NOTIFICATIONS);
    this.updateNotifications = (boolean) map.get(UPDATE_NOTIFICATIONS);
  }

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
    this.balance = 10.0;
    this.notificationRadius = 10.0;
    this.updateNotifications = true;
    this.chatNotifications = true;
  }

  public User(FirebaseUser firebaseUser, String deviceId, Location location) {
    this(
        firebaseUser.getUid(),
        firebaseUser.getDisplayName(),
        firebaseUser.getEmail(),
        deviceId,
        null,
        new FavoLocation(location));
    profilePicUrl =
        (firebaseUser.getPhotoUrl() != null) ? firebaseUser.getPhotoUrl().toString() : null;
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
        put(ACTIVE_REQUESTING_FAVORS, activeRequestingFavors);
        put(ACTIVE_ACCEPTING_FAVORS, activeAcceptingFavors);
        put(REQUESTED_FAVORS, requestedFavors);
        put(ACCEPTED_FAVORS, acceptedFavors);
        put(COMPLETED_FAVORS, completedFavors);
        put(LIKES, likes);
        put(DISLIKES, dislikes);
        put(BALANCE, balance);
        put(NOTIFICATION_RADIUS, notificationRadius);
        put(CHAT_NOTIFICATIONS, chatNotifications);
        put(UPDATE_NOTIFICATIONS, updateNotifications);
      }
    };
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public double getBalance() {
    return balance;
  }

  Date getBirthDate() {
    return birthDate;
  }

  public Location getLocation() {
    return location;
  }

  int getActiveAcceptingFavors() {
    return activeAcceptingFavors;
  }

  int getActiveRequestingFavors() {
    return activeRequestingFavors;
  }

  String getPictureUrl() {
    return profilePicUrl;
  }

  void setProfilePicUrl(String url) {
    profilePicUrl = url;
  }

  void setActiveAcceptingFavors(int totalAcceptingFavors) {
    if (totalAcceptingFavors < 0 || totalAcceptingFavors > MAX_ACCEPTING_FAVORS)
      //throw new IllegalAcceptException("Cannot accept");
    this.activeAcceptingFavors = totalAcceptingFavors;
  }

  void setActiveRequestingFavors(int totalRequestingFavors) {
    if (totalRequestingFavors < 0 || totalRequestingFavors > MAX_REQUESTING_FAVORS)
      //throw new IllegalRequestException("Cannot request");
    this.activeRequestingFavors = totalRequestingFavors;
  }

  public void setNotificationId(String notificationId) {
    this.notificationId = notificationId;
  }

  void setBalance(Double balance) {
    this.balance = balance;
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

  public int getRequestedFavors() {
    return requestedFavors;
  }

  void setRequestedFavors(int requestedFavors) {
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

  void setCompletedFavors(int completedFavors) {
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

  public double getNotificationRadius() {
    return notificationRadius;
  }

  public boolean isChatNotifications() {
    return chatNotifications;
  }

  public boolean isUpdateNotifications() {
    return updateNotifications;
  }

  public void setNotificationRadius(double notificationRadius) {
    this.notificationRadius = notificationRadius;
  }

  public void setChatNotifications(boolean chatNotifications) {
    this.chatNotifications = chatNotifications;
  }

  public void setUpdateNotifications(boolean updateNotifications) {
    this.updateNotifications = updateNotifications;
  }
}
