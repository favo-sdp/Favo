package ch.epfl.favo.favor;

import org.junit.Test;

import java.util.Map;

import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.TestConstants;
import ch.epfl.favo.gps.FavoLocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for Favor object.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class FavorUnitTests {

  @Test
  public void favorGettersReturnCorrectValues() {

    Favor favor = FakeItemFactory.getFavorWithUrl();

    assertFalse(favor.getIsArchived());
    assertNotNull(favor.getUserIds());
    assertEquals(TestConstants.TITLE, favor.getTitle());
    assertEquals(TestConstants.DESCRIPTION, favor.getDescription());
    assertEquals(TestConstants.REQUESTER_ID, favor.getRequesterId());
    assertEquals(TestConstants.LOCATION, favor.getLocation());
    assertEquals(TestConstants.FAVOR_STATUS.toInt(), favor.getStatusId());
    assertEquals(TestConstants.PICTURE_URL, favor.getPictureUrl());
    assertEquals((int) TestConstants.REWARD, (int) favor.getReward());
    assertNotNull(favor.getPostedTime());
  }

  @Test
  public void favorSettersCorrectlyUpdateValues() {

    Favor favor = FakeItemFactory.getFavor();

    FavorStatus statusId = FavorStatus.CANCELLED_REQUESTER;
    FavoLocation location = TestConstants.LOCATION;
    String accepterId = TestConstants.ACCEPTER_ID;
    String otherPictureUrl = TestConstants.OTHER_PICTURE_URL;
    double reward = 4.25;

    favor.setStatusIdToInt(statusId);
    favor.setLocation(location);
    favor.setAccepterId(accepterId);
    favor.setPictureUrl(otherPictureUrl);
    favor.setReward(reward);

    assertEquals(location, favor.getLocation());
    assertEquals(statusId.toInt(), favor.getStatusId());
    assertEquals(accepterId, favor.getAccepterId());
    assertEquals(otherPictureUrl, favor.getPictureUrl());
    assertEquals((int) reward, (int) favor.getReward());
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
    assertEquals(TestConstants.FAVOR_STATUS.toInt(), favors[0].getStatusId());
    assertEquals((int) TestConstants.REWARD, (int) favors[0].getReward());
  }

  @Test
  public void favorGivesCorrectTransformationToMap() {
    Favor favor = FakeItemFactory.getFavor();
    Map<String, Object> favorMap = favor.toMap();
    Favor favor2 = new Favor(favorMap);
    assertEquals(favor.getTitle(), favor2.getTitle());
    assertEquals(favor.getId(), favor2.getId());
    assertEquals(favor.getDescription(), favor2.getDescription());
    assertEquals(favor.getLocation(), favor2.getLocation());
    assertEquals(favor.getRequesterId(), favor2.getRequesterId());
    assertEquals(favor.getAccepterId(), favor2.getAccepterId());
    assertEquals(favor.getPostedTime(), favor2.getPostedTime());
    assertEquals(favor.getStatusId(), favor2.getStatusId());
    assertEquals(favor.getPictureUrl(), favor2.getPictureUrl());
    assertEquals((int) favor.getReward(), (int) favor2.getReward());
  }

  @Test
  public void favorComparisonIsSuccessful() {
    Favor favor = FakeItemFactory.getFavorWithUrl();
    Favor favor2 = FakeItemFactory.getFavorWithUrl();
    assertTrue(favor.contentEquals(favor2));
  }

  @Test
  public void favoLocationComparisonIsSuccessful() {
    Favor favor = FakeItemFactory.getFavor();
    FavoLocation location1 = favor.getLocation();
    FavoLocation location2 = new FavoLocation("whatever");
    FavoLocation location3 = location1;
    location2.setLatitude(location1.getLatitude());
    location2.setLongitude(location1.getLongitude());
    assertTrue(location1.equals(location2)); // check they're equal based on latitude and longitude
    assertTrue(!location1.equals(favor));
    assertTrue(location1.equals(location3)); // check reference equality
  }

  @Test
  public void favorCanBeUpdatedToOther() {
    Favor favor = FakeItemFactory.getFavor();
    String oldAccepterId = "old accepter Id";
    favor.setAccepterId(oldAccepterId);
    Favor anotherFavor = FakeItemFactory.getFavor();
    anotherFavor.setAccepterId("new accepter Id");
    favor.updateToOther(anotherFavor);
    assertEquals(oldAccepterId, favor.getAccepterId());
  }
}
