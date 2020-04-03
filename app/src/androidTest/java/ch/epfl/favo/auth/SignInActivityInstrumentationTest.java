package ch.epfl.favo.auth;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.util.DependencyFactory;

@RunWith(AndroidJUnit4.class)
public class SignInActivityInstrumentationTest {

  @Rule
  public final ActivityTestRule<SignInActivity> mActivityRule =
      new ActivityTestRule<>(SignInActivity.class);

  @Before
  public void setup() {
    DependencyFactory.setCurrentFirebaseUser(null);
  }

  @After
  public void tearDown() {
    DependencyFactory.setCurrentFirebaseUser(null);
  }

  @Test
  public void testGenerateNotificationToken() {
    mActivityRule.getActivity().retrieveCurrentRegistrationToken();
  }
}
