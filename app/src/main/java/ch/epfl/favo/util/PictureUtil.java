package ch.epfl.favo.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.database.DatabaseWrapper;

import static android.content.Context.MODE_PRIVATE;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

@SuppressLint("NewApi")
public class PictureUtil {

  private static PictureUtil INSTANCE = null;
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


  public static String saveToInternalStorage(Bitmap bitmapImage, String favorId, String picNum) {
    @SuppressLint("RestrictedApi") ContextWrapper cw = new ContextWrapper(getApplicationContext());
    File directory = new File(cw.getFilesDir(), favorId);
    File image = new File(directory.getAbsolutePath(), "0.jpeg");
    try {
      directory.mkdir();
      FileOutputStream outputStream = null;
      outputStream = new FileOutputStream(image);
      bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
      int x = 1;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return directory.getAbsolutePath();
  }


  public static Bitmap loadFromInternalStorage(String pathToFolder, String picNum) {
    try {
//      File image = new File(pathToFolder, String.format("%s.jpeg", picNum));
      Bitmap temp = BitmapFactory.decodeFile(pathToFolder+String.format("/%s.jpeg", picNum));
//      Bitmap temp = BitmapFactory.decodeStream(new FileInputStream(image));
      return temp;
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
    } catch (Exception e) {

    }
    return null;
  }
}
