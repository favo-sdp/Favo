package ch.epfl.favo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

  private static final int RC_SIGN_IN = 123;

  //    public static FirebaseUser currentFirebaseUser =
  // FirebaseAuth.getInstance().getCurrentUser();
  //
  //    @VisibleForTesting
  //    public void setCurrentFirebaseUser(FirebaseUser u) {
  //        currentFirebaseUser = u;
  //    }

  public FirebaseUser getCurrentFirebaseUser() {
    return FirebaseAuth.getInstance().getCurrentUser();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_in);

    FirebaseUser user = getCurrentFirebaseUser();
    if (user != null) {
      // Already signed-in
      startActivity(new Intent(this, UserAccountActivity.class));
      finish();
      return;
    }

    startActivityForResult(createSignInIntent(), RC_SIGN_IN);
  }

  @NonNull
  public Intent createSignInIntent() {
    ActionCodeSettings actionCodeSettings =
        ActionCodeSettings.newBuilder()
            .setAndroidPackageName("ch.epfl.favo", true, null)
            .setHandleCodeInApp(true)
            .setUrl("https://google.com")
            .build();

    List<AuthUI.IdpConfig> providers =
        Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.FacebookBuilder().build(),
            new AuthUI.IdpConfig.EmailBuilder()
                .enableEmailLinkSignIn()
                .setActionCodeSettings(actionCodeSettings)
                .build());

    // Create sign-in intent
    AuthUI.SignInIntentBuilder builder =
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
            .setAvailableProviders(providers)
            .setLogo(R.drawable.logo)
            .setTheme(R.style.AppTheme);

    if (isIntentLinkValid() && getIntent().getData() != null) {
      String link = getIntent().getData().toString();
      builder.setEmailLink(link);
    }

    return builder.build();
  }

  public boolean isIntentLinkValid() {
    return AuthUI.canHandleIntent(getIntent());
  }

  @Override
  protected void onResume() {
    super.onResume();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    if (auth.getCurrentUser() != null && getIntent().getExtras() == null) {
      startActivity(new Intent().setClass(this, UserAccountActivity.class));
      finish();
    }
  }

  public IdpResponse getIdpResponseFromIntent(Intent intent) {
    return IdpResponse.fromResultIntent(intent);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == RC_SIGN_IN) {
      IdpResponse response = getIdpResponseFromIntent(data);

      if (resultCode == RESULT_OK) {
        // Successfully signed in
        startActivity(new Intent().setClass(this, UserAccountActivity.class));
        finish();
      } else {

        if (response == null) {
          showSnackbar(R.string.sign_in_cancelled);
          return;
        }

        if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
          showSnackbar(R.string.no_internet_connection);
          return;
        }

        showSnackbar(R.string.unknown_error);
      }
    }
  }

  public void showSnackbar(@StringRes int errorMessageRes) {
    Snackbar.make(findViewById(R.id.root), errorMessageRes, Snackbar.LENGTH_LONG).show();
  }
}
