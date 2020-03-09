package ch.epfl.favo.favor;

import android.location.Location;

/**
 * Class contains all the information relevant to a single favor. Relevant info includes tile,
 * description, requester, accepter, location and status
 */
public class Favor {

  private String title;
  private String description;
  private String requesterId;
  private String accepterID;
  private Location location;
  private int statusId;

  public Favor() {}

  public Favor(
      String title, String description, String requesterId, Location location, int statusId) {
    this.title = title;
    this.description = description;
    this.requesterId = requesterId;
    this.location = location;
    this.statusId = statusId;
    this.accepterID = null;
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
    return accepterID;
  }

  void setAccepterID(String accepterID) {
    this.accepterID = accepterID;
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
