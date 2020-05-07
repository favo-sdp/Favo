package ch.epfl.favo.view;

import android.content.Intent;
import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.FakeViewModel;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.util.DependencyFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class MainActivityWithBundleTest {

  @Rule
  public ActivityTestRule<MainActivity> mActivityRule =
      new ActivityTestRule<>(MainActivity.class, false, false);

  @Before
  public void setUp() {
    DependencyFactory.setCurrentFirebaseUser(
        new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
    DependencyFactory.setCurrentGpsTracker(new MockGpsTracker());
    DependencyFactory.setCurrentViewModelClass(FakeViewModel.class);
  }

  private void launchActivityWithIntent(String url) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse(url));
    mActivityRule.launchActivity(intent);
  }

  @Test
  public void testMainActivityWithDynamicLink() throws InterruptedException {

    String url =
        "https://favoapp.page.link?sd=Check%20out%20this%20favor%20in%20the%20Favo%20App!&st=Favor%20Hello%20title&apn=ch.epfl.favo&link=https%3A%2F%2Fwww.favoapp.com%2F%3FfavorId%3DZWTJL0NKZVG7J0L1UQW9C11QVI7A";

    launchActivityWithIntent(url);

    Thread.sleep(3000);
    //onView(withId(R.id.fragment_favor_accept_view)).check(matches(isDisplayed()));
  }

  @Test
  public void testMainActivityWithDynamicLink_MissingQueryParameter() {

    String url =
        "https://favoapp.page.link?sd=Check%20out%20this%20favor%20in%20the%20Favo%20App!&st=Favor%20Hello%20title&apn=ch.epfl.favo&link=https%3A%2F%2Fwww.favoapp.com%2F%3F";

    launchActivityWithIntent(url);

    onView(withParent(withId(R.id.nav_host_fragment))).check(matches(isDisplayed()));
  }
}
