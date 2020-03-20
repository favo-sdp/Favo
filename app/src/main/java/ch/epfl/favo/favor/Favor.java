package ch.epfl.favo.favor;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import ch.epfl.favo.common.DatabaseWrapper;

/**
 * Class contains all the information relevant to a single favor. Relevant info includes tile,
 * description, requester, accepter, location and status
 */
public class Favor implements Parcelable {

  public static final Creator<Favor> CREATOR =
      new Creator<Favor>() {
        @Override
        public Favor createFromParcel(Parcel in) {
          return new Favor(in);
        }

        @Override
        public Favor[] newArray(int size) {
          return new Favor[size];
        }
      };
  private String id;
  private String title;
  private String description;
  private String requesterId;
  private String accepterId;
  private Location location;
  private Date postedTime;
  private int statusId;

  public Favor() {}

  public Favor(
      String title, String description, String requesterId, Location location, int statusId) {
    this.id = DatabaseWrapper.generateRandomId();
    this.title = title;
    this.description = description;
    this.requesterId = requesterId;
    this.location = location;
    this.postedTime = new Date();
    this.statusId = statusId;
    this.accepterId = null;
  }

  /**
   * Parcelable implementaion allows us to pass favor to fragment
   *
   * @param in
   */
  protected Favor(Parcel in) {
    title = in.readString();
    description = in.readString();
    requesterId = in.readString();
    accepterId = in.readString();
    location = in.readParcelable(Location.class.getClassLoader());
    statusId = in.readInt();
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

  public Date getPostedTime() {
    return postedTime;
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

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(title);
    dest.writeString(description);
    dest.writeString(requesterId);
    dest.writeString(accepterId);
    dest.writeParcelable(location, flags);
    dest.writeInt(statusId);
  }
}
