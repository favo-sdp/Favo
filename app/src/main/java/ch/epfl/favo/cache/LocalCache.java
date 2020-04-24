package ch.epfl.favo.cache;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class LocalCache {

  private LocalCache() {}

  public static void storeKeyValue(Context context, String key, String value) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(key, value);
    editor.apply();
  }

  public static String getValueFromCacheStr(Context context, String key) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    return preferences.getString(key, "");
  }

  public static Boolean getValueFromCacheBool(Context context, String key) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    return preferences.getBoolean(key, false);
  }
}
