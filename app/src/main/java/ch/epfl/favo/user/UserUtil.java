package ch.epfl.favo.user;

import ch.epfl.favo.common.NotImplementedException;

/**
 * This class contains all the logic. For example it has the rules
 * that allow a user to post favors or accept favors. It calls methods from
 * UserDatabase
 */
public class UserUtil {

    private static final UserUtil SINGLE_INSTANCE = new UserUtil();

    private UserUtil() {}

    public static UserUtil getSingleInstance() {
        return SINGLE_INSTANCE;
    }

    /**@param user Corresponds to user in db
     * @throws IllegalArgumentException Should check for invalid inputs.
     * @throws NotImplementedException Temporary exception while we implement.
     */
    public void createAccount(User user)
            throws IllegalArgumentException,NotImplementedException{
        /*
        TODO: Should give an error if user is already in DB
         */
        throw new NotImplementedException();
    }

    /**Allows user to login to their account.
     * @param user Valid user in DB
     */
    public void logInAccount(User user)
    {
        /*
        TODO: Login with Google Firebase. If not found, throw NotInDBException()
         */
        throw new NotImplementedException();
    }
    public void logOutAccount(){
        throw new NotImplementedException();
    }

    public void deleteAccount(){
        throw new NotImplementedException();
    }

}
