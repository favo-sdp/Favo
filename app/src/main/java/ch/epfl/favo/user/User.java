package ch.epfl.favo.user;

import android.location.Location;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

import ch.epfl.favo.common.Document;
import ch.epfl.favo.common.FavoLocation;

/**
 * This class contains all the relevant information about users TODO: It should implement parcelable
 * so that it can be injected in views
 */
public class User implements Document {

  private String id;
  private String name;
  private String email;
  private String deviceId;
  private String notificationId;
  private Date birthDate;
  private FavoLocation location;
  private int activeAcceptingFavors;
  private int activeRequestingFavors;

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

  // Getters
  @Override
  public String getId() {
    return id;
  }

  @Override
  public Map<String, Object> toMap() {
    return null;
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
}
