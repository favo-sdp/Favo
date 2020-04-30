package ch.epfl.favo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class BitmapConversionUtilTest {

  private byte[] val  = "test".getBytes();

  @Test
  public void byteArrayToBitmapTest() {
    Bitmap expected = BitmapFactory.decodeByteArray(val, 0, val.length);
    assertEquals(expected, BitmapConversionUtil.byteArrayToBitmap(val));
  }

  @Test
  public void bitmapToJpegInputStreamTest() {
    Bitmap bitmap = BitmapFactory.decodeByteArray(val, 0, val.length);
    assertThrows(
      NullPointerException.class,
      () -> BitmapConversionUtil.bitmapToJpegInputStream(bitmap));
  }
}
