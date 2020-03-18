package ch.epfl.favo.favor;

import android.location.Location;

import ch.epfl.favo.common.DatabaseWrapper;

/**
 * Class contains all the information relevant to a single favor. Relevant info includes tile,
 * description, requester, accepter, location and status
 */
public class Favor {

  private String id;
  private String title;
  private String description;
  private String requesterId;
  private String accepterId;
  private Location location;
  private int statusId;

  public Favor() {}

  public Favor(
      String title, String description, String requesterId, Location location, int statusId) {
    this.id = DatabaseWrapper.generateRandomId();
    this.title = title;
    this.description = description;
    this.requesterId = requesterId;
    this.location = location;
    this.statusId = statusId;
    this.accepterId = null;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getRequesterId() {
    return requesterId;
  }

  public String getAccepterID() {
    return accepterId;
  }

  void setAccepterID(String accepterID) {
    this.accepterId = accepterID;
  }

  /**
   * Status ID can be: 0 for requested 1 for accepted 2 for completed succesfully 3 for expired 4
   * for completed unsuccessfully
   *
   * @return statusID
   */
  public int getStatusId() {
    return statusId;
  }

  void setStatusId(int statusId) {
    this.statusId = statusId;
  }

  public Location getLocation() {
    return location;
  }

  void setLocation(Location location) {
    this.location = location;
  }
}
