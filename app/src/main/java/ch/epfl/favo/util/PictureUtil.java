package ch.epfl.favo.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.common.DatabaseWrapper;

@SuppressLint("NewApi")
public class PictureUtil {

  private static final String PICTURE_FILE_EXTENSION = ".jpeg";
  private static final long TEN_MEGABYTES = 10 * 1024 * 1024;
  private static final FirebaseStorage storage = FirebaseStorage.getInstance();

  /**
   * Uploads given picture to Firebase Cloud Storage and returns URI of where it was placed
   *
   * @param picture to be uploaded to Firebase Cloud Storage
   * @return CompletableFuture of the resulting url
   */
  public static CompletableFuture<String> uploadPicture(Bitmap picture) {
    InputStream is = BitmapConversionUtil.bitmapToJpegInputStream(picture);
    StorageReference storageRef =
        storage.getReference().child(DatabaseWrapper.generateRandomId() + PICTURE_FILE_EXTENSION);

    Task<Uri> urlTask =
        storageRef
            .putStream(is)
            .continueWithTask(
                task -> {
                  if (!task.isSuccessful()) {
                    throw task.getException();
                  }
                  return storageRef.getDownloadUrl();
                });

    CompletableFuture<Uri> urlFuture = new TaskToFutureAdapter<>(urlTask);
    return urlFuture.thenApply(url -> url.toString());
  }

  /**
   * Downloads a picture from a given url as a Bitmap
   *
   * @param pictureUrl url of the picture
   * @return CompletableFuture of the picture represented as a Bitmap
   */
  public static CompletableFuture<Bitmap> downloadPicture(String pictureUrl) {
    Task<byte[]> downloadTask =
        storage.getReferenceFromUrl(pictureUrl).getBytes(TEN_MEGABYTES);

    CompletableFuture<byte[]> downloadFuture =
        new TaskToFutureAdapter<>(downloadTask).getInstance();
    return downloadFuture.thenApply(bytes -> BitmapConversionUtil.byteArrayToBitmap(bytes));
  }

}
