package ch.epfl.favo.util;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class TestTask extends Task<String> {
  // possible task status
  enum TaskStatus {
    PENDING,
    SUCCESS,
    FAILURE,
    CANCELLED
  }

  // success listeners
  private final List<OnSuccessListener> successes = new ArrayList<>();
  // failure listeners
  private final List<OnFailureListener> failures = new ArrayList<>();
  // task status
  private TaskStatus status = TaskStatus.PENDING;
  // value of the successful task, constant for testing
  final String value = "Hello world!";
  // exception to throw if task is not successful
  final RuntimeException exception = new RuntimeException("Task was not finished successfully");

  @Override
  public boolean isComplete() {
    return status != TaskStatus.PENDING;
  }

  @Override
  public boolean isSuccessful() {
    return status == TaskStatus.SUCCESS;
  }

  @Override
  public boolean isCanceled() {
    return status == TaskStatus.CANCELLED;
  }

  @Nullable
  @Override
  public String getResult() {
    if (status != TaskStatus.SUCCESS) {
      throw exception;
    }
    return value;
  }

  @Nullable
  @Override
  public <X extends Throwable> String getResult(@NonNull Class<X> aClass) throws X {
    if (status != TaskStatus.SUCCESS) {
      throw exception;
    }
    return value;
  }

  @Nullable
  @Override
  public Exception getException() {
    if (status != TaskStatus.FAILURE) {
      throw new RuntimeException("Task did not fail");
    }
    return exception;
  }

  @NonNull
  @Override
  public Task<String> addOnSuccessListener(
      @NonNull OnSuccessListener<? super String> onSuccessListener) {
    successes.add(onSuccessListener);
    return this;
  }

  @NonNull
  @Override
  public Task<String> addOnSuccessListener(
      @NonNull Executor executor, @NonNull OnSuccessListener<? super String> onSuccessListener) {
    return addOnSuccessListener(onSuccessListener);
  }

  @NonNull
  @Override
  public Task<String> addOnSuccessListener(
      @NonNull Activity activity, @NonNull OnSuccessListener<? super String> onSuccessListener) {
    return addOnSuccessListener(onSuccessListener);
  }

  @NonNull
  @Override
  public Task<String> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
    failures.add(onFailureListener);
    return this;
  }

  @NonNull
  @Override
  public Task<String> addOnFailureListener(
      @NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
    return addOnFailureListener(onFailureListener);
  }

  @NonNull
  @Override
  public Task<String> addOnFailureListener(
      @NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
    return addOnFailureListener(onFailureListener);
  }

  // force the success of the task
  void complete() {
    status = TaskStatus.SUCCESS;
    for (final OnSuccessListener success : successes) {
      success.onSuccess(value);
    }
  }

  // force failure of the task
  void fail() {
    status = TaskStatus.FAILURE;
    for (final OnFailureListener failure : failures) {
      failure.onFailure(exception);
    }
  }
}
