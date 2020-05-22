package ch.epfl.favo.chat;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.chat.Model.Message;
import ch.epfl.favo.util.CommonTools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ChatModelTest {

  private Message model;
  private Date timestamp;

  @Before
  public void setUp() {
    timestamp = new Date();
    model = FakeItemFactory.getMessage();
    model.setTimestamp(timestamp);
  }

  @Test
  public void testEquals_EqualModels() {
    Message modelEqual =
        new Message(
            "testName",
            "testUid",
            CommonTools.TEXT_MESSAGE_TYPE,
            "testMessage",
            "imagePath",
            "testNotifId",
            "testFavorId",
            "true");
    modelEqual.setTimestamp(timestamp);
    assertEquals(model, modelEqual);
  }

  @Test
  public void testEquals_SameObjects() {
    assertEquals(model, model);
  }

  @Test
  public void testEquals_NonEqualModels() {
    Message modelEqual =
        new Message(
            "testName",
            "fadsfa",
            CommonTools.TEXT_MESSAGE_TYPE,
            "testMessage",
            "imagePath",
            "testNotifId",
            "testFavorId",
            "true");

    assertNotEquals(model, modelEqual);
  }

  @Test
  public void testHashCode_EqualCodes() {
    Message modelEqual = FakeItemFactory.getMessage();
    modelEqual.setTimestamp(timestamp);
    assertEquals(model.hashCode(), modelEqual.hashCode());
  }

  @Test
  public void testHashCode_DifferentCodes() {
    Message modelEqual = FakeItemFactory.getMessage();
    modelEqual.setName("blabla");
    modelEqual.setTimestamp(timestamp);
    assertNotEquals(model.hashCode(), modelEqual.hashCode());
  }

  @Test
  public void testToString() {
    assertEquals(
        String.format(
            "Chat{mName='testName', mMessage='testMessage', mUid='testUid', mNotifId='testNotifId', "
                + "mFavorId='testFavorId', mTimestamp=%s, mIsFirstMsg='true'}",
            timestamp),
        model.toString());
  }
}
