package ch.epfl.favo.view.tabs.addFavor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.favor.FavorUtil;
import ch.epfl.favo.view.ViewController;
import ch.epfl.favo.view.tabs.BottomDestinationTab;

/**
 * A simple {@link Fragment} subclass. Use the {@link FavorRequestView#newInstance} factory method
 * to create an instance of this fragment.
 */
public class FavorRequestView extends BottomDestinationTab implements View.OnClickListener {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;

  public FavorRequestView() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * <p>// * @param param1 Parameter 1. // * @param param2 Parameter 2.
   *
   * @return A new instance of fragment FavorRequestView.
   */
  // TODO: Rename and change types and number of parameters
  public static FavorRequestView newInstance(String param1, String param2) {
    return new FavorRequestView();
  }
  //        FavorRequestView fragment = new FavorRequestView();
  //        Bundle args = new Bundle();
  //        args.putString(ARG_PARAM1, param1);
  //        args.putString(ARG_PARAM2, param2);
  //        fragment.setArguments(args);
  //        return fragment;
  //    }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    setupView();
    View rootView = inflater.inflate(R.layout.fragment_favor, container, false);

    Button confirmFavorBtn = rootView.findViewById(R.id.add_button);
    confirmFavorBtn.setOnClickListener(this);

    return rootView;
  }

  @Override
  public void onClick(View view) {
    // The following inspection warning is suppressed. More cases will be added soon.
    // noinspection SwitchStatementWithTooFewBranches
    switch (view.getId()) {
      case R.id.add_button:
        showSnackbar(getString(R.string.favor_success_msg));
        EditText element = Objects.requireNonNull(getView()).findViewById(R.id.title);
        String title = element.getText().toString();
        Favor favor = new Favor("", title, "some desc", "", null, 0);
        FavorUtil.getSingleInstance().postFavor(favor);
    }
  }

  // Todo: Implement the following functions to verify user input.

  // Todo: Try to put this method in a util package and import it here.
  private void showSnackbar(String errorMessageRes) {
    Snackbar.make(
            requireView().findViewById(R.id.fragment_favor), errorMessageRes, Snackbar.LENGTH_LONG)
        .show();
  }
}
