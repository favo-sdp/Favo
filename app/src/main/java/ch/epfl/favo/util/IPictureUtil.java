package ch.epfl.favo.util;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.util.concurrent.CompletableFuture;

/** This class allows one to communicate with the remote storage to upload and download images */
public interface IPictureUtil {

  /**
   * Uploads picture to storage
   *
   * @param folder could be favor chat or profile
   * @param picture bitmap holding picture
   * @return future with url
   */
  CompletableFuture<String> uploadPicture(Folder folder, Bitmap picture);

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

  String favorFolder = "favor/";
  String chatFolder = "chat/";
  String profilePictureFolder = "profile_picture/";

  enum Folder {
    FAVOR(favorFolder),
    CHAT(chatFolder),
    PROFILE_PICTURE(profilePictureFolder);

    private final String folder;

    Folder(String folder) {
      this.folder = folder;
    }

    public String toString() {
      return this.folder;
    }
  }
}
