package ch.epfl.favo.view.tabs.addFavor;

import ch.epfl.favo.favor.Favor;

public enum FavorViewStatus {
  ACCEPTED("Accepted"),
  EDIT("Edit mode"),
  EXPIRED("Expired"),
  ACCEPTED_BY_OTHER("Accepted by other"), // additional state
  REQUESTED("Requested"),
  CANCELLED_REQUESTER("Cancelled by requester"),
  CANCELLED_ACCEPTER("Cancelled by accepter"),
  SUCCESSFULLY_COMPLETED("Succesfully Completed");
  private String customString;

  FavorViewStatus(String val) {
    this.customString = val;
  }

  public String getPrettyString() {
    return this.customString;
  }
  public static Favor.Status convertViewStatusToFavorStatus(FavorViewStatus status){
    Favor.Status favorStatus;
    if (status.equals(FavorViewStatus.EDIT)) {
      favorStatus = Favor.Status.REQUESTED;
    } else {
      favorStatus = Favor.Status.valueOf(status.toString());
    }
    return favorStatus;
  }
}
