package ch.epfl.favo.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.Function;

import ch.epfl.favo.BuildConfig;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.map.IGpsTracker;
import ch.epfl.favo.user.IUserUtil;
import ch.epfl.favo.user.User;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;

@SuppressLint("NewApi")
public class SignInActivity extends AppCompatActivity {

  public static final int RC_SIGN_IN = 123;
  private static final String TAG = "SignInActivity";
  private IGpsTracker mGpsTracker;

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

  private IUserUtil getCurrentUserUtil() {
    return DependencyFactory.getCurrentUserRepository();
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

    if (DependencyFactory.getCurrentFirebaseUser() != null && getIntent().getExtras() == null) {
      startMainActivity();
    }
  }

  private void startMainActivity() {
    startActivity(new Intent(this, MainActivity.class));
    finish();
  }

  void handleSignInResponse(int resultCode) throws RuntimeException {

    if (resultCode == RESULT_OK) {
      // Successfully signed in

      // Lookup user with Firebase Id in Db to extract details
      FirebaseUser currentUser = DependencyFactory.getCurrentFirebaseUser();
      String userId = DependencyFactory.getCurrentFirebaseUser().getUid();
      CompletableFuture<User> userFuture = getCurrentUserUtil().findUser(userId);
      String deviceId = DependencyFactory.getDeviceId(getApplicationContext().getContentResolver());
      // Add/update user info depending on db status
      CompletableFuture<User> editUserFuture = userFuture.thenCompose(editDeviceId(deviceId));
      CompletableFuture<User> newUserFuture =
          userFuture
              .exceptionally(
                  throwable -> new User(currentUser, deviceId, mGpsTracker.getLocation()))
              .whenComplete(postNewUser());
      CompletableFuture postUserResult =
          editUserFuture.acceptEither(newUserFuture, user -> startMainActivity());
      postUserResult.exceptionally(
          ex -> {
            Log.d(TAG, "failed to post user");
            CommonTools.showSnackbar(
                getWindow().getDecorView().getRootView(), getString(R.string.sign_in_failed));
            return null;
          });
    }
  }

  private BiConsumer<User, Throwable> postNewUser() {
    return (user, throwable) -> {
      final User finalUser = user;
      CompletableFuture postNewUser =
          getCurrentUserUtil()
              .postUser(finalUser)
              .thenAccept(o -> getCurrentUserUtil().retrieveUserRegistrationToken(finalUser));
    };
  }

  private Function<User, CompletionStage<User>> editDeviceId(String deviceId) {
    return (user) -> { // user is not null
      if (!deviceId.equals(user.getDeviceId())) {
        user.setDeviceId(deviceId);
        return getCurrentUserUtil().updateUser(user);
      } else return CompletableFuture.supplyAsync(() -> null);
    };
  }
}
