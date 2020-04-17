package ch.epfl.favo.cache;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class LocalCache {

  private LocalCache() {}

  private static final LocalCache SINGLE_INSTANCE = new LocalCache();

  public static LocalCache getSingleInstance() {
    return SINGLE_INSTANCE;
  }

  public static void storeKeyValue(Context context, String key, String value) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(key, value);
    editor.commit(); // use .apply() for async performance
  }

  public static String getValueFromCache(Context context, String key){
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    String name = preferences.getString(key, "");

    return name;
  }

}
