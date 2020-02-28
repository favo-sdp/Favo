package ch.epfl.favo.models;

import ch.epfl.favo.exceptions.NotImplementedException;

public class UserUtil {
    /*
    TODO: Design singleton constructor and logic
     */
    private static final UserUtil SINGLE_INSTANCE = new UserUtil();
    private UserUtil(){
        return;
    }

    public static UserUtil getSingleInstance() {
        return SINGLE_INSTANCE;
    }

    /**
     *
     * @param user
     * @param pw
     * @throws IllegalArgumentException
     * @throws NotImplementedException
     */
    public void createAccount(String user, String pw) throws IllegalArgumentException,NotImplementedException{
        /*
        TODO: Use Firebase API to fetch user data. Upload it to DB and return to welcome screen for login
         */
        throw new NotImplementedException();
    }

    /**
     * Allows user to login to their account
     * @param username
     * @param password
     */
    public void logInAccount (String username,String password)
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
