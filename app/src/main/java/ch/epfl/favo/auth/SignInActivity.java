package ch.epfl.favo.auth;

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

import ch.epfl.favo.BuildConfig;
import ch.epfl.favo.MainActivity;
import ch.epfl.favo.MainActivity2;
import ch.epfl.favo.R;
import ch.epfl.favo.util.DependencyFactory;

public class SignInActivity extends AppCompatActivity {

  private static final int RC_SIGN_IN = 123;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_in);

    FirebaseUser user = DependencyFactory.getCurrentFirebaseUser();
    if (user != null) {
      // Already signed-in
      startMainActivity();
      return;
    }

    startActivityForResult(createSignInIntent(), RC_SIGN_IN);
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
      IdpResponse idpResponse = IdpResponse.fromResultIntent(intent);
      handleSignInResponse(idpResponse, resultCode);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    if (auth.getCurrentUser() != null && getIntent().getExtras() == null) {
      startMainActivity();
    }
  }

  private void startMainActivity() {
    startActivity(new Intent(this, MainActivity.class));
    finish();
  }

  private void handleSignInResponse(IdpResponse idpResponse, int resultCode) {

    if (resultCode == RESULT_OK) {
      // Successfully signed in
      startMainActivity();
    } else {

      if (idpResponse == null) {
        showSnackbar(R.string.sign_in_cancelled);
        return;
      }

      if (Objects.requireNonNull(idpResponse.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
        showSnackbar(R.string.no_internet_connection);
        return;
      }

      showSnackbar(R.string.unknown_error);
    }
  }

  public void showSnackbar(@StringRes int errorMessageRes) {
    Snackbar.make(findViewById(R.id.root), errorMessageRes, Snackbar.LENGTH_LONG).show();
  }
}
