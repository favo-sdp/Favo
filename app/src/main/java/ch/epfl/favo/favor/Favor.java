package ch.epfl.favo.favor;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class contains all the information relevant to a single favor. Relevant info includes tile,
 * description, requester, accepter, location and status
 */
public class Favor implements Parcelable {

  private String id;
  private String title;
  private String description;
  private String requesterId;
  private String accepterID;
  private Location location;
  private int statusId;

  public Favor() {}

  public Favor(
      String id, String title, String description, String requesterId, Location location, int statusId) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.requesterId = requesterId;
    this.location = location;
    this.statusId = statusId;
    this.accepterID = null;
  }

  /**
   * Parcelable implementaion allows us to pass favor to fragment
   * @param in
   */
  protected Favor(Parcel in) {
    title = in.readString();
    description = in.readString();
    requesterId = in.readString();
    accepterID = in.readString();
    location = in.readParcelable(Location.class.getClassLoader());
    statusId = in.readInt();
  }

  public static final Creator<Favor> CREATOR = new Creator<Favor>() {
    @Override
    public Favor createFromParcel(Parcel in) {
      return new Favor(in);
    }

    @Override
    public Favor[] newArray(int size) {
      return new Favor[size];
    }
  };

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

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(title);
    dest.writeString(description);
    dest.writeString(requesterId);
    dest.writeString(accepterID);
    dest.writeParcelable(location, flags);
    dest.writeInt(statusId);
  }
}
