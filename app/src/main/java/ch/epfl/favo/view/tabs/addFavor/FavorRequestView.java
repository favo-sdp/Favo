package ch.epfl.favo.view.tabs.addFavor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import ch.epfl.favo.R;
import ch.epfl.favo.view.ViewController;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass. Use the {@link FavorRequestView#newInstance} factory method
 * to create an instance of this fragment.
 */
public class FavorRequestView extends Fragment {
  private static final int PICK_IMAGE_REQUEST = 1;
  private Button confirmFavorBtn;
  private Button addPictureBtn;
  private ImageView mImageView;
  private Uri mImageUri; //path of image
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
            showSnackbar(getString(R.string.favor_request_success_msg));
        }
    });

    addPictureBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openFileChooser();
        }
    });

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

    if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
        && data != null && data.getData() != null){
      mImageUri = data.getData();
      mImageView.setImageURI(mImageUri);
    }
    else{
      showSnackbar("Try again!");
    }
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
