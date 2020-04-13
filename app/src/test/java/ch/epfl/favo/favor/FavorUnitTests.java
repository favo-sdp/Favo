package ch.epfl.favo.favor;

import org.junit.Test;

import java.util.Map;

import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.TestConstants;
import ch.epfl.favo.common.FavoLocation;

import static org.junit.Assert.assertEquals;
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
    favor.setAccepterId(accepterId);

    assertEquals(location, favor.getLocation());
    assertEquals(statusId, favor.getStatusId());
    assertEquals(accepterId, favor.getAccepterId());
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
  }
  @Test
  public void favorComparisonIsSuccessful(){
    Favor favor =FakeItemFactory.getFavor();
    Favor favor2 = FakeItemFactory.getFavor();
    assertTrue(favor.equals(favor2));
    Object o = new Object();
    assertTrue(!favor.equals(o));
    Favor favor3 = favor;
    assertTrue(favor.equals(favor3));
  }
    @Test
    public void favorCanBeUpdatedToOther(){
      Favor favor = FakeItemFactory.getFavor();
      String oldAccepterId = "old accepter Id";
      favor.setAccepterId(oldAccepterId);
      Favor anotherFavor = FakeItemFactory.getFavor();
      anotherFavor.setAccepterId("new accepter Id");
      favor.updateToOther(anotherFavor);
      assertEquals(oldAccepterId,favor.getAccepterId());

    }
}
