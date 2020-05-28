package ch.epfl.favo.chat.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ch.epfl.favo.database.DatabaseWrapper;
import ch.epfl.favo.database.Document;

@IgnoreExtraProperties
public class Message implements Document {
  private String mId;
  public static final String ID = "id";
  private String mName;
  public static final String NAME = "name";
  private String mMessage;
  public static final String MESSAGE = "message";
  private String mUid;
  public static final String UID = "uId";
  private String mFavorId;
  public static final String FAVOR_ID = "favorId";
  private Date mTimestamp;
  public static final String TIME_STAMP = "timeStamp";
  private String mImagePath;
  public static final String IMAGE_PATH = "imagePath";
  private int mMessageType;
  public static final String MESSAGE_TYPE = "messageType";
  private String mLatitude;
  public static final String LATITUDE = "latitude";
  private String mLongitude;
  public static final String LONGITUDE = "longitude";

  public Message() {
    // Needed for Firebase
  }

  public Message(
      @NonNull String id,
      @Nullable String name,
      @NonNull String uid,
      int messageType,
      @Nullable String message,
      @Nullable String imagePath,
      @NonNull String favorId,
      @Nullable String latitude,
      @Nullable String longitude) {
    this(
        name,
        uid,
        messageType,
        message,
        imagePath,
        favorId,
        latitude,
        longitude);
    mId = id;
  }

  public Message(
      @Nullable String name,
      @NonNull String uid,
      int messageType,
      @Nullable String message,
      @Nullable String imagePath,
      @NonNull String favorId,
      @Nullable String latitude,
      @Nullable String longitude) {
    mId = DatabaseWrapper.generateRandomId();
    setName(name);
    setUid(uid);
    setMessageType(messageType);
    setMessage(message);
    setPicturePath(imagePath);
    setFavorId(favorId);

    mTimestamp = new Date();
    setTimestamp(mTimestamp);
    setLatitude(latitude);
    setLongitude(longitude);
  }

  public Message(
      @Nullable String name,
      @NonNull String uid,
      int messageType,
      @Nullable String message,
      @Nullable String imagePath,
      @NonNull String favorId) {
    this(name, uid, messageType, message, imagePath, favorId, null, null);
  }

  public void setMessage(String message) {
    mMessage = message;
  }

  @Nullable
  public String getMessage() {
    return mMessage;
  }

  @Nullable
  public String getName() {
    return mName;
  }

  public void setName(@Nullable String name) {
    mName = name;
  }

  @NonNull
  public String getUid() {
    return mUid;
  }

  public void setUid(@NonNull String uid) {
    mUid = uid;
  }

  @NonNull
  public String getFavorId() {
    return mFavorId;
  }

  public void setFavorId(String mFavorId) {
    this.mFavorId = mFavorId;
  }

  public int getMessageType() {
    return mMessageType;
  }

  private void setMessageType(int messageType) {
    mMessageType = messageType;
  }

  @ServerTimestamp
  @Nullable
  public Date getTimestamp() {
    return mTimestamp;
  }

  public void setTimestamp(@Nullable Date timestamp) {
    mTimestamp = timestamp;
  }

  @Nullable
  public String getPicturePath() {
    return mImagePath;
  }

  public void setPicturePath(String path) {
    mImagePath = path;
  }

  @Nullable
  public String getLatitude() {
    return mLatitude;
  }

  public void setLatitude(String latitude) {
    mLatitude = latitude;
  }

  @Nullable
  public String getLongitude() {
    return mLongitude;
  }

  public void setLongitude(String longitude) {
    mLongitude = longitude;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Message message = (Message) o;
    return mMessageType == message.mMessageType &&
            mId.equals(message.mId) &&
            mName.equals(message.mName) &&
            mMessage.equals(message.mMessage) &&
            mUid.equals(message.mUid) &&
            mFavorId.equals(message.mFavorId) &&
            mTimestamp.equals(message.mTimestamp) &&
            Objects.equals(mImagePath, message.mImagePath) &&
            Objects.equals(mLatitude, message.mLatitude) &&
            Objects.equals(mLongitude, message.mLongitude);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mId, mName, mMessage, mUid, mFavorId, mTimestamp, mImagePath, mMessageType, mLatitude, mLongitude);
  }

  @NonNull
  @Override
  public String toString() {
    return "Chat{"
        + "mId='"
        + mId
        + '\''
        + ", mName='"
        + mName
        + '\''
        + ", mMessage='"
        + mMessage
        + '\''
        + ", mMessageType="
        + mMessageType
        + '\''
        + ", mUid='"
        + mUid
        + '\''
        + ", mFavorId='"
        + mFavorId
        + '\''
        + ", mTimestamp="
        + ((mTimestamp != null) ? mTimestamp.toString() : "")
        + '\''
        + ", mImagePath='"
        + mImagePath
        + '\''
        + ", mLatitude='"
        + mLatitude
        + '\''
        + ", mLongitude='"
        + mLongitude
        + '\''
        + '}';
  }

  @Override
  public String getId() {
    return mId;
  }

  @Override
  public Map<String, Object> toMap() {
    return new HashMap<String, Object>() {
      {
        put(ID, mId);
        put(NAME, mName);
        put(MESSAGE_TYPE, mMessageType);
        put(MESSAGE, mMessage);
        put(UID, mUid);
        put(FAVOR_ID, mFavorId);
        put(TIME_STAMP, mTimestamp);
        put(IMAGE_PATH, mImagePath);
        put(LATITUDE, mLatitude);
        put(LONGITUDE, mLongitude);
      }
    };
  }
}
