package ch.epfl.favo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import ch.epfl.favo.testhelpers.TestHelper;

import static junit.framework.TestCase.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class DependencyFactoryTest {

  @Before
  public void setup() {
    TestHelper.initialize();
    new DependencyFactory();
  }

  @Test
  public void testGetInstanceFirstTime() {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    assertEquals(DependencyFactory.getCurrentFirebaseUser(), user);
  }

  @Test
  public void testSetInstance() {
    FirebaseUser testUser = TestHelper.createMockFirebaseUser();
    DependencyFactory.setCurrentFirebaseUser(testUser);
    assertEquals(DependencyFactory.getCurrentFirebaseUser(), testUser);
  }
}
