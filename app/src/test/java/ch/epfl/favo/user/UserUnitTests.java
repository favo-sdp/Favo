package ch.epfl.favo.user;

import android.location.Location;

import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import org.threeten.bp.LocalDate;

import ch.epfl.favo.TestConstants;
import ch.epfl.favo.common.NotImplementedException;
import ch.epfl.favo.util.TestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UserUnitTests {
  private User user;

  @Before
  public void setup() {
    user = new User();
  }

  @Test
  public void userCanRemoveDetailsFromDatabase() {

    String userId = TestConstants.USER_ID;
    assertThrows(NotImplementedException.class, () -> UserUtil.getSingleInstance().deleteAccount());
  }

  @Test
  public void userGettersReturnCorrectValues() {

    String name = TestConstants.NAME;
    String email = TestConstants.EMAIL;
    String deviceId = TestConstants.DEVICE_ID;
    LocalDate birthDate = LocalDate.of(1994, 11, 8);
    Location location = TestConstants.LOCATION;
    int acceptedFavors = 2;
    int requestedFavors = 3;

    User user =
        new User(name, email, deviceId, birthDate, location, acceptedFavors, requestedFavors);

    assertEquals(name, user.getName());
    assertEquals(email, user.getEmail());
    assertEquals(deviceId, user.getDeviceId());
    assertEquals(birthDate, user.getBirthDate());
    assertEquals(acceptedFavors, user.getActiveAcceptingFavors());
    assertEquals(requestedFavors, user.getActiveRequestingFavors());

    // field should initialize null and populate later
    assertNull(user.getNotificationId());
  }

  @Test
  public void userSettersCorrectlyUpdateValues() {

    User user = new User();
    int activeAcceptingFavors = 3;
    int activeRequestingFavors = 4;
    String temporaryId = "temporaryId";
    user.setActiveAcceptingFavors(activeAcceptingFavors);
    user.setActiveRequestingFavors(activeRequestingFavors);
    user.setNotificationId(temporaryId);

    assertEquals(activeAcceptingFavors, user.getActiveAcceptingFavors());
    assertEquals(activeRequestingFavors, user.getActiveRequestingFavors());
    assertEquals(temporaryId, user.getNotificationId());
  }

  @Test
  public void userCantAcceptOrRequestMoreThanOneOnce() {

    User user = new User();
    user.setActiveAcceptingFavors(2);
    user.setActiveRequestingFavors(3);

    assertFalse(user.canAccept());
    assertFalse(user.canRequest());
  }

  @Test
  public void userCanAcceptOrRequestAtMostOnce() {

    User user = new User();
    user.setActiveAcceptingFavors(0);
    user.setActiveRequestingFavors(0);

    assertTrue(user.canAccept());
    assertTrue(user.canRequest());
  }

  @Test
  public void userNameIsValid() {
    String username = TestConstants.USERNAME;
    String pw = TestConstants.PASSWORD;
    assertThrows(
        NotImplementedException.class,
        new ThrowingRunnable() {
          @Override
          public void run() throws Throwable {
            UserUtil.getSingleInstance().createAccount(username, pw);
          }
        });
  }

  @Test
  public void userShouldNotLoginWithInvalidPassword() {
    String username = TestConstants.USERNAME;
    String pw = TestUtil.generateRandomString(10);
    assertThrows(
        NotImplementedException.class,
        new ThrowingRunnable() {
          @Override
          public void run() throws Throwable {
            UserUtil.getSingleInstance().logInAccount(username, pw);
          }
        });
  }

  @Test
  public void userCanLogOutOnlyIfLoggedIn() {

    assertThrows(
        NotImplementedException.class,
        new ThrowingRunnable() {
          @Override
          public void run() throws Throwable {
            UserUtil.getSingleInstance().logOutAccount();
          }
        });
  }

  @Test
  public void userCanDeleteAccountOnlyIfAccountExists() {

    assertThrows(
        NotImplementedException.class,
        new ThrowingRunnable() {
          @Override
          public void run() throws Throwable {
            UserUtil.getSingleInstance().deleteAccount();
          }
        });
  }

  @Test
  public void userCanRetrieveOtherUsersInGivenRadius() {

    Location loc = TestConstants.LOCATION;
    double radius = TestConstants.RADIUS;
    assertThrows(
        NotImplementedException.class,
        new ThrowingRunnable() {
          @Override
          public void run() throws Throwable {
            UserUtil.getSingleInstance().retrieveOtherUsersInGivenRadius(loc, radius);
          }
        });
  }
}
