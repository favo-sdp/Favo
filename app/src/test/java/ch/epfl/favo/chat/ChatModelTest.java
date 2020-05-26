package ch.epfl.favo.chat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.chat.Model.Message;
import ch.epfl.favo.database.DatabaseWrapper;

import static ch.epfl.favo.TestConstants.FAVOR_ID;
import static ch.epfl.favo.TestConstants.MESSAGE_IMAGE_PATH;
import static ch.epfl.favo.TestConstants.MESSAGE_IS_FIRST_MESSAGE;
import static ch.epfl.favo.TestConstants.MESSAGE_LATITUDE;
import static ch.epfl.favo.TestConstants.MESSAGE_LONGITUDE;
import static ch.epfl.favo.TestConstants.MESSAGE_NOTIF_ID;
import static ch.epfl.favo.TestConstants.MESSAGE_USER_ID;
import static ch.epfl.favo.TestConstants.MESSAGE_USER_NAME;
import static ch.epfl.favo.TestConstants.MESSAGE_VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ChatModelTest {

  private Message model;
  private Date timestamp;
  private String id;

  @Before
  public void setUp() {
    id = DatabaseWrapper.generateRandomId();
    timestamp = new Date();
    model = FakeItemFactory.getMessage(id);
    model.setTimestamp(timestamp);
  }

  @Test
  public void testConstructors() {
    Message oneMessage = new Message();
    oneMessage.setName(MESSAGE_USER_NAME);
    Message secondMessage =
        new Message(
            MESSAGE_USER_NAME,
            MESSAGE_USER_ID,
            0,
            MESSAGE_VALUE,
            MESSAGE_IMAGE_PATH,
            MESSAGE_NOTIF_ID,
            FAVOR_ID,
            MESSAGE_IS_FIRST_MESSAGE);
    assertEquals(oneMessage.getName(), secondMessage.getName());
  }

  @Test
  public void testEquals_EqualModels() {

    Message modelEqual =
        new Message(
            id,
            MESSAGE_USER_NAME,
            MESSAGE_USER_ID,
            0,
            MESSAGE_VALUE,
            MESSAGE_IMAGE_PATH,
            MESSAGE_NOTIF_ID,
            FAVOR_ID,
            MESSAGE_IS_FIRST_MESSAGE,
            MESSAGE_LATITUDE,
            MESSAGE_LONGITUDE);
    modelEqual.setTimestamp(timestamp);
    assertEquals(model, modelEqual);
  }

  @Test
  public void testEquals_SameObjects() {
    assertEquals(model, model);
  }

  @Test
  public void testEquals_NonEqualModels() {
    Message modelEqual = FakeItemFactory.getMessage(id);
    modelEqual.setUid("random");

    assertNotEquals(model, modelEqual);
  }

  @Test
  public void testHashCode_EqualCodes() {
    Message modelEqual = FakeItemFactory.getMessage(id);
    modelEqual.setTimestamp(timestamp);
    assertEquals(model.hashCode(), modelEqual.hashCode());
  }

  @Test
  public void testHashCode_DifferentCodes() {
    Message modelEqual = FakeItemFactory.getMessage(id);
    modelEqual.setName("blabla");
    modelEqual.setTimestamp(timestamp);
    assertNotEquals(model.hashCode(), modelEqual.hashCode());
  }

  @Test
  public void testToString() {

    assertEquals(
        String.format(
            "Chat{mId='%s', mName='testName', mMessage='testMessage', mMessageType=0', mUid='testUid', mNotifId='testNotifId', mFavorId='%s', mTimestamp=%s', mIsFirstMsg='true', mImagePath='testImagePath', mLatitude='1.23', mLongitude='1.25'}",
            model.getId(), FAVOR_ID, timestamp),
        model.toString());
  }

  @Test
  public void testMapTransformation() {
    Map<String, Object> expected =
        new HashMap<String, Object>() {
          {
            put(Message.ID, model.getId());
            put(Message.NAME, model.getName());
            put(Message.MESSAGE, model.getMessage());
            put(Message.MESSAGE_TYPE, model.getMessageType());
            put(Message.UID, model.getUid());
            put(Message.NOTIF_ID, model.getNotifId());
            put(Message.FAVOR_ID, model.getFavorId());
            put(Message.IS_FIRST_MESSAGE, model.getIsFirstMsg());
            put(Message.IMAGE_PATH, model.getPicturePath());
            put(Message.LATITUDE, model.getLatitude());
            put(Message.LONGITUDE, model.getLongitude());
            put(Message.TIME_STAMP, model.getTimestamp());
          }
        };
    Assert.assertEquals(expected, model.toMap());
  }
}
