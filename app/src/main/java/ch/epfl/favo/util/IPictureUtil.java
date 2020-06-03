package ch.epfl.favo.util;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.util.concurrent.CompletableFuture;

public interface IPictureUtil {

  CompletableFuture<String> uploadPicture(Folder folder, Bitmap picture);

  CompletableFuture<Void> deletePicture(@NonNull String imagePath);

  CompletableFuture<Bitmap> downloadPicture(String pictureUrl);

  enum Folder {
    FAVOR("favor/"),
    CHAT("chat/"),
    PROFILE_PICTURE("profile_picture/");

    private String folder;

    Folder(String folder) { this.folder = folder; }

    public String toString() { return this.folder; }
  }
}
