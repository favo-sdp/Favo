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
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.gps.IGpsTracker;
import ch.epfl.favo.user.IUserUtil;
import ch.epfl.favo.user.User;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;

@SuppressLint("NewApi")
public class SignInActivity extends AppCompatActivity {
  public static final String TAG = "SignInActivity";
  public static final int RC_SIGN_IN = 123;
  private static final String URL_APP_NOT_FOUND = "https://google.com";
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
      // update user name if firebase user has no name
      if (user.getDisplayName() == null || user.getDisplayName().equals("")) {
        UserProfileChangeRequest profileUpdates =
            new UserProfileChangeRequest.Builder()
                .setDisplayName(CommonTools.emailToName(Objects.requireNonNull(user.getEmail())))
                .build();
        user.updateProfile(profileUpdates);
      }
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

  /**
   * Create a new sign-in intent for FirebaseAuth to initialize the login flow
   *
   * @return sign-in intent
   */
  @NonNull
  public Intent createSignInIntent() {
    ActionCodeSettings actionCodeSettings = getActionCodeSettings();

    List<AuthUI.IdpConfig> providers = getProviders(actionCodeSettings);

    // Create sign-in intent
    AuthUI.SignInIntentBuilder builder =
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(false, false)
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
        .setAndroidPackageName(getString(R.string.package_name), true, null)
        .setHandleCodeInApp(true)
        .setUrl(URL_APP_NOT_FOUND)
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

  /**
   * Handle sign-in response with the result code provided by FirebaseAuth and, if successful,
   * creates/updates the current user logged in the app
   *
   * @param resultCode: code indicating the result of the login activity
   */
  void handleSignInResponse(int resultCode) {

    if (resultCode == RESULT_OK) {
      // Successfully signed in

      // Lookup user with Firebase Id in Db to extract details
      FirebaseUser currentUser = DependencyFactory.getCurrentFirebaseUser();
      String userId = DependencyFactory.getCurrentFirebaseUser().getUid();

      CompletableFuture<User> userFuture =
          DependencyFactory.getCurrentUserRepository().findUser(userId);
      String deviceId = DependencyFactory.getDeviceId(getApplicationContext().getContentResolver());
      // Try to edit user. If not completed properly, we create a new user and post it in the db.
      CompletableFuture<User> editUserFuture =
          userFuture
              .thenCompose((Function<User, CompletionStage<User>>) user -> editUser(user, deviceId))
              .exceptionally(
                  throwable -> new User(currentUser, deviceId, mGpsTracker.getLocation()));
      CompletableFuture<User> loginFuture =
          editUserFuture.thenCompose(
              (Function<User, CompletionStage<User>>)
                  user ->
                      (user != null)
                          ? postNewUserFuture(user)
                          : CompletableFuture.supplyAsync(() -> null));
      loginFuture
          .thenAccept(user -> startMainActivity())
          .exceptionally(
              ex -> {
                Log.d(TAG, "failed to post user");
                CommonTools.showSnackbar(
                    getWindow().getDecorView().getRootView(), getString(R.string.sign_in_failed));
                this.recreate();
                return null;
              });
    }
  }
  // Returs null but need to use raw type to chain this future
  private CompletableFuture postNewUserFuture(User user) {
    if (user.getName() == null || user.getName().equals(""))
      user.setName(CommonTools.emailToName(user.getEmail()));
    return DependencyFactory.getCurrentUserRepository()
        .postUser(user)
        .thenAccept(o -> getCurrentUserUtil().postUserRegistrationToken(user));
  }

  private CompletableFuture editUser(User user, String deviceId) {
    if (!deviceId.equals(user.getDeviceId())) {
      user.setDeviceId(deviceId);
      return getCurrentUserUtil().updateUser(user);
    } else if (user.getName() == null || user.getName().equals("")) {
      user.setName(CommonTools.emailToName(user.getEmail()));
      return getCurrentUserUtil().updateUser(user);
    } else return CompletableFuture.supplyAsync(() -> null);
  }

  private IUserUtil getCurrentUserUtil() {
    return DependencyFactory.getCurrentUserRepository();
  }
}
