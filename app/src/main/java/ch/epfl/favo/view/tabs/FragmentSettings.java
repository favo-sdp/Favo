package ch.epfl.favo.view.tabs;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import ch.epfl.favo.R;

public class FragmentSettings extends PreferenceFragmentCompat {

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.root_preferences, rootKey);
  }
}
