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
  public boolean equals(@Nullable Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ChatModel chatModel = (ChatModel) o;

    return mTimestamp.equals(chatModel.mTimestamp)
        && mUid.equals(chatModel.mUid)
        && mFavorId.equals(chatModel.mFavorId)
        && (Objects.equals(mName, chatModel.mName))
        && (Objects.equals(mMessage, chatModel.mMessage));
  }

  @Override
  public int hashCode() {
    int result = mName == null ? 0 : mName.hashCode();
    result = 31 * result + (mMessage == null ? 0 : mMessage.hashCode());
    result = 31 * result + mUid.hashCode();
    result = 31 * result + mFavorId.hashCode();
    result = 31 * result + mTimestamp.hashCode();
    return result;
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
