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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.user.User;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;

import static ch.epfl.favo.user.UserUtil.USER_COLLECTION;

@SuppressLint("NewApi")
public class SignInActivity extends AppCompatActivity {
  public static final String TAG = "SignInActivity";
  public static final int RC_SIGN_IN = 123;
  private static final String URL_APP_NOT_FOUND = "https://google.com";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_in);

    // check for google play services and make request if not present
    checkPlayServices();

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
      String deviceId = DependencyFactory.getDeviceId(getApplicationContext().getContentResolver());

      DocumentReference docRef =
          DependencyFactory.getCurrentFirestore().collection(USER_COLLECTION).document(userId);
      docRef
          .get()
          .addOnCompleteListener(
              task -> {
                if (task.isSuccessful()) {
                  DocumentSnapshot document = task.getResult();
                  if (document != null && document.exists()) {
                    User user = document.toObject(User.class);
                    if (user != null) {
                      user.setDeviceId(deviceId);
                      updateNotificationToken(user, false);
                    }
                  } else {
                    User user = new User(currentUser, deviceId);
                    if (user.getName() == null || user.getName().equals(""))
                      user.setName(CommonTools.emailToName(user.getEmail()));
                    docRef
                        .set(user.toMap())
                        .addOnSuccessListener(
                            aVoid -> {
                              Log.d(TAG, "DocumentSnapshot successfully written!");
                              updateNotificationToken(user, true);
                            })
                        .addOnFailureListener(e -> onSignInFailed(Objects.requireNonNull(e)));
                  }
                } else {
                  onSignInFailed(Objects.requireNonNull(task.getException()));
                }
              });
    } else {
      // if sign-in was cancelled, return back to home page
      if (resultCode == RESULT_CANCELED) {
        onBackPressed();
      }
    }
  }

  private void updateNotificationToken(User user, boolean isNewUser) {
    FirebaseInstanceId.getInstance()
        .getInstanceId()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                if (isNewUser) {
                  DependencyFactory.getCurrentFirestore()
                      .collection(USER_COLLECTION)
                      .document(user.getId())
                      .delete();
                }
                onSignInFailed(Objects.requireNonNull(task.getException()));
                return;
              }

              // Get new Instance ID token
              String token = Objects.requireNonNull(task.getResult()).getToken();
              user.setNotificationId(token);

              DependencyFactory.getCurrentFirestore()
                  .collection(USER_COLLECTION)
                  .document(user.getId())
                  .update(user.toMap())
                  .addOnSuccessListener(
                      aVoid -> {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        startMainActivity();
                      })
                  .addOnFailureListener(
                      e -> {
                        if (isNewUser) {
                          DependencyFactory.getCurrentFirestore()
                              .collection(USER_COLLECTION)
                              .document(user.getId())
                              .delete();
                        }
                        onSignInFailed(Objects.requireNonNull(e));
                      });
            });
  }

  private void onSignInFailed(Throwable ex) {
    Log.e(TAG, "Sign-in failed: " + ex.getMessage());
    CommonTools.showSnackbar(
        getWindow().getDecorView().getRootView(), getString(R.string.sign_in_failed));

    // log out user on fail
    FirebaseAuth.getInstance().signOut();
    this.recreate();
  }
}
