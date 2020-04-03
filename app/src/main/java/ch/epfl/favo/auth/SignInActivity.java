package ch.epfl.favo.auth;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ch.epfl.favo.BuildConfig;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.map.Locator;
import ch.epfl.favo.user.User;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.DependencyFactory;

public class SignInActivity extends AppCompatActivity {

  private static final int RC_SIGN_IN = 123;
  private Locator mGpsTracker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_in);

    // check for google play services and make request if not present
    checkPlayServices();

    // initialize location library
    mGpsTracker =
        DependencyFactory.getCurrentGpsTracker(Objects.requireNonNull(getApplicationContext()));

    FirebaseUser user = DependencyFactory.getCurrentFirebaseUser();
    if (user != null) {
      // Already signed-in
      startMainActivity();
      return;
    }

    startActivityForResult(createSignInIntent(), RC_SIGN_IN);
  }

  private void checkPlayServices() {
    GoogleApiAvailability gApi = GoogleApiAvailability.getInstance();
    int resultCode = gApi.isGooglePlayServicesAvailable(this);
    if (resultCode != ConnectionResult.SUCCESS) {
      gApi.makeGooglePlayServicesAvailable(this);
    }
  }

  @NonNull
  public Intent createSignInIntent() {
    ActionCodeSettings actionCodeSettings = getActionCodeSettings();

    List<AuthUI.IdpConfig> providers = getProviders(actionCodeSettings);

    // Create sign-in intent
    AuthUI.SignInIntentBuilder builder =
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
            .setAvailableProviders(providers)
            .setLogo(R.drawable.logo)
            .setTheme(R.style.AppTheme);

    if (AuthUI.canHandleIntent(getIntent()) && getIntent().getData() != null) {
      String link = getIntent().getData().toString();
      builder.setEmailLink(link);
    }

    return builder.build();
  }

  private List<AuthUI.IdpConfig> getProviders(ActionCodeSettings actionCodeSettings) {

    return Arrays.asList(
        new AuthUI.IdpConfig.GoogleBuilder().build(),
        new AuthUI.IdpConfig.FacebookBuilder().build(),
        new AuthUI.IdpConfig.EmailBuilder()
            .enableEmailLinkSignIn()
            .setRequireName(true)
            .setActionCodeSettings(actionCodeSettings)
            .build());
  }

  private ActionCodeSettings getActionCodeSettings() {
    return ActionCodeSettings.newBuilder()
        .setAndroidPackageName("ch.epfl.favo", true, null)
        .setHandleCodeInApp(true)
        .setUrl("https://google.com")
        .build();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);

    if (requestCode == RC_SIGN_IN) {
      handleSignInResponse(resultCode);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    checkPlayServices();

    FirebaseAuth auth = FirebaseAuth.getInstance();
    if (auth.getCurrentUser() != null && getIntent().getExtras() == null) {
      startMainActivity();
    }
  }

  private void startMainActivity() {
    startActivity(new Intent(this, MainActivity.class));
    finish();
  }

  void handleSignInResponse(int resultCode) {

    if (resultCode == RESULT_OK) {
      // Successfully signed in

      FirebaseUser currentUser = DependencyFactory.getCurrentFirebaseUser();
      String name = currentUser.getDisplayName();
      String email = currentUser.getEmail();
      Uri photo = currentUser.getPhotoUrl();
      Location loc = mGpsTracker.getLocation();
      String deviceId = currentUser.getUid();
      User user = new User(name, email, deviceId, null, loc);

      UserUtil.getSingleInstance().postAccount(user);
      UserUtil.getSingleInstance().retrieveUserRegistrationToken(user);

      startMainActivity();
    }
  }
}
