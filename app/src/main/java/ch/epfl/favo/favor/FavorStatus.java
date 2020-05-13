package ch.epfl.favo.favor;

import androidx.annotation.NonNull;

import java.util.HashMap;

import ch.epfl.favo.R;

public enum FavorStatus {
  REQUESTED("Requested", 0),
  ACCEPTED("Accepted", 1),
  EXPIRED("Expired", 2),
  CANCELLED_REQUESTER("Cancelled by requester", 3),
  CANCELLED_ACCEPTER("Cancelled by accepter", 4),
  SUCCESSFULLY_COMPLETED("Succesfully Completed", 5),
  COMPLETED_REQUESTER("Completed by requester", 6),
  COMPLETED_ACCEPTER("Completed by accepter", 7),
  ACCEPTED_BY_OTHER("Accepted by other", 8), // additional state
  EDIT("Edit mode", 9);

  public static int[] archivedStates = {
    EXPIRED.toInt(),
    CANCELLED_REQUESTER.toInt(),
    CANCELLED_ACCEPTER.toInt(),
    SUCCESSFULLY_COMPLETED.toInt()
  };

  public static HashMap<FavorStatus, Integer> statusColor = new HashMap<FavorStatus, Integer>() {
    {
      put(FavorStatus.REQUESTED, R.color.requested_status_bg);
      put(FavorStatus.ACCEPTED, R.color.accepted_status_bg);
      put(FavorStatus.EXPIRED, R.color.cancelled_status_bg);
      put(FavorStatus.CANCELLED_REQUESTER, R.color.cancelled_status_bg);
      put(FavorStatus.CANCELLED_ACCEPTER, R.color.cancelled_status_bg);
      put(FavorStatus.SUCCESSFULLY_COMPLETED, R.color.accepted_status_bg);
      put(FavorStatus.COMPLETED_REQUESTER, R.color.accepted_status_bg);
      put(FavorStatus.COMPLETED_ACCEPTER, R.color.accepted_status_bg);
      put(FavorStatus.ACCEPTED_BY_OTHER, R.color.cancelled_status_bg);
      put(FavorStatus.EDIT, R.color.requested_status_bg);
    }
  };

  private String status;
  private int code;

  FavorStatus(String status, int code) {
    this.status = status;
    this.code = code;
  }

  @NonNull
  public String toString() {
    return this.status;
  }

  public int toInt() {
    return this.code;
  }

  public static FavorStatus toEnum(int code) {
    return FavorStatus.values()[code];
  }

  // public static FavorStatus toEnum(String status) {
  //   return FavorStatus.valueOf(status);
  // }

  // public static FavorStatus convertTemporaryStatus(FavorStatus status) {
  //  if (status.equals(FavorStatus.EDIT)) {
  //    status = FavorStatus.REQUESTED;
  //  }
  //  return status;
  // }
}
