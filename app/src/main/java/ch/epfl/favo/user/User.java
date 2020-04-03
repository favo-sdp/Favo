package ch.epfl.favo.user;

import android.location.Location;

import java.time.LocalDate;

import ch.epfl.favo.common.DatabaseWrapper;
import ch.epfl.favo.common.Document;

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
  private LocalDate birthDate;
  private Location location;
  private int activeAcceptingFavors;
  private int activeRequestingFavors;

  public User() {}

  public User(String name, String email, String deviceId, LocalDate birthDate, Location location) {
    this.id = DatabaseWrapper.generateRandomId();
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

  public LocalDate getBirthDate() {
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

  void setActiveAcceptingFavors(int activeAcceptingFavors) {
    this.activeAcceptingFavors = activeAcceptingFavors;
  }

  void setActiveRequestingFavors(int activeRequestingFavors) {
    this.activeRequestingFavors = activeRequestingFavors;
  }

  void setNotificationId(String notificationId) {
    this.notificationId = notificationId;
  }

  void setLocation(Location location) {
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
