package ch.epfl.favo.util;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.util.concurrent.CompletableFuture;

/** This class allows one to communicate with the remote storage to upload and download images */
public interface IPictureUtil {
  /**
   * Uploads picture to storage.
   *
   * @param picture picture to be uploaded
   * @return a future with the string url
   */
  CompletableFuture<String> uploadPicture(Bitmap picture);

  /**
   * Deletes a picture from storage
   *
   * @param imagePath url of the image
   * @return a future that can be successful or failed
   */
  CompletableFuture<Void> deletePicture(@NonNull String imagePath);

  /**
   * Download a bitmap object from storage
   *
   * @param pictureUrl url of the image
   * @return picture bitmap
   */
  CompletableFuture<Bitmap> downloadPicture(String pictureUrl);
}
