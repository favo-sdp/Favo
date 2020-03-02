package ch.epfl.favo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Already signed-in
            startActivity(new Intent(this, UserAccountActivity.class));
            finish();
            return;
        }

        if (AuthUI.canHandleIntent(getIntent())) {
            catchEmailLinkSignIn();
        } else {
            signIn();
        }

        setContentView(R.layout.activity_sign_in);
    }

    public void catchEmailLinkSignIn() {
        if (AuthUI.canHandleIntent(getIntent())) {
            if (getIntent().getData() == null) {
                return;
            }
            String link = getIntent().getData().toString();
            signInWithEmailLink(link);
        }
    }

    public void signIn() {
        startActivityForResult(createSignInIntent(null), RC_SIGN_IN);
    }

    public void signInWithEmailLink(@Nullable String link) {
        startActivityForResult(createSignInIntent(link), RC_SIGN_IN);
    }

    @NonNull
    public Intent createSignInIntent(@Nullable String link) {

        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName("ch.epfl.favo", true, null)
                .setHandleCodeInApp(true)
                .setUrl("https://google.com") // This URL needs to be whitelisted
                .build();

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().enableEmailLinkSignIn().setActionCodeSettings(actionCodeSettings).build());

        // Create sign-in intent
        AuthUI.SignInIntentBuilder builder = AuthUI.getInstance().createSignInIntentBuilder()
                .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                .setAvailableProviders(providers)
                .setLogo(R.drawable.logo2)
                .setTheme(R.style.GreenTheme);

        if (link != null) {
            builder.setEmailLink(link);
        }

        return builder.build();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                startActivity(new Intent().setClass(this, UserAccountActivity.class));
                finish();
            } else {

                if (response == null) {
                    // User pressed back button
                    showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.no_internet_connection);
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.ERROR_USER_DISABLED) {
                    showSnackbar(R.string.account_disabled);
                    return;
                }

                showSnackbar(R.string.unknown_error);
                Log.e(TAG, "Sign-in error: ", response.getError());

            }
        }
    }

    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(findViewById(R.id.root), errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

}
