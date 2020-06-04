package ch.epfl.favo.favor;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import ch.epfl.favo.database.DatabaseWrapper;
import ch.epfl.favo.database.Document;
import ch.epfl.favo.gps.FavoLocation;

/**
 * Class contains all the information relevant to a single favor. Relevant info includes tile,
 * description, requester, accepter, location and status
 */
@SuppressLint("NewApi")
public class Favor implements Parcelable, Document, Cloneable {

  // String constants for Map conversion
  private static final String ID = "id";
  public static final String TITLE = "title";
  private static final String DESCRIPTION = "description";
  static final String USER_IDS = "userIds";
  private static final String LOCATION = "location";
  public static final String POSTED_TIME = "postedTime";
  private static final String STATUS_ID = "statusId";
  static final String PICTURE_URL = "pictureUrl";
  public static final String IS_ARCHIVED = "isArchived";
  private static final String REWARD = "reward";

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
  private List<String> userIds;
  private FavoLocation location;
  private Date postedTime;
  private int statusId;
  private String pictureUrl;
  private boolean isArchived;
  private double reward;

  public Favor() {}

  // General Constructor
  public Favor(
      String title,
      String description,
      String requesterId,
      FavoLocation location,
      int statusId,
      double reward,
      String pictureUrl) {

    this.id = DatabaseWrapper.generateRandomId();
    this.title = title;
    this.description = description;
    this.userIds = new ArrayList<>(Arrays.asList(requesterId));
    this.location = location;
    this.postedTime = new Date();
    this.statusId = statusId;
    this.pictureUrl = pictureUrl;
    this.isArchived = false;
    this.reward = reward;
  }

  // Constructor to override default generated Id
  public Favor(
      String id,
      String title,
      String description,
      String requesterId,
      FavoLocation location,
      int statusId,
      double reward) {
    this(title, description, requesterId, location, statusId, reward, null);
    this.id = id;
  }
  // copy constructor
  public Favor(Favor other) {
    this(
        other.getId(),
        other.getTitle(),
        other.getDescription(),
        other.getRequesterId(),
        other.getLocation(),
        other.getStatusId(),
        other.getReward());
    this.isArchived = other.isArchived;
    this.pictureUrl = other.getPictureUrl();
    this.postedTime = other.getPostedTime();
    this.userIds = new ArrayList<>(other.getUserIds());
  }

  // Constructor to make statusId int conversion implicit
  public Favor(
      String title,
      String description,
      String requesterId,
      FavoLocation location,
      FavorStatus statusId,
      double reward) {

    this(title, description, requesterId, location, statusId.toInt(), reward, null);
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
    this.userIds = new ArrayList<>((List<String>) map.get(USER_IDS));
    this.location = (FavoLocation) map.get(LOCATION);
    this.postedTime = (Date) map.get(POSTED_TIME);
    this.statusId = (int) map.get(STATUS_ID);
    this.pictureUrl = (String) map.get(PICTURE_URL);
    this.isArchived = (boolean) map.get(IS_ARCHIVED);
    this.reward = (double) map.get(REWARD);
  }

  /**
   * Parcelable implementaion allows us to pass favor to fragment
   *
   * @param in
   */
  protected Favor(Parcel in) {
    title = in.readString();
    description = in.readString();
    userIds = in.readArrayList(String.class.getClassLoader());
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
        put(USER_IDS, userIds);
        put(LOCATION, location);
        put(POSTED_TIME, postedTime);
        put(STATUS_ID, statusId);
        put(PICTURE_URL, pictureUrl);
        put(IS_ARCHIVED, isArchived);
        put(REWARD, reward);
      }
    };
  }

  public String getTitle() {
    return title;
  }

  public double getReward() {
    return reward;
  }

  public String getDescription() {
    return description;
  }

  public List<String> getUserIds() {
    return userIds;
  }

  public String getRequesterId() {
    return userIds != null && userIds.size() > 0 ? userIds.get(0) : null;
  }

  public String getAccepterId() {
    return userIds != null && userIds.size() > 1 ? userIds.get(1) : null;
  }

  public boolean getIsArchived() {
    return isArchived;
  }

  public void setReward(double reward) {
    this.reward = reward;
  }

  public void setTitle(String title) {
    this.title = title;
  }
  /**
   * structure of userIds: a list with the first position always setting as requester Id, following
   * with potential helpers who commit this favor. When a requester finally decide a accepter, he
   * reset this list with his own Id and accepter's ID *
   */
  public void setAccepterId(String id) {
    if (userIds != null && !userIds.isEmpty()) {
      userIds.add(id);
    }
  }

  public void clearAccepterIds() {
    userIds = new ArrayList<>(Arrays.asList(getRequesterId()));
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

  public String getPictureUrl() {
    return this.pictureUrl;
  }

  public void setPictureUrl(String pictureUrl) {
    this.pictureUrl = pictureUrl;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(title);
    dest.writeString(description);
    dest.writeList(userIds);
    dest.writeParcelable(location, flags);
    dest.writeInt(statusId);
    dest.writeString(pictureUrl);
  }

  public void updateToOther(Favor other) {
    // we take all the values except for the id, acceptor and requester id

    this.title = other.getTitle();
    this.description = other.getDescription();
    this.location = other.getLocation();
    this.postedTime = other.getPostedTime();
    this.statusId = other.getStatusId();
    this.pictureUrl = other.getPictureUrl();
  }

  public boolean contentEquals(Favor other) {
    if (other == null) return false;
    return this.title.equals(other.title)
        && this.description.equals(other.description)
        && this.statusId == other.getStatusId()
        && this.location.equals(other.location)
        && (this.pictureUrl == null && other.pictureUrl == null
            || (this.pictureUrl.equals(other.pictureUrl)));
  }
}
