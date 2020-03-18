package ch.epfl.favo.util;

import android.location.Location;

import java.util.ArrayList;

import ch.epfl.favo.favor.Favor;

public class FakeFavorList {
    private long time;
    private double latitude, longitude;

    public FakeFavorList(double latitude, double longitude, long time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;

        Location location = new Location("provider name");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setTime(time);
    }

    public ArrayList<Favor> retrieveFavorList() {
        Favor favor0 = retrieveFavor(0, 0.001, 0.001);
        Favor favor1 = retrieveFavor(1, 0.001, -0.002);
        Favor favor2 = retrieveFavor(2, -0.002, -0.001);
        ArrayList<Favor> favorList = new ArrayList<>();
        favorList.add(favor0);
        favorList.add(favor1);
        favorList.add(favor2);
        return favorList;
    }

    public Favor retrieveFavor(int number, double latitudeOffset, double longitudeOffset) {
        Location location = new Location("provider name");
        location.setLatitude(latitude + latitudeOffset);
        location.setLongitude(longitude + longitudeOffset);
        location.setTime(time);
        return new Favor("Title of Favor " + number,
                "Description of favor " + number, "Requester" + number, location, 0);
    }
}
