package ch.epfl.favo.view.tabs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;

import ch.epfl.favo.R;
import ch.epfl.favo.auth.SignInActivity;
import ch.epfl.favo.user.User;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.IPictureUtil.Folder;
import ch.epfl.favo.util.PictureUtil;

import static android.app.Activity.RESULT_OK;
import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

@SuppressLint("NewApi")
public class UserAccountPage extends Fragment {

  private static final int PICK_IMAGE_REQUEST = 1;
  private static final int USE_CAMERA_REQUEST = 2;

  private View view;
  private ImageView mImageView;
  private User currentUser;

  public UserAccountPage() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    view = inflater.inflate(R.layout.fragment_user_account, container, false);

    setupButtons();

    displayUserDetails(new User(null, "", "", null, null, null));

    DependencyFactory.getCurrentUserRepository()
        .findUser(DependencyFactory.getCurrentFirebaseUser().getUid())
        .whenComplete((user, e) -> currentUser = user)
        .whenComplete((user, e) -> displayUserDetails(user))
        .whenComplete((user, e) -> setupEditProfileDialog(inflater, user));

    return view;
  }

  private void setupButtons() {
    Button signOutButton = view.findViewById(R.id.sign_out);
    signOutButton.setOnClickListener(this::signOut);

    Button deleteAccountButton = view.findViewById(R.id.delete_account);
    deleteAccountButton.setOnClickListener(this::deleteAccountClicked);
  }

  private void setupEditProfileDialog(LayoutInflater inflater, User user) {
    Button editProfileButton = view.findViewById(R.id.edit_profile);
    TextView displayNameView = view.findViewById(R.id.user_name);
    ImageView profilePictureView = view.findViewById(R.id.user_profile_picture);

    View dialogView = inflater.inflate(R.layout.edit_profile_details_dialog, null);
    final EditText displayNameInput = dialogView.findViewById(R.id.change_name_dialog_user_input);
    mImageView = dialogView.findViewById(R.id.new_profile_picture);

    setupPictureUploadButtons(dialogView);

    AlertDialog changeProfileDetailsDialog =
        new AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setNegativeButton(
                R.string.name_change_dialog_negative, ((dialog, which) -> dialog.dismiss()))
            .setPositiveButton(
                R.string.name_change_dialog_positive,
                (dialog, which) -> {
                  user.setName(displayNameInput.getText().toString());
                  if (mImageView.getDrawable() != null) {
                    Bitmap newProfilePicture =
                        ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                    PictureUtil.getInstance()
                        .uploadPicture(Folder.PROFILE_PICTURE, newProfilePicture)
                        .thenAccept(
                            url -> {
                              user.setProfilePictureUrl(url);
                              DependencyFactory.getCurrentUserRepository().updateUser(user);
                            });
                    profilePictureView.setImageBitmap(newProfilePicture);
                    mImageView.setImageResource(0);
                  } else {
                    DependencyFactory.getCurrentUserRepository().updateUser(user);
                  }

                  displayNameView.setText(displayNameInput.getText());
                  dialog.dismiss();
                })
            .create();

    editProfileButton.setOnClickListener(
        v -> {
          displayNameInput.setText(displayNameView.getText());
          changeProfileDetailsDialog.show();
        });
  }

  /** Identifes buttons and sets onclick listeners. */
  private void setupPictureUploadButtons(View rootView) {

    // Button: Add Image from files
    ImageButton addPictureFromFilesBtn = rootView.findViewById(R.id.add_picture_button);
    addPictureFromFilesBtn.setOnClickListener(new onButtonClick());

    // Button: Add picture from camera
    ImageButton addPictureFromCameraBtn = rootView.findViewById(R.id.add_camera_picture_button);
    addPictureFromCameraBtn.setOnClickListener(new onButtonClick());
    if (!isCameraAvailable()) { // if camera is not available
      addPictureFromCameraBtn.setEnabled(false);
    }
  }

  class onButtonClick implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.add_camera_picture_button:
          takePicture();
          break;
        case R.id.add_picture_button:
          openFileChooser();
          break;
      }
    }
  }

  /**
   * Called when upload file from storage button is clicked. Method calls external fileChooser
   * intent.
   */
  public void openFileChooser() {
    Intent openFileChooserIntent = new Intent();
    openFileChooserIntent.setType("image/*");
    openFileChooserIntent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(openFileChooserIntent, PICK_IMAGE_REQUEST);
  }

  /** Called when camera button is clicked Method calls camera intent. */
  private void takePicture() {
    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {
      requireActivity()
          .requestPermissions(new String[] {Manifest.permission.CAMERA}, USE_CAMERA_REQUEST);
    } else {
      Intent takePictureIntent = DependencyFactory.getCurrentCameraIntent();

      if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
        startActivityForResult(takePictureIntent, USE_CAMERA_REQUEST);
      }
    }
  }

  private boolean isCameraAvailable() {
    boolean hasCamera =
        requireActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    int numberOfCameras = Camera.getNumberOfCameras();
    return (hasCamera && numberOfCameras != 0);
  }
  /**
   * This method is called when external intents are used to load data on view.
   *
   * @param requestCode integer value specifying which intent was launched
   * @param resultCode integer indicating whether intent was successful
   * @param data result from intent. In this case it contains picture data
   */
  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    super.onActivityResult(requestCode, resultCode, data);
    // If intent was not succesful
    if (resultCode != RESULT_OK || data == null) {
      CommonTools.showSnackbar(requireView(), getString(R.string.error_msg_image_request_view));
      return;
    }
    switch (requestCode) {
      case PICK_IMAGE_REQUEST:
        {
          Uri mImageUri = data.getData();
          mImageView.setImageURI(mImageUri);
          break;
        }
      case USE_CAMERA_REQUEST:
        {
          Bundle extras = data.getExtras();
          Bitmap imageBitmap = (Bitmap) extras.get("data");
          mImageView.setImageBitmap(imageBitmap);
          break;
        }
    }
  }

  private void displayUserDetails(User user) {
    TextView nameView = view.findViewById(R.id.user_name);
    nameView.setText(user.getName());

    TextView emailView = view.findViewById(R.id.user_email);
    emailView.setText(user.getEmail());

    if (user.getProfilePictureUrl() != null) {
      ImageView profilePictureView = view.findViewById(R.id.user_profile_picture);

      Glide.with(this).load(user.getProfilePictureUrl()).fitCenter().into(profilePictureView);
    }

    TextView favorsCreatedView = view.findViewById(R.id.user_account_favorsCreated);
    favorsCreatedView.setText(getString(R.string.favors_created_format, user.getRequestedFavors()));

    TextView favorsAcceptedView = view.findViewById(R.id.user_account_favorsAccepted);
    favorsAcceptedView.setText(
        getString(R.string.favors_accepted_format, user.getAcceptedFavors()));

    TextView favorsCompletedView = view.findViewById(R.id.user_account_favorsCompleted);
    favorsCompletedView.setText(
        getString(R.string.favors_completed_format, user.getCompletedFavors()));

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
      if (errorMessage == R.string.delete_account_failed) {
        // remove user data from database
        DependencyFactory.getCurrentUserRepository().deleteUser(currentUser);
        // remove user preferences from cache
        getDefaultSharedPreferences(requireContext()).edit().clear().apply();
      }
      startActivity(new Intent(getActivity(), SignInActivity.class));
    } else {
      CommonTools.showSnackbar(getView(), getString(errorMessage));
    }
  }
}
