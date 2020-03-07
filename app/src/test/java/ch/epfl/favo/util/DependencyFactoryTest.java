package ch.epfl.favo.util;

import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.favo.FakeFirebaseUser;

import static ch.epfl.favo.TestConstants.*;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static junit.framework.TestCase.assertEquals;

public class DependencyFactoryTest {

  @Before
  public void setup() {
    new DependencyFactory();
  }

  @Test
  public void testSetInstance() {
    FirebaseUser testUser = new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER);
    DependencyFactory.setCurrentFirebaseUser(testUser);
    assertEquals(DependencyFactory.getCurrentFirebaseUser(), testUser);
  }
}
