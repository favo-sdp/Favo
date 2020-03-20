package ch.epfl.favo.user;

import android.location.Location;

import java.util.ArrayList;

import ch.epfl.favo.common.CollectionWrapper;
import ch.epfl.favo.common.NotImplementedException;

public class UserUtil {
  /*
  TODO: Design singleton constructor and logic
   */
  // Single private instance
  private static final String TAG = "UserUtil";
  private static final UserUtil SINGLE_INSTANCE = new UserUtil();
  private static final CollectionWrapper collection = new CollectionWrapper("users");

  // Private constructor
  private UserUtil() {}

  // Single instance getter
  public static UserUtil getSingleInstance() {
    return SINGLE_INSTANCE;
  }

  /**
   * @param user Corresponds to user in db.
   * @param pw Corresponds to their password in db.
   * @throws IllegalArgumentException Should check for invalid inputs.
   * @throws NotImplementedException Temporary exception while we implement.
   */
  public void createAccount(String user, String pw)
      throws IllegalArgumentException, NotImplementedException {
    /*
    TODO: Use Firebase API to fetch user data. \
     Upload it to DB and return to welcome screen for login
     */
    throw new NotImplementedException();
  }

  /**
   * Allows user to login to their account.
   *
   * @param username Valid username in DB.
   * @param password Valid Pw in DB.
   */
  public void logInAccount(String username, String password) {
    /*
    TODO: Login with Google Firebase. If not found, throw NotInDBException()
     */
    throw new NotImplementedException();
  }

  public void logOutAccount() {
    throw new NotImplementedException();
  }

  public void deleteAccount() {
    throw new NotImplementedException();
  }

  /**
   * Returns all the favors that are active in a given radius.
   *
   * @param loc Location to search around (Android location type)
   * @param radius a given radius to search within
   */
  public ArrayList<User> retrieveOtherUsersInGivenRadius(Location loc, double radius) {

    throw new NotImplementedException();
  }
}
