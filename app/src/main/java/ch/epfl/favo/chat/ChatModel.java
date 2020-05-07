package ch.epfl.favo.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.Objects;

@IgnoreExtraProperties
public class ChatModel {
  private String mName;
  private String mMessage;
  private String mUid;
  private String mFavorId;
  private Date mTimestamp;

  public ChatModel() {
    // Needed for Firebase
  }

  public ChatModel(
      @Nullable String name,
      @Nullable String message,
      @NonNull String uid,
      @NonNull String favorId) {
    mName = name;
    mMessage = message;
    mUid = uid;
    mFavorId = favorId;
  }

  @Nullable
  public String getName() {
    return mName;
  }

  public void setName(@Nullable String name) {
    mName = name;
  }

  @Nullable
  public String getMessage() {
    return mMessage;
  }

  public void setMessage(@Nullable String message) {
    mMessage = message;
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

  @ServerTimestamp
  @Nullable
  public Date getTimestamp() {
    return mTimestamp;
  }

  public void setTimestamp(@Nullable Date timestamp) {
    mTimestamp = timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChatModel chatModel = (ChatModel) o;
    return mName.equals(chatModel.mName) &&
            mMessage.equals(chatModel.mMessage) &&
            mUid.equals(chatModel.mUid) &&
            mFavorId.equals(chatModel.mFavorId) &&
            mTimestamp.equals(chatModel.mTimestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mName, mMessage, mUid, mFavorId, mTimestamp);
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
        + ", mUid='"
        + mUid
        + '\''
        + ", mFavorId='"
        + mFavorId
        + '\''
        + ", mTimestamp="
        + mTimestamp
        + '}';
  }
}
