package ch.epfl.favo.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.database.DatabaseWrapper;

import static android.graphics.BitmapFactory.decodeFile;

@SuppressLint("NewApi")
public class PictureUtil {

  private static PictureUtil INSTANCE = null;
  private static final String TAG = "PictureUtil";

  private static final String PICTURE_FILE_EXTENSION = ".jpeg";
  private static final long TEN_MEGABYTES = 10 * 1024 * 1024;
  private final FirebaseStorage storage;

  private PictureUtil() {
    storage = DependencyFactory.getCurrentFirebaseStorage();
  }

  public static PictureUtil getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new PictureUtil();
    }
    return INSTANCE;
  }

  private static FirebaseStorage getStorage() {
    return getInstance().storage;
  }

  /**
   * Uploads given picture to Firebase Cloud Storage and returns URI of where it was placed
   *
   * @param picture to be uploaded to Firebase Cloud Storage
   * @return CompletableFuture of the resulting url
   */
  public CompletableFuture<String> uploadPicture(Bitmap picture) {
    InputStream is = BitmapConversionUtil.bitmapToJpegInputStream(picture);
    StorageReference storageRef =
        getStorage()
            .getReference()
            .child(DatabaseWrapper.generateRandomId() + PICTURE_FILE_EXTENSION);

    Task<Uri> urlTask =
        storageRef
            .putStream(is)
            .continueWithTask(
                task -> {
                  if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                  }
                  return storageRef.getDownloadUrl();
                });

    CompletableFuture<Uri> urlFuture = new TaskToFutureAdapter<>(urlTask);
    return urlFuture.thenApply(Uri::toString);
  }

  public CompletableFuture deletePicture(@NonNull String imagePath) {
    String pictureId = getPictureIdFromPath(imagePath);
    Task<Void> deleteTask = getStorage().getReference().child(pictureId).delete();
    return new TaskToFutureAdapter<>(deleteTask).getInstance();
  }

  private String getPictureIdFromPath(String path) {
    // Example:
    // https://firebasestorage.googleapis.com/v0/b/favo-11728.appspot.com/o/V6Y8F6DOR3NKW71UEQKULUPXMQC0.jpeg?alt=media&token=f88ee85f-a201-435f-88cd-4b5803df9656
    String id = path.split("/o/")[1].split("\\?")[0];
    return id;
  }

  /**
   * Downloads a picture from a given url as a Bitmap
   *
   * @param pictureUrl url of the picture
   * @return CompletableFuture of the picture represented as a Bitmap
   */
  public CompletableFuture<Bitmap> downloadPicture(String pictureUrl) {
    Task<byte[]> downloadTask =
        getStorage().getReferenceFromUrl(pictureUrl).getBytes(TEN_MEGABYTES);
    CompletableFuture<byte[]> downloadFuture =
        new TaskToFutureAdapter<>(downloadTask).getInstance();
    return downloadFuture.thenApply(BitmapConversionUtil::byteArrayToBitmap);
  }

  /**
   * Save given picture to Internal Storage and returns Boolean to indicate if it is a success.
   * @param context: getActivity()
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


  public static Bitmap loadFromInternalStorage(String pathToFolder, int picNum) {
    Bitmap result = decodeFile(pathToFolder + String.format("/%s.jpeg", picNum));
    if (result == null)
      Log.e(TAG, "Failed to load bitmap from internal storage");
    return result;
  }

  /**
   * Parameter class for SaveToStorageParamsTask
   */
  private static class SaveToStorageParams {
    String baseDir;
    String favorId;
    String imageNum;
    Bitmap bitmap;

    SaveToStorageParams(String baseDir, String favorId, String imageNum, Bitmap bitmap) {
      this.baseDir = baseDir;
      this.favorId = favorId;
      this.imageNum = imageNum;
      this.bitmap = bitmap;
    }
  }

  /**
   * Store image to internal storage asynchronously
   */
  private static class SaveToStorageTask extends AsyncTask<SaveToStorageParams, Void, Boolean> {

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
      return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
      if (!result) {
        Log.e(TAG, "Error: Failed to save to internal storage. Please investigate");
      }
    }
  }
}
