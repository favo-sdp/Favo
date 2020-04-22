package ch.epfl.favo.favor;

public enum FavorStatus {
  REQUESTED("Requested", 0),
  ACCEPTED("Accepted", 1),
  EXPIRED("Expired", 2),
  CANCELLED_REQUESTER("Cancelled by requester", 3),
  CANCELLED_ACCEPTER("Cancelled by accepter", 4),
  SUCCESSFULLY_COMPLETED("Successfully Completed", 5),
  ACCEPTED_BY_OTHER("Accepted by other", 6), // additional state
  EDIT("Edit mode", 7);

  public static int[] archivedStates = {
    EXPIRED.toInt(),
    CANCELLED_REQUESTER.toInt(),
    CANCELLED_ACCEPTER.toInt(),
    SUCCESSFULLY_COMPLETED.toInt()
  };

  private String status;
  private int code;

  FavorStatus(String status, int code) {
    this.status = status;
    this.code = code;
  }

  public String toString() {
    return this.status;
  }

  public int toInt() {
    return this.code;
  }

  public static FavorStatus toEnum(int code) {
    return FavorStatus.values()[code];
  }

  public static FavorStatus toEnum(String status) {
    return FavorStatus.valueOf(status);
  }

  public static FavorStatus convertTemporaryStatus(FavorStatus status) {
    if (status.equals(FavorStatus.EDIT)) {
      status = FavorStatus.REQUESTED;
    }
    return status;
  }
}
