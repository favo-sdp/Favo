package ch.epfl.favo.view.tabs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import ch.epfl.favo.R;
import ch.epfl.favo.auth.SignInActivity;
import ch.epfl.favo.user.User;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.viewmodel.IFavorViewModel;

@SuppressLint("NewApi")
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

    String currentUserId = DependencyFactory.getCurrentFirebaseUser().getUid();

    setupButtons();

    displayUserDetails(new User(null, "Name", "Email", null, null, null));

    viewModel =
        (IFavorViewModel)
            new ViewModelProvider(requireActivity())
                .get(DependencyFactory.getCurrentViewModelClass());

    UserUtil.getSingleInstance()
      .findUser(currentUserId)
      .whenComplete((user, e) -> displayUserDetails(user))
      .whenComplete((user, e) -> setupEditProfileDialog(inflater, user));

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

  private void setupEditProfileDialog(LayoutInflater inflater, User user) {
    Button editProfileButton = view.findViewById(R.id.edit_profile);
    View profileHolderView = view.findViewById(R.id.user_profile_holder);
    TextView displayNameView = view.findViewById(R.id.user_name);

    View dialogView = inflater.inflate(R.layout.edit_profile_details_dialog, null);
    final EditText displayNameInput = dialogView.findViewById(R.id.change_name_dialog_user_input);

    AlertDialog changeProfileDetailsDialog = new AlertDialog.Builder(requireContext())
      .setView(dialogView)
      .setNegativeButton(R.string.name_change_dialog_negative, ((dialog, which) -> dialog.dismiss()))
      .setPositiveButton(R.string.name_change_dialog_positive, (dialog, which) -> {
        user.setName(displayNameInput.getText().toString());
        UserUtil.getSingleInstance().updateUser(user);
        displayNameView.setText(displayNameInput.getText());
        dialog.dismiss();
      })
      .create();

    editProfileButton.setOnClickListener(v -> {
      displayNameInput.setText(displayNameView.getText());
      changeProfileDetailsDialog.show();
    });
  }

  private void displayUserDetails(User user) {
    TextView nameView = view.findViewById(R.id.user_name);
    nameView.setText(user.getName());

    TextView emailView = view.findViewById(R.id.user_email);
    emailView.setText(
        TextUtils.isEmpty(user.getEmail()) ? getText(R.string.no_email_text) : user.getEmail());

    if (user.getProfilePictureUrl() != null) {
      ImageView profilePictureView = view.findViewById(R.id.user_profile_picture);

      Glide.with(this)
        .load(user.getProfilePictureUrl())
        .fitCenter()
        .into(profilePictureView);
    }

    TextView favorsCreatedView = view.findViewById(R.id.user_account_favorsCreated);
    favorsCreatedView.setText(getString(R.string.favors_created_format, user.getRequestedFavors()));

    TextView favorsAcceptedView= view.findViewById(R.id.user_account_favorsAccepted);
    favorsAcceptedView.setText(getString(R.string.favors_accepted_format, user.getAcceptedFavors()));

    TextView favorsCompletedView = view.findViewById(R.id.user_account_favorsCompleted);
    favorsCompletedView.setText(getString(R.string.favors_completed_format, user.getCompletedFavors()));

    TextView accountLikesView = view.findViewById(R.id.user_account_likes);
    accountLikesView.setText(getString(R.string.likes_format, user.getLikes()));

    TextView accountDislikesView = view.findViewById(R.id.user_account_dislikes);
    accountDislikesView.setText(getString(R.string.dislikes_format, user.getDislikes()));
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
