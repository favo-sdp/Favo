package ch.epfl.favo.util;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertEquals;

public class TaskToFutureAdapterTest {

  @Test
  public void taskToFutureAdapterSuccess() throws ExecutionException, InterruptedException {
    TestTask task = new TestTask();
    CompletableFuture<String> f = new TaskToFutureAdapter<>(task);
    task.complete();
    // check that the listener is triggered properly (MAY BE LATER)
    f.thenApply(
        value -> {
          assertEquals(task.value, value);
          return value;
        });
    // explicitly wait because we want the test to actually check the future termination
    String result = f.get();
    assertEquals(task.value, result);
  }

  @Test
  public void taskToFutureAdapterFailure() throws InterruptedException {
    TestTask task = new TestTask();
    CompletableFuture<String> f = new TaskToFutureAdapter<>(task);
    task.fail();
    // check that listener is triggered properly (MAY BE LATER)
    f.exceptionally(
        exception -> {
          assertEquals(task.exception, exception);
          return null;
        });
    // explicitly wait because we want the test to actually check the future termination
    try {
      f.get();
    } catch (ExecutionException e) {
      assertEquals(task.exception, e.getCause());
    }
  }
}
