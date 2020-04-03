package ch.epfl.favo.notifications;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.google.firebase.messaging.RemoteMessage;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.common.DatabaseUpdater;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.FAVOR_ID;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.NOTIFICATION_BODY;
import static ch.epfl.favo.TestConstants.NOTIFICATION_TITLE;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static com.google.android.gms.common.api.CommonStatusCodes.TIMEOUT;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class FirebaseMessagingServiceTest {

  @Rule
  public final ActivityTestRule<MainActivity> mainActivityTestRule =
      new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
          DependencyFactory.setCurrentFirebaseUser(
              new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
          DependencyFactory.setCurrentDatabaseUpdater(new MockDatabaseWrapper());
        }
      };

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

  @After
  public void tearDown() {
    DependencyFactory.setCurrentFirebaseUser(null);
    DependencyFactory.setCurrentDatabaseUpdater(null);
    Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    mainActivityTestRule.getActivity().sendBroadcast(closeIntent);
  }

  @Test
  public void testNotifications() {
    Bundle bundle = generateBundle();

    FirebaseMessagingService.showNotification(
        mainActivityTestRule.getActivity(), new RemoteMessage(bundle), "Default channel id");

    // WORKS LOCALLY, NOT ON TRAVIS... BUT WORKS ON CIRRUS :)
    UiDevice device = UiDevice.getInstance(getInstrumentation());
    device.openNotification();

    device.wait(Until.hasObject(By.text(NOTIFICATION_TITLE)), TIMEOUT);
    UiObject2 title = device.findObject(By.text(NOTIFICATION_TITLE));
    UiObject2 text = device.findObject(By.text(NOTIFICATION_BODY));
    assertEquals(NOTIFICATION_TITLE, title.getText());
    assertEquals(NOTIFICATION_BODY, text.getText());
    title.click();
    getInstrumentation().waitForIdleSync();
    //    // check that tab 2 is indeed opened
    onView(withParent(withId(R.id.nav_host_fragment))).check(matches(isDisplayed()));
  }

  private Bundle generateBundle() {

    Bundle bundle = new Bundle();
    bundle.putString("google.delivered_priority", "high");
    bundle.putLong("google.sent_time", (new Date()).getTime());
    bundle.putLong("google.ttl", 2419200);
    bundle.putString("google.original_priority", "high");
    bundle.putString("google.message_id", UUID.randomUUID().toString());
    bundle.putString("from", "533932732600");
    bundle.putString("gcm.notification.title", NOTIFICATION_TITLE);
    bundle.putString("gcm.notification.body", NOTIFICATION_BODY);
    bundle.putString("gcm.notification.e", "1");
    bundle.putString("gcm.notification.tag", FAVOR_ID);
    bundle.putSerializable(
        "gcm.notification.data",
        new HashMap<String, String>() {
          {
            put("FavorId", FAVOR_ID);
          }
        });
    return bundle;
  }
}
