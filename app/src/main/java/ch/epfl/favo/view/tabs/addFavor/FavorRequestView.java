package ch.epfl.favo.view.tabs.addFavor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.common.FavoLocation;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.map.Locator;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.FavorFragmentFactory;
import ch.epfl.favo.view.ViewController;

import static android.app.Activity.RESULT_OK;
import static ch.epfl.favo.util.CommonTools.hideKeyboardFrom;

public class FavorRequestView extends Fragment {

  public static final int PICK_IMAGE_REQUEST = 1;
  public static final int USE_CAMERA_REQUEST = 2;
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

  private Favor currentFavor;

  public FavorRequestView() {
    // Required empty public constructor
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_favor, container, false);
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
    mGpsTracker =
        DependencyFactory.getCurrentGpsTracker(
            Objects.requireNonNull(getActivity()).getApplicationContext());
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
    mTitleView.setText(currentFavor.getTitle());
    mDescriptionView.setText(currentFavor.getDescription());
    mStatusView.setText(currentFavor.getStatusId().toString());
    updateViewFromStatus(v);
  }

  /**
   * Identifes buttons and sets onclick listeners.
   *
   * @param rootView
   */
  @RequiresApi(api = Build.VERSION_CODES.M)
  private void setupButtons(View rootView) {

    // Button: Request Favor
    confirmFavorBtn = rootView.findViewById(R.id.request_button);
    confirmFavorBtn.setOnClickListener(v -> requestFavor());

    if (DependencyFactory.isOfflineMode(Objects.requireNonNull(getContext()))) {
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
          if (currentFavor.getStatusId() == Favor.Status.EDIT) {
            confirmUpdatedFavor();
          } else { // text is currently "Edit Request"
            startUpdatingFavor();
          }
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
  @RequiresApi(api = Build.VERSION_CODES.M)
  private void requestFavor() {
    // update currentFavor
    getFavorFromView(Favor.Status.REQUESTED);
    // post to DB
    FavorUtil.getSingleInstance().postFavor(currentFavor);
    // Save the favor to local favorList
    updateMainActivityLists(true);

    // Show confirmation and minimize keyboard
    if (DependencyFactory.isOfflineMode(Objects.requireNonNull(getContext()))) {
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
    toggleTextViewsEditable(false);
    updateViewFromStatus(v);
  }

  /** When edit button is clicked */
  private void startUpdatingFavor() {
    currentFavor.setStatusId(Favor.Status.EDIT);
    updateViewFromStatus(getView());
  }

  /** Gets called once favor has been updated on view. */
  private void confirmUpdatedFavor() {
    getFavorFromView(Favor.Status.REQUESTED);
    // update lists
    updateMainActivityLists(true);
    updateViewFromStatus(getView());
    showSnackbar(getString(R.string.favor_edit_success_msg));

    // DB call to update Favor details
    FavorUtil.getSingleInstance().postFavor(currentFavor);
  }

  /** Updates favor on DB. Updates maps on main activity hides keyboard shows snackbar */
  private void cancelFavor() {
    currentFavor.setStatusId(Favor.Status.CANCELLED_REQUESTER);
    updateMainActivityLists(false);
    updateViewFromStatus(getView());
    // Show confirmation and minimize keyboard
    showSnackbar(getString(R.string.favor_cancel_success_msg));

    // DB call to update status
    Map<String, Object> statusUpdates = new HashMap<>();
    statusUpdates.put("statusId", Favor.Status.CANCELLED_REQUESTER);
    FavorUtil.getSingleInstance().updateFavor(currentFavor.getId(), statusUpdates);
  }

  private void updateMainActivityLists(boolean favorIsActive) {
    MainActivity mainActivity = Objects.requireNonNull((MainActivity) getActivity());
    if (favorIsActive) {
      mainActivity.activeFavors.put(currentFavor.getId(), currentFavor);
      mainActivity.archivedFavors.remove(currentFavor.getId());

    } else {
      mainActivity.archivedFavors.put(currentFavor.getId(), currentFavor);
      mainActivity.activeFavors.remove(currentFavor.getId());
    }
  }

  /** Updates status text and button visibility on favor status changes. */
  private void updateViewFromStatus(View view) {
    mStatusView.setText(currentFavor.getStatusId().getPrettyString());
    switch (currentFavor.getStatusId()) {
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
          editFavorBtn.setText(R.string.edit_favor);
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
      hideKeyboardFrom(getContext(), view);
    }
  }

  private void enableUploadImageButtons(boolean setEnabled) {
    addPictureFromFilesBtn.setEnabled(setEnabled);
    if (isCameraAvailable()) {
      addPictureFromCameraBtn.setEnabled(setEnabled);
    }
  }

  /** Extracts favor data from and assigns it to currentFavor. */
  private Favor getFavorFromView(Favor.Status status) {

    // Extract details and post favor to Firebase
    EditText titleElem = Objects.requireNonNull(getView()).findViewById(R.id.title_request_view);
    EditText descElem = Objects.requireNonNull(getView()).findViewById(R.id.details);
    String title = titleElem.getText().toString();
    String desc = descElem.getText().toString();
    Favor.Status FavorStatus = (status == null) ? Favor.Status.REQUESTED : status;
    FavoLocation loc = new FavoLocation(mGpsTracker.getLocation());
    Favor favor = new Favor(title, desc, UserUtil.currentUserId, loc, FavorStatus);
    if (currentFavor == null) {
      currentFavor = favor;
    } else {
      currentFavor.updateToOther(favor);
    }
    return currentFavor;
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
  @RequiresApi(api = Build.VERSION_CODES.M)
  public void takePicture() {
    if (ContextCompat.checkSelfPermission(
            Objects.requireNonNull(getContext()), Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {
      Objects.requireNonNull(getActivity())
          .requestPermissions(new String[] {Manifest.permission.CAMERA}, USE_CAMERA_REQUEST);
    } else {
      Intent takePictureIntent = DependencyFactory.getCurrentCameraIntent();

      if (takePictureIntent.resolveActivity(
              Objects.requireNonNull(getActivity()).getPackageManager())
          != null) {
        startActivityForResult(takePictureIntent, USE_CAMERA_REQUEST);
      }
    }
  }

  private boolean isCameraAvailable() {
    boolean hasCamera =
        Objects.requireNonNull(getActivity())
            .getPackageManager()
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
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
    Snackbar.make(Objects.requireNonNull(getView()), errorMessageRes, Snackbar.LENGTH_LONG).show();
  }

  /**
   * Saves the key listener for the edit text items. ensures keyboard hides when user clicks outside
   * of edit texts.
   *
   * @param view corresponds to root view created during onCreate
   */
  @SuppressLint("ClickableViewAccessibility")
  private void setupView(View view) {
    ((ViewController) Objects.requireNonNull(getActivity())).setupViewBotDestTab();
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
              hideKeyboardFrom(Objects.requireNonNull(getContext()), v);
              return false;
            });
  }
}
