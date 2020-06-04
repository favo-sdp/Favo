package ch.epfl.favo.util;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.util.concurrent.CompletableFuture;

public interface IPictureUtil {

  CompletableFuture<String> uploadPicture(Folder folder, Bitmap picture);

  CompletableFuture<Void> deletePicture(@NonNull String imagePath);

  CompletableFuture<Bitmap> downloadPicture(String pictureUrl);

  String favorFolder = "favor/";
  String chatFolder = "chat/";
  String profilePictureFolder = "profile_picture/";

  enum Folder {
    FAVOR(favorFolder),
    CHAT(chatFolder),
    PROFILE_PICTURE(profilePictureFolder);

    private String folder;

    Folder(String folder) {
      this.folder = folder;
    }

    public String toString() {
      return this.folder;
    }
  }
}
