package ch.epfl.favo.view.tabs;

import android.os.Bundle;
import android.util.Log;

import androidx.preference.PreferenceFragmentCompat;

import ch.epfl.favo.R;
import ch.epfl.favo.cache.LocalCache;

public class FragmentSettings extends PreferenceFragmentCompat {

  private static final String TAG = "FragmentSettings";

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.root_preferences, rootKey);
  }

  @Override
  public void onStart() {
    super.onStart();
    LocalCache.storeKeyValue(getContext(), "key1", "value1");
    Log.d(TAG, "onCreatePreferences: stored key1 value1 ");
    String val = LocalCache.getValueFromCacheStr(getContext(), "radius");
    Log.d(TAG, String.format("onCreatePreferences: retrieved %s", val));
  }
}
