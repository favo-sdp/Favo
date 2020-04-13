package ch.epfl.favo;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.favo.favor.Favor;

public class FakeItemFactory {

  public static Favor getFavor() {
    return new Favor(
        TestConstants.FAVOR_ID,
        TestConstants.TITLE,
        TestConstants.DESCRIPTION,
        TestConstants.REQUESTER_ID,
        TestConstants.LOCATION,
        TestConstants.STATUS_ID);
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
}
