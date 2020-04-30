package ch.epfl.favo.user;

import android.location.Location;

import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.mockito.Mockito;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.TestConstants;
import ch.epfl.favo.common.CollectionWrapper;
import ch.epfl.favo.common.FavoLocation;
import ch.epfl.favo.common.NotImplementedException;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.TestUtil;
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
    assertEquals(0, user.getRequestedFavors());
    assertEquals(0, user.getAcceptedFavors());
    assertEquals(0, user.getCompletedFavors());
    assertEquals(0, user.getLikes());
    assertEquals(0, user.getDislikes());

    // field should initialize null and populate later
    assertNull(user.getNotificationId());
  }

  @Test
  public void userSettersCorrectlyUpdateValues() {

    int testNum = 2;

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
    user.setRequestedFavors(testNum);
    user.setAcceptedFavors(testNum);
    user.setCompletedFavors(testNum);
    user.setLikes(testNum);
    user.setDislikes(testNum);

    assertEquals(activeAcceptingFavors, user.getActiveAcceptingFavors());
    assertEquals(activeRequestingFavors, user.getActiveRequestingFavors());
    assertEquals(temporaryNotificationId, user.getNotificationId());
    assertEquals(temporaryDeviceId, user.getDeviceId());
    assertEquals(testNum, user.getRequestedFavors());
    assertEquals(testNum, user.getAcceptedFavors());
    assertEquals(testNum, user.getCompletedFavors());
    assertEquals(testNum, user.getLikes());
    assertEquals(testNum, user.getDislikes());
  }

  @Test
  public void userGivesCorrectTransformationToMap() {
    User user =
        new User(
            TestConstants.USER_ID,
            TestConstants.NAME,
            TestConstants.EMAIL,
            TestConstants.DEVICE_ID,
            null,
            null);
    ;
    Map<String, Object> userMap = user.toMap();
    User user2 = new User(userMap);
    assertEquals(user.getId(), user2.getId());
    assertEquals(user.getName(), user2.getName());
    assertEquals(user.getEmail(), user2.getEmail());
    assertEquals(user.getDeviceId(), user2.getDeviceId());
    assertEquals(user.getNotificationId(), user2.getNotificationId());
    assertEquals(user.getBirthDate(), user2.getBirthDate());
    assertEquals(user.getLocation(), user2.getLocation());
    assertEquals(user.getActiveRequestingFavors(), user2.getActiveRequestingFavors());
    assertEquals(user.getActiveAcceptingFavors(), user2.getActiveAcceptingFavors());
    assertEquals(user.getRequestedFavors(), user2.getRequestedFavors());
    assertEquals(user.getAcceptedFavors(), user2.getAcceptedFavors());
    assertEquals(user.getCompletedFavors(), user2.getCompletedFavors());
    assertEquals(user.getLikes(), user2.getLikes());
    assertEquals(user.getDislikes(), user2.getDislikes());
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

    CollectionWrapper collection = Mockito.mock(CollectionWrapper.class);
    CompletableFuture successfulFuture =
        new CompletableFuture() {
          {
            complete(null);
          }
        };
    Mockito.doReturn(successfulFuture).when(collection).addDocument(any(User.class));
    DependencyFactory.setCurrentCollectionWrapper(collection);

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
