package ch.epfl.favo.view.tabs;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import ch.epfl.favo.R;

public class FragmentSettings extends PreferenceFragmentCompat {

  private static final String TAG = "FragmentSettings";

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.root_preferences, rootKey);
  }

  @Override
  public void onStart() {
    super.onStart();
    // todo: use this to load preferences if necessary
    //    LocalCache.storeKeyValue(getContext(), "key1", "value1");
    //    Log.d(TAG, "onCreatePreferences: stored key1 value1 ");
    //    String val = LocalCache.getValueFromCacheStr(getContext(), "radius");
    //    Log.d(TAG, String.format("onCreatePreferences: retrieved %s", val));
  }
}
