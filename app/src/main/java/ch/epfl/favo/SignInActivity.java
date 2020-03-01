package ch.epfl.favo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.util.ExtraConstants;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        List<AuthUI.IdpConfig> providers = Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.logo)      // Set logo drawable
                        .setTheme(R.style.FirebaseUI)      // Set theme
                        .build(),
                RC_SIGN_IN);
    }

    // [START auth_fui_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                startActivity(new Intent().setClass(this, UserAccountActivity.class).putExtra(ExtraConstants.IDP_RESPONSE, response));
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

//    public void getUserProfile() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            // Name, email address, and profile photo Url
//            String name = user.getDisplayName();
//            String email = user.getEmail();
//            Uri photoUrl = user.getPhotoUrl();
//
//            // Check if user's email is verified
//            boolean emailVerified = user.isEmailVerified();
//
//            // The user's ID, unique to the Firebase project. Do NOT use this value to
//            // authenticate with your backend server, if you have one. Use
//            // FirebaseUser.getIdToken() instead.
//            String uid = user.getUid();
//        }
//    }
//
//    public void getProviderData() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            for (UserInfo profile : user.getProviderData()) {
//                // Id of the provider (ex: google.com)
//                String providerId = profile.getProviderId();
//
//                // UID specific to the provider
//                String uid = profile.getUid();
//
//                // Name, email address, and profile photo Url
//                String name = profile.getDisplayName();
//                String email = profile.getEmail();
//                Uri photoUrl = profile.getPhotoUrl();
//            }
//        }
//    }
//
//    public void updateProfile() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                .setDisplayName("Jane Q. User")
//                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
//                .build();
//
//        assert user != null;
//        user.updateProfile(profileUpdates)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "User profile updated.");
//                        }
//                    }
//                });
//        // [END update_profile]
//    }
//
//    public void updateEmail() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        user.updateEmail("user@example.com")
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "User email address updated.");
//                        }
//                    }
//                });
//    }
//
//    public void updatePassword() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String newPassword = "SOME-SECURE-PASSWORD";
//
//        user.updatePassword(newPassword)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "User password updated.");
//                        }
//                    }
//                });
//    }
//
//    public void deleteUser() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        user.delete()
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "User account deleted.");
//                        }
//                    }
//                });
//    }
//
//    public void reauthenticate() {
//        // [START reauthenticate]
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        // Get auth credentials from the user for re-authentication. The example below shows
//        // email and password credentials but there are multiple possible providers,
//        // such as GoogleAuthProvider or FacebookAuthProvider.
//        AuthCredential credential = EmailAuthProvider
//                .getCredential("user@example.com", "password1234");
//
//        // Prompt the user to re-provide their sign-in credentials
//        user.reauthenticate(credential)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Log.d(TAG, "User re-authenticated.");
//                    }
//                });
//        // [END reauthenticate]
//    }
//
//    public void getGoogleCredentials() {
//        String googleIdToken = "";
//        AuthCredential credential = GoogleAuthProvider.getCredential(googleIdToken, null);
//    }

//    public void signOut() {
//        FirebaseAuth.getInstance().signOut();
//    }
//
//    private void enableUserManuallyInputCode() {
//        // No-op
//    }
//
//    private void updateUI(@Nullable FirebaseUser user) {
//        // No-op
//    }

}
