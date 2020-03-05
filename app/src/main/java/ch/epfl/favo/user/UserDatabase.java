package ch.epfl.favo.user;

import ch.epfl.favo.common.NotImplementedException;

public class UserDatabase {

    private static final UserDatabase SINGLE_INSTANCE = new UserDatabase();

    private UserDatabase() {}

    public static UserDatabase getSingleInstance() {
        return SINGLE_INSTANCE;
    }

    public void getUserFromDB(String userId)
            throws IllegalArgumentException, NotImplementedException {
        /*
        TODO: Fetch user data after implementing
            callback interface
         */
        throw new NotImplementedException();
    }

    public void removeUserFromDB(String userId)
            throws IllegalArgumentException, NotImplementedException {
        /*
        TODO: Delete user data after implementing
            callback interface
         */
        throw new NotImplementedException();
    }

}
