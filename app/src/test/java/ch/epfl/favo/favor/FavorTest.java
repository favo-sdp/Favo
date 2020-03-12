package ch.epfl.favo.favor;

import android.location.Location;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

<<<<<<< HEAD:app/src/test/java/ch/epfl/favo/favor/FavorUnitTests.java
import ch.epfl.favo.TestConstants;
=======

import ch.epfl.favo.common.TestUtil;
>>>>>>> code complete on UserDatabase callbacks:app/src/test/java/ch/epfl/favo/favor/FavorTest.java
import ch.epfl.favo.common.NotImplementedException;
import ch.epfl.favo.util.TestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class FavorUnitTests {

  @Test
  public void favorCanRetrieveDetailsFromDatabase() {

    String favorId = "2negoinr3";
    assertThrows(
        NotImplementedException.class,
        () -> FavorDatabase.getSingleInstance().getFavorDetailsFromDB(favorId));
  }

  @Test
  public void favorCanRemoveDetailsFromDatabase() {

    String favorId = "2negoinr3";
    assertThrows(
        NotImplementedException.class,
        () -> FavorDatabase.getSingleInstance().removeFavorFromDB(favorId));
  }

  @Test
  public void favorGettersReturnCorrectValues() {

    String title = "Flat tire";
    String description = "Tiire popped while turning left on Avenue Rhodanie";
    String requesterId = "2362489";
    Location location = new Location("Dummy provider");
    int statusId = 0;

    Favor favor = new Favor(title, description, requesterId, location, statusId);

    assertEquals(title, favor.getTitle());
    assertEquals(description, favor.getDescription());
    assertEquals(requesterId, favor.getRequesterId());
    assertEquals(location, favor.getLocation());
    assertEquals(statusId, favor.getStatusId());
  }

  @Test
  public void favorSettersCorrectlyUpdateValues() {

    Favor favor = new Favor();
    int statusId = 3;
    Location location = new Location("Dummy provider 2");
    String accepterId = "2364652";
    favor.setStatusId(3);
    favor.setLocation(location);
    favor.setAccepterID(accepterId);

    assertEquals(location, favor.getLocation());
    assertEquals(statusId, favor.getStatusId());
    assertEquals(accepterId, favor.getAccepterID());
  }

  @Test
  public void favorIsNotLongerThan300Characters() {
    String title = "Sample Favor";
    String description = TestUtil.generateRandomString(305);
    String location = "valid location"; // replace by valid location
    assertThrows(
            NotImplementedException.class,
            new ThrowingRunnable() {
              @Override
              public void run() throws Throwable {
                FavorUtil.getSingleInstance().postFavor(title, description, location);
              }
            });
  }

  @Test
  public void favorCanRetrieveAllFavorsForGivenUser() {

    String userId = TestConstants.USER_ID;
    assertThrows(
            NotImplementedException.class,
            new ThrowingRunnable() {
              @Override
              public void run() throws Throwable {
                FavorUtil.getSingleInstance().retrieveAllFavorsForGivenUser(userId);
              }
            });
  }

  @Test
  public void favorCanRetrieveAllActiveFavorsForGivenUser() {

    String userId = TestConstants.USER_ID;
    assertThrows(
            NotImplementedException.class,
            new ThrowingRunnable() {
              @Override
              public void run() throws Throwable {
                FavorUtil.getSingleInstance().retrieveAllActiveFavorsForGivenUser(userId);
              }
            });
  }

  @Test
  public void favorCanRetrieveAllRequestedFavorsForGivenUser() {

    String userId = TestConstants.USER_ID;
    assertThrows(
            NotImplementedException.class,
            new ThrowingRunnable() {
              @Override
              public void run() throws Throwable {
                FavorUtil.getSingleInstance().retrieveAllRequestedFavorsForGivenUser(userId);
              }
            });
  }

  @Test
  public void favorCanRetrieveAllAcceptedFavorsForGivenUser() {

    String userId = TestConstants.USER_ID;
    assertThrows(
            NotImplementedException.class,
            new ThrowingRunnable() {
              @Override
              public void run() throws Throwable {
                FavorUtil.getSingleInstance().retrieveAllAcceptedFavorsForGivenUser(userId);
              }
            });
  }

  @Test
  public void favorCanRetrieveAllFavorsInGivenRadius() {

    Location loc = TestConstants.LOCATION;
    double radius = TestConstants.RADIUS;
    assertThrows(
            NotImplementedException.class,
            new ThrowingRunnable() {
              @Override
              public void run() throws Throwable {
                FavorUtil.getSingleInstance().retrieveAllFavorsInGivenRadius(loc, radius);
              }
            });
  }
}
