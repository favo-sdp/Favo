package ch.epfl.favo.view.tabs.addFavor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.R;
import ch.epfl.favo.common.FavoLocation;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.map.Locator;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.FavorFragmentFactory;
import ch.epfl.favo.util.PictureUtil;

import static android.app.Activity.RESULT_OK;
import static ch.epfl.favo.util.CommonTools.hideSoftKeyboard;

@SuppressLint("NewApi")
public class FavorRequestView extends Fragment {

  public static final int PICK_IMAGE_REQUEST = 1;
  public static final int USE_CAMERA_REQUEST = 2;
  private FavorStatus favorStatus;
  private ImageView mImageView;
  private EditText mTitleView;
  private EditText mDescriptionView;
  private TextView mStatusView;
  private Locator mGpsTracker;
  private Button confirmFavorBtn;
  private Button addPictureFromFilesBtn;
  private Button addPictureFromCameraBtn;
  private Button cancelFavorBtn;
  private Button editFavorBtn;
  private Button chatBtn;

  private Favor currentFavor;

  public FavorRequestView() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_favor_request_view, container, false);
    setupButtons(rootView);
    // Edit text:
    mTitleView = rootView.findViewById(R.id.title_request_view);
    mTitleView.requestFocus();
    mDescriptionView = rootView.findViewById(R.id.details);
    mStatusView = rootView.findViewById(R.id.favor_status_text);
    setupView(rootView);
    // Extract other elements
    mImageView = rootView.findViewById(R.id.image_view_request_view);

    // Get dependencies
    mGpsTracker = DependencyFactory.getCurrentGpsTracker(requireActivity().getApplicationContext());
    // Inject argument

    if (getArguments() != null) {
      currentFavor = getArguments().getParcelable(FavorFragmentFactory.FAVOR_ARGS);
      displayFavorInfo(rootView);
      setFavorActivatedView(rootView);
    }
    return rootView;
  }

  /** When fragment is launched with favor. */
  public void displayFavorInfo(View v) {
    favorStatus = FavorStatus.toEnum(currentFavor.getStatusId());
    mTitleView.setText(currentFavor.getTitle());
    mDescriptionView.setText(currentFavor.getDescription());
    mStatusView.setText(favorStatus.toString());

    updateViewFromStatus(v);
  }

  /**
   * Identifes buttons and sets onclick listeners.
   *
   * @param rootView
   */
  private void setupButtons(View rootView) {

    // Button: Request Favor
    confirmFavorBtn = rootView.findViewById(R.id.request_button);
    confirmFavorBtn.setOnClickListener(v -> requestFavor());

    if (DependencyFactory.isOfflineMode(requireContext())) {
      confirmFavorBtn.setText(R.string.request_favor_draft);
    }

    // Button: Add Image from files
    addPictureFromFilesBtn = rootView.findViewById(R.id.add_picture_button);
    addPictureFromFilesBtn.setOnClickListener(v -> openFileChooser());

    // Button: Add picture from camera
    addPictureFromCameraBtn = rootView.findViewById(R.id.add_camera_picture_button);
    addPictureFromCameraBtn.setOnClickListener(v -> takePicture());
    if (!isCameraAvailable()) { // if camera is not available
      addPictureFromCameraBtn.setEnabled(false);
    }

    // Button: Cancel Favor
    cancelFavorBtn = rootView.findViewById(R.id.cancel_favor_button);
    cancelFavorBtn.setOnClickListener(v -> cancelFavor());

    // Button: Edit favor
    editFavorBtn = rootView.findViewById(R.id.edit_favor_button);
    editFavorBtn.setOnClickListener(
        v -> {
          // if text is currently "Update Request"
          if (favorStatus.equals(FavorStatus.EDIT)) {
            confirmUpdatedFavor();
          } else { // text is currently "Edit Request"
            startUpdatingFavor();
          }
        });

    // Chat button
    chatBtn = rootView.findViewById(R.id.chat_button);
    chatBtn.setOnClickListener(
        v -> {
          Bundle favorBundle = new Bundle();
          favorBundle.putParcelable("FAVOR_ARGS", currentFavor);
          Navigation.findNavController(requireView())
              .navigate(R.id.action_nav_favorRequestView_to_chatView, favorBundle);
        });
  }

  /**
   * Toggles edit text between editable/uneditable
   *
   * @param switchToEditable when true, will go from uneditable->editable
   */
  private void toggleTextViewsEditable(boolean switchToEditable) {
    if (!switchToEditable) {
      mTitleView.setKeyListener(null);
      mDescriptionView.setKeyListener(null);
    } else {
      mTitleView.setKeyListener((KeyListener) mTitleView.getTag());
      mDescriptionView.setKeyListener((KeyListener) mDescriptionView.getTag());
    }
  }

  /**
   * Method is called when request favor button is clicked. It uploads favor request to the database
   * and updates view so that favor is editable.
   */
  private void requestFavor() {
    // update currentFavor
    favorStatus = FavorStatus.REQUESTED;
    getFavorFromView(favorStatus);
    // post to DB
    FavorUtil.getSingleInstance().postFavor(currentFavor);

    // Show confirmation and minimize keyboard
    if (DependencyFactory.isOfflineMode(requireContext())) {
      showSnackbar(getString(R.string.save_draft_message));
    } else {
      showSnackbar(getString(R.string.favor_request_success_msg));
    }
    setFavorActivatedView(getView());
    updateViewFromStatus(getView());
  }
  /**
   * Once favor has been requested.
   *
   * @param v rootview
   */
  private void setFavorActivatedView(View v) {
    confirmFavorBtn.setVisibility(View.INVISIBLE);
    editFavorBtn.setVisibility(View.VISIBLE);
    cancelFavorBtn.setVisibility(View.VISIBLE);
    chatBtn.setVisibility(View.VISIBLE);
    toggleTextViewsEditable(false);
    updateViewFromStatus(v);
  }

  /** When edit button is clicked */
  private void startUpdatingFavor() {
    CompletableFuture<Favor> currentFavorFuture =
        FavorUtil.getSingleInstance().retrieveFavor(currentFavor.getId());
    currentFavorFuture.thenAccept(
        favor -> {
          if (favor.getStatusId() == FavorStatus.ACCEPTED.toInt()) {
            CommonTools.showSnackbar(getView(), getString(R.string.fail_edit_favor_request_view));
            favorStatus = FavorStatus.toEnum(favor.getStatusId());
            currentFavor.setStatusIdToInt(favorStatus);

          } else {
            favorStatus = FavorStatus.EDIT;
          }
          updateViewFromStatus(getView());
        });
    currentFavorFuture.exceptionally(
        e -> {
          showSnackbar(getString(R.string.update_favor_error));
          return null;
        });
  }

  /** Gets called once favor has been updated on view. */
  private void confirmUpdatedFavor() {
    currentFavor.setStatusIdToInt(FavorStatus.REQUESTED);
    getFavorFromView(favorStatus);
    updateViewFromStatus(getView());
    showSnackbar(getString(R.string.favor_edit_success_msg));

    // DB call to update Favor details
    FavorUtil.getSingleInstance().postFavor(currentFavor);
  }

  /** Updates favor on DB. Updates maps on main activity hides keyboard shows snackbar */
  private void cancelFavor() {
    currentFavor.setStatusIdToInt(FavorStatus.CANCELLED_REQUESTER);
    favorStatus = FavorStatus.CANCELLED_REQUESTER;
    updateViewFromStatus(getView());
    // Show confirmation and minimize keyboard
    showSnackbar(getString(R.string.favor_cancel_success_msg));

    // DB call to update status
    FavorUtil.getSingleInstance().updateFavor(currentFavor);
  }

  /** Updates status text and button visibility on favor status changes. */
  private void updateViewFromStatus(View view) {
    mStatusView.setText(favorStatus.toString());
    switch (favorStatus) {
      case REQUESTED:
        {
          editFavorBtn.setText(R.string.edit_favor);
          mStatusView.setBackgroundColor(getResources().getColor(R.color.requested_status_bg));
          updateViewFromParameters(view, false, true, false, true, true);
          break;
        }
      case EDIT:
        {
          mStatusView.setBackgroundColor(getResources().getColor(R.color.edit_status_bg));
          editFavorBtn.setText(R.string.confirm_favor_edit);
          updateViewFromParameters(view, true, false, true, true, true);
          break;
        }
      case ACCEPTED:
        {
          mStatusView.setBackgroundColor(getResources().getColor(R.color.accepted_status_bg));
          updateViewFromParameters(view, false, true, false, false, true);
          break;
        }
      case SUCCESSFULLY_COMPLETED:
        {
          updateViewFromParameters(view, false, true, false, false, false);
          mStatusView.setBackgroundColor(getResources().getColor(R.color.completed_status_bg));
          break;
        }
      default: // cancelled
        {
          mStatusView.setBackgroundColor(getResources().getColor(R.color.cancelled_status_bg));
          editFavorBtn.setText(R.string.restart_request);
          updateViewFromParameters(view, false, true, false, true, false);
        }
    }
  }

  private void updateViewFromParameters(
      View view,
      boolean enableImageButtons,
      boolean hideKeyboard,
      boolean textEditable,
      boolean editButtonEnabled,
      boolean cancelButtonEnabled) {
    enableUploadImageButtons(enableImageButtons);
    toggleTextViewsEditable(textEditable);
    editFavorBtn.setEnabled(editButtonEnabled);
    cancelFavorBtn.setEnabled(cancelButtonEnabled);
    if (hideKeyboard) {
      hideSoftKeyboard(requireActivity());
    }
  }

  private void enableUploadImageButtons(boolean setEnabled) {
    addPictureFromFilesBtn.setEnabled(setEnabled);
    if (isCameraAvailable()) {
      addPictureFromCameraBtn.setEnabled(setEnabled);
    }
  }

  /** Extracts favor data from and assigns it to currentFavor. */
  private void getFavorFromView(FavorStatus status) {

    // Extract details and post favor to Firebase
    EditText titleElem = Objects.requireNonNull(getView()).findViewById(R.id.title_request_view);
    EditText descElem = Objects.requireNonNull(getView()).findViewById(R.id.details);

    String userId = DependencyFactory.getCurrentFirebaseUser().getUid();
    String title = titleElem.getText().toString();
    String desc = descElem.getText().toString();
    FavoLocation loc = new FavoLocation(mGpsTracker.getLocation());
    status = FavorStatus.convertTemporaryStatus(status);

    Favor favor = new Favor(title, desc, userId, loc, status);

    // Upload picture to database if it exists
    if (mImageView.getDrawable() != null) {
      Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

      // Good idea to display the result of uploading the picture (not sure how to do this)
      CompletableFuture<String> pictureUrl = PictureUtil.uploadPicture(bais);
      pictureUrl.thenAccept(url -> FavorUtil.getSingleInstance().updateFavorPhoto(favor, url));
      pictureUrl.exceptionally(e -> {
        // insert something about being unable to upload picture
        return null;
      });
    }

    // Updates the current favor
    if (currentFavor == null) {
      currentFavor = favor;
    } else {
      currentFavor.updateToOther(favor);
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
  public void takePicture() {
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
      showSnackbar(getString(R.string.error_msg_image_request_view));
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

  /**
   * Will display error message in the form of a snack bar. This method is wrapped for unit tests
   *
   * @param errorMessageRes error message.
   */
  public void showSnackbar(String errorMessageRes) {
    Snackbar.make(requireView(), errorMessageRes, Snackbar.LENGTH_LONG).show();
  }

  /**
   * Saves the key listener for the edit text items. ensures keyboard hides when user clicks outside
   * of edit texts.
   *
   * @param view corresponds to root view created during onCreate
   */
  @SuppressLint("ClickableViewAccessibility")
  private void setupView(View view) {
    if (mTitleView.getKeyListener() != null) {
      mTitleView.setTag(mTitleView.getKeyListener());
    }
    if (mDescriptionView.getKeyListener() != null) {
      mDescriptionView.setTag(mDescriptionView.getKeyListener());
    }
    // ensure click on view will hide keyboard
    view.findViewById(R.id.constraint_layout_req_view)
        .setOnTouchListener(
            (v, event) -> {
              hideSoftKeyboard(requireActivity());
              return false;
            });
  }
}
