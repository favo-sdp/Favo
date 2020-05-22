package ch.epfl.favo.util;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.util.concurrent.CompletableFuture;

public interface IPictureUtil {


  CompletableFuture<String> uploadPicture(Bitmap picture);

  CompletableFuture<Void> deletePicture(@NonNull String imagePath);

  CompletableFuture<Bitmap> downloadPicture(String pictureUrl);
}
