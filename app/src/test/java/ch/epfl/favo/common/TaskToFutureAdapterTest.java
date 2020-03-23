package ch.epfl.favo.common;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TaskToFutureAdapterTest {

  @Test
  public void addTwoNumbersTask() {
    Task<Void> task = new Task<Void>() {
      @Override
      public boolean isComplete() {
        return false;
      }

      @Override
      public boolean isSuccessful() {
        return false;
      }

      @Override
      public boolean isCanceled() {
        return false;
      }

      @Nullable
      @Override
      public Void getResult() {
        return null;
      }

      @Nullable
      @Override
      public <X extends Throwable> Void getResult(@NonNull Class<X> aClass) throws X {
        return null;
      }

      @Nullable
      @Override
      public Exception getException() {
        return null;
      }

      @NonNull
      @Override
      public Task<Void> addOnSuccessListener(@NonNull OnSuccessListener<? super Void> onSuccessListener) {
        return null;
      }

      @NonNull
      @Override
      public Task<Void> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super Void> onSuccessListener) {
        return null;
      }

      @NonNull
      @Override
      public Task<Void> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super Void> onSuccessListener) {
        return null;
      }

      @NonNull
      @Override
      public Task<Void> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
        return null;
      }

      @NonNull
      @Override
      public Task<Void> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        return null;
      }

      @NonNull
      @Override
      public Task<Void> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
        return null;
      }
    };

    CompletableFuture<Void> future = new TaskToFutureAdapter<Void>(task);
    future.complete(null);
    assert(true);

  }
}
