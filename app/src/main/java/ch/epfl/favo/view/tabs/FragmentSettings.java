package ch.epfl.favo.view.tabs;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import ch.epfl.favo.R;
import ch.epfl.favo.view.ViewController;

public class FragmentSettings extends PreferenceFragmentCompat {

  private void setupView() {
    ((ViewController) getActivity()).showBackIcon();
    ((ViewController) getActivity()).hideBottomTabs();
  }

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setupView();
    setPreferencesFromResource(R.xml.root_preferences, rootKey);
  }
}
