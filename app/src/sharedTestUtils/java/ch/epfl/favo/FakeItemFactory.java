package ch.epfl.favo;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.favo.chat.Model.Message;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.user.User;
import ch.epfl.favo.util.CommonTools;

public class FakeItemFactory {

  public static Favor getFavor() {
    return new Favor(
        TestConstants.FAVOR_ID,
        TestConstants.TITLE,
        TestConstants.DESCRIPTION,
        TestConstants.REQUESTER_ID,
        TestConstants.LOCATION,
        TestConstants.FAVOR_STATUS.toInt(),
        TestConstants.REWARD);
  }

  public static Favor getFavorForRequester() {
    return new Favor(
        TestConstants.FAVOR_ID,
        TestConstants.TITLE,
        TestConstants.DESCRIPTION,
        TestConstants.USER_ID,
        TestConstants.LOCATION,
        TestConstants.FAVOR_STATUS.toInt(),
        TestConstants.REWARD);
  }

  public static Favor getFavorWithUrl() {
    Favor favor = getFavor();
    favor.setPictureUrl(TestConstants.PICTURE_URL);
    return favor;
  }

  public static FirebaseUser getFirebaseUser() {
    return new FakeFirebaseUser(
        TestConstants.NAME, TestConstants.EMAIL, TestConstants.PHOTO_URI, TestConstants.PROVIDER);
  }

  public static User getUser() {

    return new User(
        TestConstants.USER_ID,
        TestConstants.NAME,
        TestConstants.EMAIL,
        TestConstants.DEVICE_ID,
        TestConstants.BIRTHDAY,
        TestConstants.LOCATION);
  }

  public static List<Favor> getFavorList() {
    return new ArrayList<Favor>() {
      {
        add(getFavor());
        add(getFavor());
        add(getFavor());
      }
    };
  }

  public static Map<String, Favor> getFavorListMap() {
    List<Favor> favorList = getFavorList();
    Map<String, Favor> result = new HashMap<>(favorList.size());
    for (Favor favor : favorList) {
      result.put(favor.getId(), favor);
    }
    return result;
  }

  public static Message getMessage(String id) {
    return new Message(
        id,
        TestConstants.MESSAGE_USER_NAME,
        TestConstants.MESSAGE_USER_ID,
        CommonTools.TEXT_MESSAGE_TYPE,
        TestConstants.MESSAGE_VALUE,
        TestConstants.MESSAGE_IMAGE_PATH,
        TestConstants.MESSAGE_NOTIF_ID,
        TestConstants.FAVOR_ID,
        TestConstants.MESSAGE_IS_FIRST_MESSAGE,
        TestConstants.MESSAGE_LATITUDE,
        TestConstants.MESSAGE_LONGITUDE);
  }

}
