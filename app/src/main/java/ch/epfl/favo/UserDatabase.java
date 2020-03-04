package ch.epfl.favo;

import java.util.ArrayList;

public final class UserDatabase {

    public UserDatabase() {}

    public static String addUserToDatabase(ArrayList userDetails) {
        DatabaseWrapper.getDatabaseInstance();
        return "";
    }

    public static boolean deleteUserFromDatabase(String userId) {
        boolean success = false;
        DatabaseWrapper.getDatabaseInstance();
        return success;
    }

}
