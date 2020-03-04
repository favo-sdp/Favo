package ch.epfl.favo;

import android.provider.ContactsContract;

import java.util.ArrayList;

public final class FavorDatabase {

    public FavorDatabase() {}

    public static String addFavorToDatabase(ArrayList favorDetails) {
        DatabaseWrapper.getDatabaseInstance();
        return "";
    }

    public static boolean deleteFavorFromDatabase(String favorId) {
        boolean success = false;
        DatabaseWrapper.getDatabaseInstance();
        return success;
    }

}
