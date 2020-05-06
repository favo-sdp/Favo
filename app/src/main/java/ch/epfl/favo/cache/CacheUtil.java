package ch.epfl.favo.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.FileNotFoundException;
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
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(key, value);
    editor.apply();
  }

  /**
   * Store the given {key, value} pair to the default shared preference of the given context.
   * @param context: main activity
   * @param key: String, key for lookup
   * @param value: Boolean, value to store
   */
  public void storeKeyValueBool(Context context, String key, String value) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(key, value);
    editor.apply();
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

  public boolean pictureDownloaded(String pathToFolder) {
    File file = new File(pathToFolder);
    return file.exists();
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
  public void saveToInternalStorage(
          Context context, Bitmap bitmapImage, String favorId, int imageNum) {

    /* Remark:
     * The application always has permission to read and write in its internal storage directory.
     * Thus we won't need to check/request permission here. */

    String baseDir = context.getFilesDir().getAbsolutePath();
    SaveToStorageParams saveToStorageParams = new SaveToStorageParams(
            baseDir, favorId, Integer.toString(imageNum), bitmapImage);
    SaveToStorageTask saveToStorageTask = new SaveToStorageTask();
    saveToStorageTask.execute(saveToStorageParams);
  }


  @RequiresApi(api = Build.VERSION_CODES.N)
  public CompletableFuture<Bitmap> loadFromInternalStorage(String pathToFolder, int picNum){
    return CompletableFuture.supplyAsync(() -> {
      Bitmap result = decodeFile(pathToFolder + String.format("%s.jpeg", picNum));
      if (result == null) {
        Log.e(TAG, "Failed to load bitmap from internal storage");
      }
      return result;
    });
  }


  /**
   * Parameter class for SaveToStorageParamsTask
   */
  public static class SaveToStorageParams {
    String baseDir;
    String favorId;
    String imageNum;
    Bitmap bitmap;

    public SaveToStorageParams(String baseDir, String favorId, String imageNum, Bitmap bitmap) {
      this.baseDir = baseDir;
      this.favorId = favorId;
      this.imageNum = imageNum;
      this.bitmap = bitmap;
    }
  }

  /**
   * Store image to internal storage asynchronously
   */
  public static class SaveToStorageTask extends AsyncTask<SaveToStorageParams, Void, Boolean> {

    @Override
    protected Boolean doInBackground(SaveToStorageParams... params) {
      String baseDir = params[0].baseDir;
      String favorId = params[0].favorId;
      String imageNum = params[0].imageNum;
      Bitmap bitmap = params[0].bitmap;

      // If image folder for the current favor does not exist, create the directory.
      File favorDir = new File(baseDir, favorId);
      if (!favorDir.isDirectory()) {
        if (!favorDir.mkdir()) {
          Log.e(TAG, "Error: Creating new directory to store images failed.");
          return false;
        }
      }

      // Create the image file and write the bitmap to file.
      File image = new File(favorDir.getAbsolutePath(), String.format("%s.jpeg", imageNum));
      FileOutputStream fos = null;
      try {
        fos = new FileOutputStream(image);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
      } catch (FileNotFoundException e) {
        Log.e(TAG, "Error: Cannot save image to internal storage.");
        e.printStackTrace();
        return false;
      } finally {
        if (fos != null) {
          try {
            fos.close();
          } catch (IOException e) {
            Log.e(TAG, "Error: Failed to close FileOutputStream");
          }
        }
      }
      Log.d(TAG, "Successfully saved picture " + image.getAbsolutePath());
      return true;
    }
  }
}
