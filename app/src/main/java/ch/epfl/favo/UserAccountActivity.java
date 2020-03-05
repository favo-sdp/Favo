package ch.epfl.favo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class UserAccountActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setTheme(R.style.AppTheme);
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_user_account);
    displayUserData(Objects.requireNonNull(DependencyFactory.getCurrentFirebaseUser()));
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

    ((TextView) findViewById(R.id.user_providers))
        .setText(getString(R.string.used_providers, user.getProviderId()));
  }

  public void signOut(View view) {
    AuthUI.getInstance().signOut(this).addOnCompleteListener(this::onComplete);
  }

  public void deleteAccountClicked(View view) {
    new AlertDialog.Builder(this)
        .setMessage("Are you sure you want to delete this account?")
        .setPositiveButton("Yes", (dialogInterface, i) -> deleteAccount())
        .setNegativeButton("No", null)
        .show();
  }

  private void deleteAccount() {
    AuthUI.getInstance().delete(this).addOnCompleteListener(this::onComplete);
  }

  public void onComplete(@NonNull Task<Void> task) {
    startActivity(new Intent(UserAccountActivity.this, SignInActivity.class));
    finish();
  }
}
