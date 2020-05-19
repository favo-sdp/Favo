package ch.epfl.favo.chat.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.Objects;

@IgnoreExtraProperties
public class Message {
  private String mName;
  private String mMessage;
  private String mUid;
  private String mNotifId;
  private String mFavorId;
  private Date mTimestamp;
  private String mIsFirstMsg;
  private String mImagePath;
  private int mMessageType;

  public Message() {
    // Needed for Firebase
  }

  public Message(
      @Nullable String name,
      @NonNull String uid,
      @NonNull int messageType,
      @Nullable String message,
      @Nullable String imagePath,
      @NonNull String notifId,
      @NonNull String favorId,
      @NonNull String isFirstMsg) {
    mName = name;
    mUid = uid;
    mMessageType = messageType;
    mMessage = message;
    mImagePath = imagePath;
    mNotifId = notifId;
    mFavorId = favorId;
    mIsFirstMsg = isFirstMsg;
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

  @NonNull
  String getNotifId() {
    return mNotifId;
  }

  public void setNotifId(String mNotifId) {
    this.mNotifId = mNotifId;
  }

  @NonNull
  public String getIsFirstMsg() {
    return mIsFirstMsg;
  }

  public void setIsFirstMsg(String isFirstMsg) {
    this.mIsFirstMsg = isFirstMsg;
  }

  @NonNull
  public int getMessageType() {
    return mMessageType;
  }

  public void setMessageType(int messageType) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Message chatModel = (Message) o;
    return mName.equals(chatModel.mName)
        && mUid.equals(chatModel.mUid)
        && mMessageType == chatModel.mMessageType
        && mMessage.equals(chatModel.mMessage)
        && mImagePath.equals(chatModel.mImagePath)
        && mNotifId.equals(chatModel.mNotifId)
        && mFavorId.equals(chatModel.mFavorId)
        && mTimestamp.equals(chatModel.mTimestamp)
        && mIsFirstMsg.equals(chatModel.mIsFirstMsg);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        mName, mUid, mMessageType, mMessage, mImagePath, mNotifId, mFavorId, mIsFirstMsg);
  }

  @NonNull
  @Override
  public String toString() {
    return "Chat{"
        + "mName='"
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
        + ", mNotifId='"
        + mNotifId
        + '\''
        + ", mFavorId='"
        + mFavorId
        + '\''
        + ", mTimestamp="
        + ((mTimestamp != null) ? mTimestamp.toString() : "")
        + '\''
        + ", mIsFirstMsg='"
        + mIsFirstMsg
        + '\''
        + ", mImagePath='"
        + mImagePath
        + '}';
  }
}
