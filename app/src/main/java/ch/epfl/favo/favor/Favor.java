package ch.epfl.favo.favor;

import android.location.Location;

public class Favor {

    private String title;
    private String description;
    private String requesterId;
    private Location location;
    private int statusId;

    public Favor() {}

    public Favor(
            String title,
            String description,
            String requesterId,
            Location location,
            int statusId
    ) {
        this.title = title;
        this.description = description;
        this.requesterId = requesterId;
        this.location = location;
        this.statusId = statusId;
    }

    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public String getRequesterId() { return requesterId; }

    public int getStatusId() { return statusId; }

    public Location getLocation() { return location; }

    void setLocation(Location location) {
        this.location = location;
    }

    void setStatusId(int statusId) {
        this.statusId = statusId;
    }
}
