package ch.epfl.favo.requestview;

import android.content.Intent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.view.tabs.addFavor.FavorRequestView;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.spy;

public class RequestViewTest {

  // These tests just want to make sure that no exception is thrown when
  // the result action of the favor fragment_favor_detail view is handled

  private FavorRequestView spy;

  @Before
  public void setup() {
    spy = spy(FavorRequestView.class);
  }

  @Test
  public void testFileChooser() {
    Mockito.doNothing().when(spy).startActivityForResult(any(Intent.class), anyInt());
    spy.openFileChooser();
  }

  @Test
  public void testViewStatusShowDesiredStrings() {
    Assert.assertEquals("Expired", FavorStatus.EXPIRED.toString());
  }
}
