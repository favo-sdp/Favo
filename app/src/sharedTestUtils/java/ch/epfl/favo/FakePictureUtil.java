package ch.epfl.favo;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import ch.epfl.favo.util.IPictureUtil;

public class FakePictureUtil implements IPictureUtil {
  CompletableFuture failedResult;
  boolean throwsError = false;

  public void setThrowError(Throwable error) {
    failedResult =
        new CompletableFuture() {
          {
            completeExceptionally(new CompletionException(error));
          }
        };
  }

  @Override
  public CompletableFuture<String> uploadPicture(Folder folder, Bitmap picture) {
    if (throwsError) return failedResult;
    return CompletableFuture.supplyAsync(() -> "testUrl");
  }

  @Override
  public CompletableFuture<Void> deletePicture(@NonNull String imagePath) {
    if (throwsError) return failedResult;
    return new CompletableFuture<Void>() {
      {
        complete(null);
      }
    };
  }

  @Override
  public CompletableFuture<Bitmap> downloadPicture(String pictureUrl) {
    if (throwsError) return failedResult;
    return null;
  }
}
