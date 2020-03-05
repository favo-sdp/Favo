package ch.epfl.favo.testhelpers;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;

import androidx.test.core.app.ApplicationProvider;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class TestHelper {

  private static final String EMAIL = "test@example.com";
  private static final String NAME = "Test Testerson";
  private static final String UID = "uid";
  private static final Uri PHOTO_URI = Uri.parse("http://example.com/profile.png");

  private static Context CONTEXT = ApplicationProvider.getApplicationContext();

  static {
    FirebaseApp app = mock(FirebaseApp.class);
    when(app.get(eq(FirebaseAuth.class))).thenReturn(mock(FirebaseAuth.class));
    when(app.getApplicationContext()).thenReturn(CONTEXT);
    when(app.getName()).thenReturn("[DEFAULT]");
  }

  public static void initialize() {
    spyContextAndResources();
    AuthUI.setApplicationContext(CONTEXT);
    if (FirebaseApp.getApps(CONTEXT).isEmpty()) {
      FirebaseApp.initializeApp(
          CONTEXT,
          new FirebaseOptions.Builder().setApiKey("fake").setApplicationId("fake").build());
    }
  }

  private static void spyContextAndResources() {
    CONTEXT = spy(CONTEXT);
    when(CONTEXT.getApplicationContext()).thenReturn(CONTEXT);
    Resources spiedResources = spy(CONTEXT.getResources());
    when(CONTEXT.getResources()).thenReturn(spiedResources);
  }

  public static FirebaseUser createMockFirebaseUser() {
    FirebaseUser user = mock(FirebaseUser.class);
    when(user.getUid()).thenReturn(UID);
    when(user.getEmail()).thenReturn(EMAIL);
    when(user.getDisplayName()).thenReturn(NAME);
    when(user.getPhotoUrl()).thenReturn(PHOTO_URI);
    return user;
  }
}
