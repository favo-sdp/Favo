package ch.epfl.favo;

import android.location.Location;
import android.os.Parcel;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.DependencyFactory;

import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;

public class FavorAndroidTest {
  @Rule
  public final ActivityTestRule<MainActivity> activityTestRule =
      new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
          DependencyFactory.setCurrentFirebaseUser(
              new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
        }
      };

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

  @After
  public void tearDown() {
    DependencyFactory.setCurrentFirebaseUser(null);
  }

  @Test
  public void favorGettersReturnCorrectValuesByParcelable() {
    String title = "Flat tire";
    String description = "Tiire popped while turning left on Avenue Rhodanie";
    String requesterId = "2362489";
    Location location = new Location("Dummy provider");
    Favor.Status statusId = 0;

    Favor favor = new Favor(title, description, requesterId, location, statusId);
    int flag = 0;
    Parcel parcel = Parcel.obtain();
    favor.writeToParcel(parcel, flag);
    Favor favorNew = Favor.CREATOR.createFromParcel(parcel);
    // assertEquals(title, favorNew.getTitle());
    // assertEquals(description, favorNew.getDescription());
    // assertEquals(requesterId, favorNew.getRequesterId());
    // assertEquals(location, favorNew.getLocation());
    // assertEquals(statusId, favorNew.getStatusId());
  }
}
