package ch.epfl.favo.requestview;

import android.text.Spanned;
import android.text.SpannedString;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import ch.epfl.favo.view.tabs.addFavor.InputFilterMinMax;

public class InputFilterTest {

  @Test
  public void testInputFilter_ReturnEmptyString() {
    InputFilterMinMax filter = new InputFilterMinMax(0, 50);
    Assert.assertEquals("", filter.filter("2", 0, 1, new SpannedString("20"), 0, 1));
  }

  @Test
  public void testInputFilter_ReturnEmptyStringForException() {
    InputFilterMinMax filter = new InputFilterMinMax(0, 50);
    CharSequence sequence = "not a number";
    Spanned spannedString = Mockito.mock(Spanned.class);
    Mockito.doReturn("not a number").when(spannedString).toString();
    Assert.assertEquals("", filter.filter(sequence, 0, 1, spannedString, 0, 1));
  }

  @Test
  public void testInputFilter_ReturnNull() {
    InputFilterMinMax filter = new InputFilterMinMax(0, 50);
    CharSequence sequence = "3";
    Spanned spannedString = Mockito.mock(Spanned.class);
    Mockito.doReturn("2").when(spannedString).toString();
    Assert.assertEquals("23", spannedString.toString() + sequence.toString());
    Assert.assertNull(filter.filter(sequence, 0, 1, spannedString, 0, 1));
  }
}
