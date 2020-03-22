package ch.epfl.favo.common;

import android.annotation.SuppressLint;

import com.google.android.gms.tasks.Task;

import java.util.concurrent.CompletableFuture;

@SuppressLint("NewApi")
public class TaskToFutureAdapter<T> extends CompletableFuture {
  TaskToFutureAdapter(Task<T> task) {
    super();
    task.addOnSuccessListener(value -> this.complete(value));
    task.addOnFailureListener(failure -> this.completeExceptionally(failure));
  }
}
