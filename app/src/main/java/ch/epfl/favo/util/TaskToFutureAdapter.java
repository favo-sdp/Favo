package ch.epfl.favo.util;

import android.annotation.SuppressLint;

import com.google.android.gms.tasks.Task;

import java.util.concurrent.CompletableFuture;

@SuppressLint("NewApi")
public class TaskToFutureAdapter<T> extends CompletableFuture<T> {
  public TaskToFutureAdapter(Task<T> task) {
    super();
    task.addOnSuccessListener(this::complete);
    task.addOnFailureListener(this::completeExceptionally);
  }
}
