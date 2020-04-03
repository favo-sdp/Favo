package ch.epfl.favo.favor;

import android.location.Location;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.mockito.Mockito;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.TestConstants;
import ch.epfl.favo.common.CollectionWrapper;
import ch.epfl.favo.common.FavoLocation;
import ch.epfl.favo.common.NotImplementedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class FavorUnitTests {

  @Test
  public void favorGettersReturnCorrectValues() {

    Favor favor = FakeItemFactory.getFavor();

    assertEquals(TestConstants.TITLE, favor.getTitle());
    assertEquals(TestConstants.DESCRIPTION, favor.getDescription());
    assertEquals(TestConstants.REQUESTER_ID, favor.getRequesterId());
    assertEquals(TestConstants.LOCATION, favor.getLocation());
    assertEquals(TestConstants.FAVOR_STATUS, favor.getStatusId());
    assertNotNull(favor.getPostedTime());
  }

  @Test
  public void favorSettersCorrectlyUpdateValues() {

    Favor favor = FakeItemFactory.getFavor();

    Favor.Status statusId = Favor.Status.CANCELLED_REQUESTER;
    FavoLocation location = new FavoLocation("Dummy provider 2");
    String accepterId = "2364652";

    favor.setStatusId(statusId);
    favor.setLocation(location);
    favor.setAccepterID(accepterId);

    assertEquals(location, favor.getLocation());
    assertEquals(statusId, favor.getStatusId());
    assertEquals(accepterId, favor.getAccepterID());
  }

  @Test
  public void favorSuccessfullyPostsToDB() {
    CollectionWrapper mock = Mockito.mock(CollectionWrapper.class);
    Mockito.doNothing().when(mock).addDocument(any(Favor.class));

    Favor favor = FakeItemFactory.getFavor();
    FavorUtil.getSingleInstance().postFavor(favor);

    assertNotNull(favor);
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

  @Test
  public void describeContentsCorrect() {

    Favor favor = FakeItemFactory.getFavor();

    assertEquals(favor.describeContents(), 0);
  }

  @Test
  public void CreatorArrayCorrect() {

    Favor[] favors = Favor.CREATOR.newArray(3);
    favors[0] = FakeItemFactory.getFavor();

    assertEquals(TestConstants.TITLE, favors[0].getTitle());
    assertEquals(TestConstants.DESCRIPTION, favors[0].getDescription());
    assertEquals(TestConstants.REQUESTER_ID, favors[0].getRequesterId());
    assertEquals(TestConstants.LOCATION, favors[0].getLocation());
    assertEquals(TestConstants.FAVOR_STATUS, favors[0].getStatusId());
  }

  @Test
  public void getDocumentFunction() throws ExecutionException, InterruptedException {
    // get favor from database
    String favorID = "WEZDZQD78A5SI5Q790SZAL7FW";
    assertThrows(
      RuntimeException.class,
      () -> FavorUtil.getSingleInstance().retrieveFavor(favorID).get());
    }

  @Test
  public void favorGivesCorrectTransformationToMap(){
    Favor favor = FakeItemFactory.getFavor();
    Map<String,Object> favorMap = favor.toMap();
    Favor favor2 = new Favor(favorMap);
    assertEquals(favor.getTitle(),favor2.getTitle());
    assertEquals(favor.getId(),favor2.getId());
    assertEquals(favor.getDescription(),favor2.getDescription());
    assertEquals(favor.getLocation(),favor2.getLocation());
    assertEquals(favor.getRequesterId(),favor2.getRequesterId());
    assertEquals(favor.getAccepterID(),favor2.getAccepterID());
    assertEquals(favor.getPostedTime(),favor2.getPostedTime());
    assertEquals(favor.getStatusId(),favor2.getStatusId());
  }
}
