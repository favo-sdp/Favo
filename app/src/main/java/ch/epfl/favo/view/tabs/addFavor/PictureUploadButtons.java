package ch.epfl.favo.view.tabs.addFavor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.view.View;
import android.widget.ImageButton;

import androidx.core.content.ContextCompat;

import ch.epfl.favo.R;
import ch.epfl.favo.util.DependencyFactory;

@SuppressLint("NewApi")
public class PictureUploadButtons {

  private static final int PICK_IMAGE_REQUEST = 1;
  private static final int USE_CAMERA_REQUEST = 2;

  private static final PictureUploadButtons INSTANCE = new PictureUploadButtons();

  public static PictureUploadButtons getInstance() { return INSTANCE; }

  /** Identifes buttons and sets onclick listeners. */
  public void setupButtons(Context context, Activity activity, View root) {

    // Button: Add Image from files
    ImageButton addPictureFromFilesBtn = root.findViewById(R.id.add_picture_button);
    addPictureFromFilesBtn.setOnClickListener(new onButtonClick(context, activity));

    // Button: Add picture from camera
    ImageButton addPictureFromCameraBtn = root.findViewById(R.id.add_camera_picture_button);
    addPictureFromCameraBtn.setOnClickListener(new onButtonClick(context, activity));
    if (!isCameraAvailable(activity)) { // if camera is not available
      addPictureFromCameraBtn.setEnabled(false);
    }
  }

  private boolean isCameraAvailable(Activity activity) {
    boolean hasCamera =
      activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    int numberOfCameras = Camera.getNumberOfCameras();
    return (hasCamera && numberOfCameras != 0);
  }

  class onButtonClick implements View.OnClickListener {

    Context context;
    Activity activity;

    onButtonClick(Context context, Activity activity) {
      this.context = context;
      this.activity = activity;
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.add_camera_picture_button:
          takePicture(context, activity);
          break;
        case R.id.add_picture_button:
          openFileChooser(activity);
          break;
      }
    }
  }

  /** Called when camera button is clicked Method calls camera intent. */
  private void takePicture(Context context, Activity activity) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
      != PackageManager.PERMISSION_GRANTED) {
      activity.requestPermissions(new String[] {Manifest.permission.CAMERA}, USE_CAMERA_REQUEST);
    } else {
      Intent takePictureIntent = DependencyFactory.getCurrentCameraIntent();

      if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
        activity.startActivityForResult(takePictureIntent, USE_CAMERA_REQUEST);
      }
    }
  }

  /**
   * Called when upload file from storage button is clicked. Method calls external fileChooser
   * intent.
   */
  private void openFileChooser(Activity activity) {
    Intent openFileChooserIntent = new Intent();
    openFileChooserIntent.setType("image/*");
    openFileChooserIntent.setAction(Intent.ACTION_GET_CONTENT);
    activity.startActivityForResult(openFileChooserIntent, PICK_IMAGE_REQUEST);
  }
}
