package ch.epfl.favo.user;

import java.time.LocalDate;

/**
 * This class contains all the relevant information about users TODO: It should implement parcelable
 * so that it can be injected in views
 */
public class User {

  private String id;
  private String name;
  private String email;
  private String deviceId;
  private LocalDate birthDate;
  private int activeAcceptingFavors;
  private int activeRequestingFavors;

  public User() {}

  public User(

      String id,
      String name,
      String email,
      String deviceId,
      LocalDate birthDate,
      int activeAcceptingFavors,
      int activeRequestingFavors) {

    this.id = id;
    this.name = name;
    this.email = email;
    this.deviceId = deviceId;
    this.birthDate = birthDate;
    this.activeAcceptingFavors = activeAcceptingFavors;
    this.activeRequestingFavors = activeRequestingFavors;
  }

  // Getters
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

  public LocalDate getBirthDate() {
    return birthDate;
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
  // Can only accept or request favors
  boolean canAccept() {
    return activeAcceptingFavors + activeRequestingFavors < 1;
  }

  boolean canRequest() {
    return activeAcceptingFavors + activeRequestingFavors < 1;
  }
}
