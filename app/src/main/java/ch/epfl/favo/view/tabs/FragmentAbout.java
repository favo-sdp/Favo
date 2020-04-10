package ch.epfl.favo.view.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.Objects;

import ch.epfl.favo.R;
import ch.epfl.favo.view.ViewController;

/** A simple {@link Fragment} subclass. */
public class FragmentAbout extends Fragment {

  public FragmentAbout() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    setupView();
    return inflater.inflate(R.layout.fragment_about, container, false);
  }

  private void setupView() {
    ((ViewController) Objects.requireNonNull(getActivity())).setupViewBotDestTab();
  }
}
