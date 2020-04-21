package ch.epfl.favo.user;

import android.location.Location;

import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.mockito.Mockito;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.TestConstants;
import ch.epfl.favo.common.CollectionWrapper;
import ch.epfl.favo.common.FavoLocation;
import ch.epfl.favo.common.NotImplementedException;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.TestUtils;
import ch.epfl.favo.view.MockDatabaseWrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UserUnitTests {

  @Before
  public void setup() {
    DependencyFactory.setCurrentCollectionWrapper(new MockDatabaseWrapper());
  }

  @Test
  public void userCanRemoveDetailsFromDatabase() {

    assertThrows(NotImplementedException.class, () -> UserUtil.getSingleInstance().deleteAccount());
  }

  @Test
  public void userGettersReturnCorrectValues() {

    String id = TestConstants.USER_ID;
    String name = TestConstants.NAME;
    String email = TestConstants.EMAIL;
    String deviceId = TestConstants.DEVICE_ID;
    Date birthDate = new Date();
    FavoLocation location = TestConstants.LOCATION;

    User user = new User(id, name, email, deviceId, birthDate, location);

    assertEquals(name, user.getName());
    assertEquals(email, user.getEmail());
    assertEquals(deviceId, user.getDeviceId());
    assertEquals(birthDate, user.getBirthDate());
    assertEquals(0, user.getActiveAcceptingFavors());
    assertEquals(0, user.getActiveRequestingFavors());

    // field should initialize null and populate later
    assertNull(user.getNotificationId());
  }

  @Test
  public void userSettersCorrectlyUpdateValues() {

    User user = new User();
    int activeAcceptingFavors = 3;
    int activeRequestingFavors = 4;
    String temporaryNotificationId = "temporaryNotificationId";
    String temporaryDeviceId = "temporaryDeviceId";
    FavoLocation newLoc = new FavoLocation();

    user.setActiveAcceptingFavors(activeAcceptingFavors);
    user.setActiveRequestingFavors(activeRequestingFavors);
    user.setNotificationId(temporaryNotificationId);
    user.setDeviceId(temporaryDeviceId);
    user.setLocation(newLoc);

    assertEquals(activeAcceptingFavors, user.getActiveAcceptingFavors());
    assertEquals(activeRequestingFavors, user.getActiveRequestingFavors());
    assertEquals(temporaryNotificationId, user.getNotificationId());
    assertEquals(temporaryDeviceId, user.getDeviceId());
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
  public void userSuccessfullyPostsToDB() {
    DependencyFactory.setCurrentCollectionWrapper(new MockDatabaseWrapper());
    CollectionWrapper collection = Mockito.mock(CollectionWrapper.class);
    Mockito.doNothing().when(collection).addDocument(any(User.class));

    String id = TestConstants.USER_ID;
    String name = TestConstants.NAME;
    String email = TestConstants.EMAIL;
    String deviceId = TestConstants.DEVICE_ID;
    FavoLocation location = TestConstants.LOCATION;

    User user = new User(id, name, email, deviceId, null, location);
    UserUtil.getSingleInstance().postUser(user);

    assertNotNull(user);
  }

  @Test
  public void userSuccessfullyRetrievedFromDB() {
    CollectionWrapper collection = Mockito.mock(CollectionWrapper.class);
    CompletableFuture<User> userFuture = new CompletableFuture<>();
    when(collection.getDocument(any(String.class))).thenReturn(userFuture);
    UserUtil.getSingleInstance().setCollectionWrapper(collection);
    String id = TestConstants.USER_ID;
    CompletableFuture<User> user = UserUtil.getSingleInstance().findUser(id);

    assertNotNull(user);
  }

  @Test
  public void userShouldNotLoginWithInvalidPassword() {
    String username = TestConstants.USERNAME;
    String pw = TestUtils.generateRandomString(10);
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
