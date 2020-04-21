package ch.epfl.favo.util;

import org.junit.Test;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.common.DatabaseWrapper;
import ch.epfl.favo.favor.Favor;

import static ch.epfl.favo.TestConstants.DESCRIPTION;
import static ch.epfl.favo.TestConstants.LOCATION;
import static ch.epfl.favo.TestConstants.REQUESTER_ID;
import static ch.epfl.favo.TestConstants.TITLE;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CommonToolsTests {

  @Test
  public void ConvertTimeTest() {
    long date = new Date().getTime();
    Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
    String time = format.format(date);
    assertEquals(CommonTools.convertTime(date), time);
  }

  @Test
  public void databaseUtilCorrectlyGeneratesIds() {
    int ID_LENGTH = 28;
    String id1 = DatabaseWrapper.generateRandomId();
    String id2 = DatabaseWrapper.generateRandomId();
    assertEquals(ID_LENGTH, id1.length());
    assertEquals(ID_LENGTH, id2.length());
    assertNotEquals(id1, id2);
  }
}
