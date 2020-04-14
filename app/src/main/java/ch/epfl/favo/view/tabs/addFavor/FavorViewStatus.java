package ch.epfl.favo.view.tabs.addFavor;

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
}
