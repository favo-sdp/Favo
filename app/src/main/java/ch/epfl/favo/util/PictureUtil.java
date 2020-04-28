package ch.epfl.favo.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
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


  public static boolean saveToInternalStorage(
          Context context, Bitmap bitmapImage, String favorId, int picNum) {

    /* Remark:
     * The application always has permission to read and write in its internal storage directory.
     * Thus we won't need to check/request permission here. */

    // Favor-specific image directory: "/data/user/0/ch.epfl.favo/files/<Favor_id>"
    File directory = new File(context.getFilesDir(), favorId);

    // If directory does not exist, create the directory.
    if (!directory.isDirectory()) {
      if (!directory.mkdir())
        Log.e(TAG, "Error: Creating new directory to store images failed.");
    }

    // Create the image file and write the bitmap to file.
    File image = new File(directory.getAbsolutePath(), String.format("%s.jpeg", picNum));
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(image);
      bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
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


  public static Bitmap loadFromInternalStorage(String pathToFolder, int picNum) {
    Bitmap result = decodeFile(pathToFolder + String.format("/%s.jpeg", picNum));
    if (result == null) {
      Log.e(TAG, "Failed to load bitmap from internal storage");
    }
    return result;
  }
}
