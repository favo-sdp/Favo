package ch.epfl.favo.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static android.graphics.BitmapFactory.decodeFile;

public class CacheUtil {

  private static CacheUtil INSTANCE = null;
  private static final String TAG = "LocalCacheUtil";

  private CacheUtil() {}

  public static CacheUtil getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new CacheUtil();
    }
    return INSTANCE;
  }

  /**
   * Store the given {key, value} pair to the default shared preference of the given context.
   * @param context: main activity
   * @param key: String, key for lookup
   * @param value: String, value to store
   */
  public void storeKeyValueStr(Context context, String key, String value) {
    PreferenceManager
            .getDefaultSharedPreferences(context).edit()
            .putString(key, value).apply();
  }

  /**
   * Store the given {key, value} pair to the default shared preference of the given context.
   * @param context: main activity
   * @param key: String, key for lookup
   * @param value: Boolean, value to store
   */
  public void storeKeyValueBool(Context context, String key, Boolean value) {
    PreferenceManager
            .getDefaultSharedPreferences(context).edit()
            .putBoolean(key, value).apply();
  }

  /**
   * Retrieve the value (String) associated with the given key from the default shared preference
   * @param context: main activity
   * @param key: String, key for lookup
   * @return value (String) associated with the given key
   */
  public String getValueFromCacheStr(Context context, String key) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    return preferences.getString(key, "");
  }

  /**
   * Retrieve the value (Boolean) associated with the given key from the default shared preference
   * @param context: main activity
   * @param key: String, key for lookup
   * @return value (Boolean) associated with the given key
   */
  public Boolean getValueFromCacheBool(Context context, String key) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    return preferences.getBoolean(key, false);
  }


  /**
   * Save given picture to Internal Storage and returns Boolean to indicate if it is a success.
   * @param context: main activity
   * @param bitmapImage: picture ot be stored
   * @param favorId: id of the favor associated with the image
   *              - Favor-specific image directory: "/data/user/0/ch.epfl.favo/files/<Favor_id>"
   * @param imageNum: the index of the image, set to 0 for now (TODO: support multiple pitures.)
   * @return Boolean to indicate whether the write was a success
   */
  @RequiresApi(api = Build.VERSION_CODES.N)
  public Uri saveToInternalStorage(
          Context context, Bitmap bitmapImage, String favorId, int imageNum) {

    /* Remark:
     * The application always has permission to read and write in its internal storage directory.
     * Thus we won't need to check/request permission here. */

    String baseDir = context.getFilesDir().getAbsolutePath();

    File favorDir = new File(baseDir, favorId);
    if (!favorDir.isDirectory()) {
      favorDir.mkdir();
    }

    File image = new File(favorDir.getAbsolutePath(), String.format("%s.jpeg", imageNum));
    try (FileOutputStream fos = new FileOutputStream(image)) {
      bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
    } catch (IOException ignore) {
      return null;
    }

    return Uri.parse(image.getAbsolutePath());
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  public CompletableFuture<Bitmap> loadFromInternalStorage(String pathToFolder, int picNum){
    return CompletableFuture.supplyAsync(() -> decodeFile(pathToFolder + String.format("%s.jpeg", picNum)));
  }
}
