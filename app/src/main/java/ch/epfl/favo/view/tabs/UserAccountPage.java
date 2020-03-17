package ch.epfl.favo.view.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import ch.epfl.favo.R;
import ch.epfl.favo.auth.SignInActivity;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.ViewController;

public class UserAccountPage extends Fragment {

  private View view;

  public UserAccountPage() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
      // Inflate the layout for this fragment
      view = inflater.inflate(R.layout.account_info, container, false);

      setupButtons();
      setupView();
      displayUserData(Objects.requireNonNull(DependencyFactory.getCurrentFirebaseUser()));
      return view;

  }

  private void setupView(){
    ((ViewController) getActivity()).showBackIcon();
    ((ViewController) getActivity()).hideBottomTabs();
  }

  private void setupButtons() {
    Button signOutButton = view.findViewById(R.id.sign_out);
    signOutButton.setOnClickListener(this::signOut);

    Button deleteAccountButton = view.findViewById(R.id.delete_account);
    deleteAccountButton.setOnClickListener(this::deleteAccountClicked);
  }

  private void displayUserData(FirebaseUser user) {

    if (user.getPhotoUrl() != null) {
      Glide.with(this)
          .load(user.getPhotoUrl())
          .fitCenter()
          .into((ImageView) view.findViewById(R.id.user_profile_picture));
    }

    ((TextView) view.findViewById(R.id.user_name))
        .setText(
            TextUtils.isEmpty(user.getDisplayName())
                ? Objects.requireNonNull(user.getEmail()).split("@")[0]
                : user.getDisplayName());

    ((TextView) view.findViewById(R.id.user_email))
        .setText(TextUtils.isEmpty(user.getEmail()) ? "No email" : user.getEmail());

    ((TextView) view.findViewById(R.id.user_providers))
        .setText(getString(R.string.used_providers, user.getProviderId()));
  }

  private void signOut(View view) {
    AuthUI.getInstance()
        .signOut(Objects.requireNonNull(getActivity()))
        .addOnCompleteListener(task -> onComplete(task, R.string.sign_out_failed));
  }

  private void deleteAccountClicked(View view) {
    new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
        .setMessage("Are you sure you want to delete this account?")
        .setPositiveButton("Yes", (dialogInterface, i) -> deleteAccount())
        .setNegativeButton("No", null)
        .show();
  }

  private void deleteAccount() {
    AuthUI.getInstance()
        .delete(Objects.requireNonNull(getActivity()))
        .addOnCompleteListener(task -> onComplete(task, R.string.delete_account_failed));
  }

  private void onComplete(@NonNull Task<Void> task, int errorMessage) {
    if (task.isSuccessful()) {
      startActivity(new Intent(getActivity(), SignInActivity.class));
    } else {
      showSnackbar(errorMessage);
    }
  }

  private void showSnackbar(@StringRes int errorMessageRes) {
    Snackbar.make(view, errorMessageRes, Snackbar.LENGTH_LONG).show();
  }
}
