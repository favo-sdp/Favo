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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

import ch.epfl.favo.R;
import ch.epfl.favo.exception.IllegalRequestException;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorStatus;
import ch.epfl.favo.gps.FavoLocation;
import ch.epfl.favo.gps.IGpsTracker;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.NonClickableToolbar;
import ch.epfl.favo.viewmodel.IFavorViewModel;

import static android.app.Activity.RESULT_OK;
import static androidx.navigation.Navigation.findNavController;
import static ch.epfl.favo.util.CommonTools.hideSoftKeyboard;

@SuppressLint("NewApi")
public class FavorRequestView extends Fragment {
  private static String TAG = "FavorRequestView";

  private static final int PICK_IMAGE_REQUEST = 1;
  private static final int USE_CAMERA_REQUEST = 2;

  private IFavorViewModel favorViewModel;

  private FavorStatus favorStatus;
  private ImageView mImageView;
  private EditText mTitleView;
  private EditText mDescriptionView;
  private IGpsTracker mGpsTracker;
  private Button confirmFavorBtn;
  private Button addPictureFromFilesBtn;
  private Button addPictureFromCameraBtn;
  private Button locationAccessBtn;
  private NonClickableToolbar toolbar;
  private Favor currentFavor;

  public FavorRequestView() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_favor_request_view, container, false);
    setupButtons(rootView);
    favorStatus = FavorStatus.EDIT;

    // Edit text:
    mTitleView = rootView.findViewById(R.id.title_request_view);
    mTitleView.requestFocus();
    mDescriptionView = rootView.findViewById(R.id.details);
    setupView(rootView);
    // Extract other elements
    mImageView = rootView.findViewById(R.id.image_view_request_view);

    // Get dependencies
    mGpsTracker = DependencyFactory.getCurrentGpsTracker(requireActivity().getApplicationContext());
    // Inject argument
    favorViewModel =
        (IFavorViewModel)
            new ViewModelProvider(requireActivity())
                .get(DependencyFactory.getCurrentViewModelClass());
    toolbar = requireActivity().findViewById(R.id.toolbar_main_activity);
    if (getArguments() != null) {
      currentFavor = (Favor) getArguments().get(CommonTools.FAVOR_ARGS);
      displayFavorInfo(rootView);
    }
    return rootView;
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  public IFavorViewModel getViewModel() {
    return favorViewModel;
  }


  /** When fragment is launched with favor. */
  private void displayFavorInfo(View v) {
    favorStatus = FavorStatus.toEnum(currentFavor.getStatusId());
    mTitleView.setText(currentFavor.getTitle());
    mDescriptionView.setText(currentFavor.getDescription());
    String url = currentFavor.getPictureUrl();
    if (url != null) {
      v.findViewById(R.id.loading_panel).setVisibility(View.VISIBLE);
      getViewModel()
          .downloadPicture(currentFavor)
          .thenAccept(
              picture -> {
                mImageView.setImageBitmap(picture);
                v.findViewById(R.id.loading_panel).setVisibility(View.GONE);
              });
    }
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

    // Button: Access location
    locationAccessBtn = rootView.findViewById(R.id.location_request_view_btn);
    locationAccessBtn.setOnClickListener(
        v -> {
          getFavorFromView();
          CommonTools.hideSoftKeyboard(requireActivity());
          favorViewModel.setShowObservedFavor(true);
          favorViewModel.setFavorValue(currentFavor);
          // signal the destination is map view
          findNavController(requireActivity(), R.id.nav_host_fragment)
              .popBackStack(R.id.nav_map, false);
        });
  }

  /**
   * Method is called when fragment_favor_detail favor button is clicked. It uploads favor
   * fragment_favor_detail to the database and updates view so that favor is editable.
   */
  private void requestFavor() {
    // update currentFavor
    View currentView = getView();
    favorStatus = FavorStatus.REQUESTED;
    getFavorFromView();
    // post to DB
    CompletableFuture postFavorFuture = getViewModel().requestFavor(currentFavor);
    postFavorFuture.thenAccept(
        o -> {
          CommonTools.showSnackbar(currentView, getString(R.string.favor_request_success_msg));

          // update user info
          UserUtil.getSingleInstance()
              .findUser(DependencyFactory.getCurrentFirebaseUser().getUid())
              .thenAccept(
                  user -> {
                    user.setRequestedFavors(user.getRequestedFavors() + 1);
                    UserUtil.getSingleInstance().updateUser(user);
                  });
        });
    postFavorFuture.exceptionally(onFailedResult(currentView));
    // Show confirmation and minimize keyboard
    if (DependencyFactory.isOfflineMode(requireContext())) {
      showSnackbar(getString(R.string.save_draft_message));
    }
    CommonTools.hideSoftKeyboard(requireActivity());
  }

  private Function onFailedResult(View currentView) {
    return (exception) -> {
      if (((CompletionException) exception).getCause() instanceof IllegalRequestException)
        CommonTools.showSnackbar(currentView, getString(R.string.illegal_request_error));
      else CommonTools.showSnackbar(currentView, getString(R.string.update_favor_error));
      Log.e(TAG, Objects.requireNonNull(((Exception) exception).getMessage()));
      return null;
    };
  }

  /** Extracts favor data from and assigns it to currentFavor. */
  private void getFavorFromView() {

    // Extract details and post favor to Firebase
    EditText titleElem = requireView().findViewById(R.id.title_request_view);
    EditText descElem = requireView().findViewById(R.id.details);

    String userId = DependencyFactory.getCurrentFirebaseUser().getUid();
    String title = titleElem.getText().toString();
    String desc = descElem.getText().toString();
    FavoLocation loc = new FavoLocation(mGpsTracker.getLocation());

    // if this is not a new favor draft, then do not override the favor position with current user
    // location.
    if (currentFavor != null) {
      loc.setLongitude(currentFavor.getLocation().getLongitude());
      loc.setLatitude(currentFavor.getLocation().getLatitude());
    }
    Favor favor = new Favor(title, desc, userId, loc, favorStatus);

    // Upload picture to database if it exists //TODO: extract to FavorViewModel and implement
    // callbacks in requestFavor and confirm
    if (mImageView.getDrawable() != null) {
      Bitmap picture = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
      getViewModel().uploadOrUpdatePicture(favor, picture);
    } else {
      favor.setPictureUrl(null);
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
  private void showSnackbar(String errorMessageRes) {
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
