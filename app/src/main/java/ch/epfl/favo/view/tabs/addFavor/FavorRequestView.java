package ch.epfl.favo.view.tabs.addFavor;


import android.content.Intent;
import android.net.Uri;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.map.Locator;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.view.ViewController;

import static android.app.Activity.RESULT_OK;


public class FavorRequestView extends Fragment {
  private static final int PICK_IMAGE_REQUEST = 1;
  private Button confirmFavorBtn;
  private Button addPictureBtn;
  private ImageView mImageView;
  private Uri mImageUri; //path of image

  private Locator mGpsTracker;
  // TODO: Rename and change types of parameters

  public FavorRequestView() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    setupView();
    View rootView = inflater.inflate(R.layout.fragment_favor, container, false);

    confirmFavorBtn = rootView.findViewById(R.id.request_button);
    addPictureBtn = rootView.findViewById(R.id.add_picture_button);
    mImageView = rootView.findViewById(R.id.imageView);

    confirmFavorBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          requestFavor();
        }
    });

    addPictureBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openFileChooser();
        }
    });

    mGpsTracker = DependencyFactory.getCurrentGpsTracker(getActivity().getApplicationContext());

    return rootView;
  }


  public void openFileChooser(){
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(intent,PICK_IMAGE_REQUEST);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == PICK_IMAGE_REQUEST
        && resultCode == RESULT_OK
        && data != null
        && data.getData() != null) {
      mImageUri = data.getData();
      mImageView.setImageURI(mImageUri);
    } else {
      showSnackbar("Try again!");
    }
    }


  private void requestFavor() {
    showSnackbar(getString(R.string.favor_respond_success_msg));
    EditText titleElem = Objects.requireNonNull(getView()).findViewById(R.id.title);
    EditText descElem = Objects.requireNonNull(getView()).findViewById(R.id.desc);
    String title = titleElem.getText().toString();
    String desc = descElem.getText().toString();
    Location loc = mGpsTracker.getLocation();

    Favor favor = new Favor(title, desc, null, loc, 0);
    FavorUtil.getSingleInstance().postFavor(favor);
  }

  // Todo: Implement the following functions to verify user input.

  // Todo: Try to put this method in a util package and import it here.
  public void showSnackbar( String errorMessageRes) {
    Snackbar.make(
            requireView().findViewById(R.id.fragment_favor), errorMessageRes, Snackbar.LENGTH_LONG)
        .show();
  }


    private void setupView(){
        ((ViewController) getActivity()).setupViewBotDestTab();
    }
}
