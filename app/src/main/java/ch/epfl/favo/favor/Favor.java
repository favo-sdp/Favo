package ch.epfl.favo.favor;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import ch.epfl.favo.common.DatabaseWrapper;
import ch.epfl.favo.common.Document;
import ch.epfl.favo.common.FavoLocation;

/**
 * Class contains all the information relevant to a single favor. Relevant info includes tile,
 * description, requester, accepter, location and status
 */
@SuppressLint("NewApi")
public class Favor implements Parcelable, Document {

  // String constants for Map conversion
  public static final String ID = "id";
  public static final String TITLE = "title";
  public static final String DESCRIPTION = "description";
  public static final String REQUESTER_ID = "requesterId";
  public static final String ACCEPTER_ID = "accepterId";
  public static final String LOCATION = "location";
  public static final String POSTED_TIME = "postedTime";
  public static final String STATUS_ID = "statusId";
  public static final String IS_ARCHIVED = "isArchived";

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
  private FavoLocation location;
  private Date postedTime;
  private int statusId;
  private boolean isArchived;

  public Favor() {}

  // General Constructor
  public Favor(
      String title, String description, String requesterId, FavoLocation location, int statusId) {

    this.id = DatabaseWrapper.generateRandomId();
    this.title = title;
    this.description = description;
    this.requesterId = requesterId;
    this.location = location;
    this.postedTime = new Date();
    this.statusId = statusId;
    this.accepterId = null;
    this.isArchived = false;
  }

  // Constructor to override default generated Id
  public Favor(
      String id,
      String title,
      String description,
      String requesterId,
      FavoLocation location,
      int statusId) {
    this(title, description, requesterId, location, statusId);
    this.id = id;
  }

  // Constructor to make statusId int conversion implicit
  public Favor(
      String title,
      String description,
      String requesterId,
      FavoLocation location,
      FavorStatus statusId) {

    this(title, description, requesterId, location, statusId.toInt());
  }

  /**
   * Constructor from map
   *
   * @param map
   */
  public Favor(Map<String, Object> map) {
    this.id = (String) map.get(ID);
    this.title = (String) map.get(TITLE);
    this.description = (String) map.get(DESCRIPTION);
    this.requesterId = (String) map.get(REQUESTER_ID);
    this.accepterId = (String) map.get(ACCEPTER_ID);
    this.location = (FavoLocation) map.get(LOCATION);
    this.postedTime = (Date) map.get(POSTED_TIME);
    this.statusId = (int) map.get(STATUS_ID);
    this.isArchived = (boolean) map.get(IS_ARCHIVED);
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
    try {
      statusId = in.readInt();
    } catch (Exception e) {
      statusId = -1;
    }
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public Map<String, Object> toMap() {
    return new HashMap<String, Object>() {
      {
        put(ID, id);
        put(TITLE, title);
        put(DESCRIPTION, description);
        put(REQUESTER_ID, requesterId);
        put(ACCEPTER_ID, accepterId);
        put(LOCATION, location);
        put(POSTED_TIME, postedTime);
        put(STATUS_ID, statusId);
        put(IS_ARCHIVED, isArchived);
      }
    };
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

  public String getAccepterId() {
    return accepterId;
  }

  public boolean getIsArchived() {
    return isArchived;
  }

  public void setAccepterId(String id) {
    this.accepterId = id;
  }

  public Date getPostedTime() {
    return postedTime;
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

  private void setStatusId(int statusId) {
    this.statusId = statusId;
    if (IntStream.of(FavorStatus.archivedStates).anyMatch(i -> i == statusId)) {
      this.setIsArchived(true);
    } else {
      this.setIsArchived(false);
    }
  }

  private void setIsArchived(boolean val) {
    this.isArchived = val;
  }

  public void setStatusIdToInt(FavorStatus statusId) {
    setStatusId(statusId.toInt());
  }

  public FavoLocation getLocation() {
    return location;
  }

  public void setLocation(FavoLocation location) {
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

  public void updateToOther(Favor other) {
    // we take all the values except for the id, acceptor and requester id

    this.title = other.getTitle();
    this.description = other.getDescription();
    this.location = other.getLocation();
    this.postedTime = other.getPostedTime();
    this.statusId = other.getStatusId();
  }
  // Overriding equals() to compare two Complex objects
  public boolean contentEquals(Favor other) {
    return this.title.equals(other.title)
        && this.description.equals(other.description)
        && this.statusId == other.getStatusId()
        && this.location.equals(other.location);
  }
}
