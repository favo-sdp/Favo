package ch.epfl.favo;

import android.location.Location;

import java.util.ArrayList;

public final class FavorUtil {

    public FavorUtil() {}

    public static ArrayList initializeFavor(String title, String description, Location location) {
        ArrayList favorDetails = new ArrayList<>();
        FavorDatabase.addFavorToDatabase(favorDetails);
        return favorDetails;
    }

    public static boolean deleteFavor(String favorId) {
        boolean success = false;
        FavorDatabase.deleteFavorFromDatabase(favorId);
        return success;
    }
}
