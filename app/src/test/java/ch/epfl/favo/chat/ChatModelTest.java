package ch.epfl.favo.chat;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ChatModelTest {

  private ChatModel model;
  private Date timestamp;

  @Before
  public void setUp() {
    timestamp = new Date();
    model = new ChatModel("testName", "testMessage", "testUid", "testNotifId", "testFavorId", "true");
    model.setTimestamp(timestamp);
  }

  @Test
  public void testEquals_EqualModels() {
    ChatModel modelEqual = new ChatModel("testName", "testMessage", "testUid", "testNotifId", "testFavorId", "true");
    modelEqual.setTimestamp(timestamp);
    assertEquals(model, modelEqual);
  }

  @Test
  public void testEquals_SameObjects() {
    assertEquals(model, model);
  }

  @Test
  public void testEquals_NonEqualModels() {
    ChatModel modelEqual = new ChatModel("testName", "testMessage", "fadsfa", "testNotifId", "testFavorId", "true");
    assertNotEquals(model, modelEqual);
  }

  @Test
  public void testHashCode_EqualCodes() {
    ChatModel modelEqual = new ChatModel("testName", "testMessage", "testUid", "testNotifId", "testFavorId", "true");
    modelEqual.setTimestamp(timestamp);
    assertEquals(model.hashCode(), modelEqual.hashCode());
  }

  @Test
  public void testHashCode_DifferentCodes() {
    ChatModel modelEqual = new ChatModel("adsfaf", "testMessage", "testUid", "testNotifId", "testFavorId", "true");
    modelEqual.setTimestamp(timestamp);
    assertNotEquals(model.hashCode(), modelEqual.hashCode());
  }

  @Test
  public void testToString() {
    assertEquals(String.format(
        "Chat{mName='testName', mMessage='testMessage', mUid='testUid', mNotifId='testNotifId', " +
                "mFavorId='testFavorId', mTimestamp=%s, mIsFirstMsg='true'}", timestamp),
        model.toString());
  }
}
