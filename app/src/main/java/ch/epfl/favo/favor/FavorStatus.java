package ch.epfl.favo.favor;

public enum FavorStatus {
    REQUESTED("Requested", 0),
    EDIT("Edit mode", 1),
    ACCEPTED("Accepted", 2),
    EXPIRED("Expired", 3),
    CANCELLED_REQUESTER("Cancelled by requester", 4),
    CANCELLED_ACCEPTER("Cancelled by accepter", 5),
    SUCCESSFULLY_COMPLETED("Completed succesfully", 6);

    private String statusString;
    private int statusCode;

    FavorStatus(String text, int code) {
        this.statusString = text;
        this.statusCode = code;
    }

    public String toString() {
        return statusString;
    }

    public int toInt() {
        return statusCode;
    }

    public static String toString(int code) {
        return toEnum(code).name();
    }

    public static FavorStatus toEnum(int code) {
        return FavorStatus.values()[code];
    }
}
