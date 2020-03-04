package ch.epfl.favo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class UserAccountActivity extends AppCompatActivity {

  // public FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

  //    @VisibleForTesting
  //    public void setCurrentFirebaseUser(FirebaseUser u) {
  //        this.currentFirebaseUser = u;
  //    }

  public FirebaseUser getCurrentFirebaseUser() {
    return FirebaseAuth.getInstance().getCurrentUser();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setTheme(R.style.AppTheme);
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_user_account);
    displayUserData(Objects.requireNonNull(getCurrentFirebaseUser()));
    // createUser(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()));
  }

  public void displayUserData(FirebaseUser user) {

    if (user.getPhotoUrl() != null) {
      Glide.with(getApplicationContext())
          .load(user.getPhotoUrl())
          .fitCenter()
          .into((ImageView) findViewById(R.id.user_profile_picture));
    }

    ((TextView) findViewById(R.id.user_name))
        .setText(
            TextUtils.isEmpty(user.getDisplayName())
                ? Objects.requireNonNull(user.getEmail()).split("@")[0]
                : user.getDisplayName());
    ((TextView) findViewById(R.id.user_email))
        .setText(TextUtils.isEmpty(user.getEmail()) ? "No email" : user.getEmail());

    //        List<String> providers = new ArrayList<>();
    //        if (!user.getProviderData().isEmpty()) {
    //            for (UserInfo info : user.getProviderData()) {
    //                switch (info.getProviderId()) {
    //                    case GoogleAuthProvider.PROVIDER_ID:
    //                        providers.add(getString(R.string.providers_google));
    //                        break;
    //                    case FacebookAuthProvider.PROVIDER_ID:
    //                        providers.add(getString(R.string.providers_facebook));
    //                        break;
    //                    default:
    //                        providers.add(info.getProviderId());
    //                }
    //            }
    //        }
    ((TextView) findViewById(R.id.user_providers))
        .setText(getString(R.string.used_providers, user.getProviderId()));
  }

  public AuthUI getCurrentAuthUIInstance() {
    return AuthUI.getInstance();
  }

  public void signOut(View view) {
    getCurrentAuthUIInstance()
        .signOut(this)
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {
                startActivity(new Intent(UserAccountActivity.this, SignInActivity.class));
                finish();
              } else {
                // Log.w(TAG, "signOut:failure", task.getException());
                showSnackbar(R.string.sign_out_failed);
              }
            });
  }

  public void deleteAccountClicked(View view) {
    new AlertDialog.Builder(this)
        .setMessage("Are you sure you want to delete this account?")
        .setPositiveButton("Yes", (dialogInterface, i) -> deleteAccount())
        .setNegativeButton("No", null)
        .show();
  }

  private void deleteAccount() {
    getCurrentAuthUIInstance()
        .delete(this)
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {
                startActivity(new Intent(UserAccountActivity.this, SignInActivity.class));
                finish();
              } else {
                showSnackbar(R.string.delete_account_failed);
              }
            });
  }

  public void showSnackbar(@StringRes int errorMessageRes) {
    Snackbar.make(findViewById(R.id.root), errorMessageRes, Snackbar.LENGTH_LONG).show();
  }
}
