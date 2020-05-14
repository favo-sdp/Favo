package ch.epfl.favo.view.tabs;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import ch.epfl.favo.R;
import ch.epfl.favo.auth.SignInActivity;
import ch.epfl.favo.user.User;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.viewmodel.IFavorViewModel;

public class UserAccountPage extends Fragment {

  private View view;
  private IFavorViewModel viewModel;

  public UserAccountPage() {
    // Required empty public constructor
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    view = inflater.inflate(R.layout.fragment_user_account, container, false);

    setupButtons();

    displayUserData(DependencyFactory.getCurrentFirebaseUser());
    displayUserDetails(new User());

    viewModel =
        (IFavorViewModel)
            new ViewModelProvider(requireActivity())
                .get(DependencyFactory.getCurrentViewModelClass());
    UserUtil.getSingleInstance()
        .findUser(DependencyFactory.getCurrentFirebaseUser().getUid())
        .thenAccept(this::displayUserDetails);

    return view;
  }

  private IFavorViewModel getViewModel() {
    return viewModel;
  }

  private void setupButtons() {
    Button signOutButton = view.findViewById(R.id.sign_out);
    signOutButton.setOnClickListener(this::signOut);

    Button deleteAccountButton = view.findViewById(R.id.delete_account);
    deleteAccountButton.setOnClickListener(this::deleteAccountClicked);
  }

  private void displayUserDetails(User user) {
    ((TextView) view.findViewById(R.id.user_account_favorsCreated))
        .setText(getString(R.string.favors_created_format, user.getRequestedFavors()));
    ((TextView) view.findViewById(R.id.user_account_favorsAccepted))
        .setText(getString(R.string.favors_accepted_format, user.getAcceptedFavors()));
    ((TextView) view.findViewById(R.id.user_account_favorsCompleted))
        .setText(getString(R.string.favors_completed_format, user.getCompletedFavors()));
    ((TextView) view.findViewById(R.id.user_account_likes))
        .setText(getString(R.string.likes_format, user.getLikes()));
    ((TextView) view.findViewById(R.id.user_account_dislikes))
        .setText(getString(R.string.dislikes_format, user.getDislikes()));
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
        .setText(
            TextUtils.isEmpty(user.getEmail()) ? getText(R.string.no_email_text) : user.getEmail());
  }

  private void signOut(View view) {
    AuthUI.getInstance()
        .signOut(requireActivity())
        .addOnCompleteListener(task -> onComplete(task, R.string.sign_out_failed));
  }

  private void deleteAccountClicked(View view) {
    new AlertDialog.Builder(requireActivity())
        .setMessage(getText(R.string.delete_account_alert))
        .setPositiveButton(getText(R.string.yes_text), (dialogInterface, i) -> deleteAccount())
        .setNegativeButton(getText(R.string.no_text), null)
        .show();
  }

  private void deleteAccount() {
    AuthUI.getInstance()
        .delete(requireActivity())
        .addOnCompleteListener(task -> onComplete(task, R.string.delete_account_failed));
  }

  private void onComplete(@NonNull Task<Void> task, int errorMessage) {
    if (task.isSuccessful()) {
      startActivity(new Intent(getActivity(), SignInActivity.class));
    } else {
      CommonTools.showSnackbar(getView(), getString(errorMessage));
    }
  }
}
