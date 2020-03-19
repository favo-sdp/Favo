package ch.epfl.favo.util;

import org.junit.Test;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;

public class CommonToolsTests {
  CommonTools commonTools = new CommonTools();

  @Test
  public void ConvertTimeTest() {
    long date = new Date().getTime();
    Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
    String time = format.format(date);
    assertEquals(CommonTools.convertTime(date), time);
  }
}
