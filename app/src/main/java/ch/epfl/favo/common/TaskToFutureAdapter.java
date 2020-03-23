package ch.epfl.favo.common;

import android.annotation.SuppressLint;

import com.google.android.gms.tasks.Task;

import java.util.concurrent.CompletableFuture;

@SuppressLint("NewApi")
class TaskToFutureAdapter<T> extends CompletableFuture<T> {
  TaskToFutureAdapter(Task<T> task) {
    super();
    task.addOnSuccessListener(this::complete);
    task.addOnFailureListener(this::completeExceptionally);
  }
}
