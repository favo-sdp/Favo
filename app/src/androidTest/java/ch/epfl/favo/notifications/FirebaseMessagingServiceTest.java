package ch.epfl.favo.notifications;

import android.os.Bundle;

import androidx.test.platform.app.InstrumentationRegistry;
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

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import ch.epfl.favo.FakeFirebaseUser;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.util.DependencyFactory;

import static ch.epfl.favo.TestConstants.EMAIL;
import static ch.epfl.favo.TestConstants.NAME;
import static ch.epfl.favo.TestConstants.PHOTO_URI;
import static ch.epfl.favo.TestConstants.PROVIDER;
import static com.google.android.gms.common.api.CommonStatusCodes.TIMEOUT;
import static org.junit.Assert.assertEquals;

public class FirebaseMessagingServiceTest {

    private final static String NOTIFICATION_TITLE = "title";
    private final static String NOTIFICATION_BODY = "body";

    @Rule
    public final ActivityTestRule<MainActivity> mainActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class){
                @Override
                protected void beforeActivityLaunched() {
                    DependencyFactory.setCurrentFirebaseUser(new FakeFirebaseUser(NAME, EMAIL, PHOTO_URI, PROVIDER));
                }
            };

    @After
    public void tearDown() {
        DependencyFactory.setCurrentFirebaseUser(null);
    }

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);


    @Test
    public void testNotifications() {

        Bundle bundle = new Bundle();
        bundle.putString("google.delivered_priority", "high");
        bundle.putLong("google.sent_time",  (new Date()).getTime());
        bundle.putLong("google.ttl", 2419200);
        bundle.putString("google.original_priority", "high");
        bundle.putString("google.message_id", UUID.randomUUID().toString());
        bundle.putString("from", "533932732600");
        bundle.putString("gcm.notification.title", NOTIFICATION_TITLE);
        bundle.putString("gcm.notification.body", NOTIFICATION_BODY);
        bundle.putString("gcm.notification.e", "1");

        FirebaseMessagingService.showNotification(mainActivityTestRule.getActivity(), Objects.requireNonNull(new RemoteMessage(bundle).getNotification()), "Default channel id");

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.openNotification();
        device.wait(Until.hasObject(By.text(NOTIFICATION_TITLE)), TIMEOUT);
        UiObject2 title = device.findObject(By.text(NOTIFICATION_TITLE));
        UiObject2 text = device.findObject(By.text(NOTIFICATION_BODY));
        assertEquals(NOTIFICATION_TITLE, title.getText());
        assertEquals(NOTIFICATION_BODY, text.getText());
        title.click();
    }
}
