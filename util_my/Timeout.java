package util_my;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class Timeout {
   private boolean started = false;
   private boolean canceled = false;
   private final Runnable runTask;
   private Optional<ForkJoinTask<?>> taskStatus = Optional.empty();
   private final static ForkJoinPool executor = ForkJoinPool.commonPool();

   /**
    * 
    * @param delay the delay in milliseconds or
    *              -1 to create a sleeping
    *              timeout, that can only run with
    *              runImmediately
    */
   public Timeout(Runnable task, long delay) {
      this.runTask = task;
      if (delay != -1)
         submit(delay, false);
   }

   public Timeout(Runnable task) {
      this(task, 0);
   }

   public Timeout(long delay) {
      this(() -> {
      }, delay);
   }

   private void submit(long delay, boolean force) {
      this.taskStatus = Optional.of(executor.submit(() -> {
         if (delay > 0)
            try {
               Thread.sleep(delay);
            } catch (InterruptedException e) {
               e.printStackTrace();
               throw new Error(e);
            }
         if (!this.canceled || force) {
            this.started = true;
            try {
               this.runTask.run();
            } catch (Throwable e) {
               e.printStackTrace();
               throw new Error(e);
            }
         }
      }));
   }

   public void cancel() {
      if (this.started)
         return;
      this.canceled = true;
   }

   public void startImmediately() {
      if (this.started)
         return;
      this.cancel();
      this.submit(0, true);
   }

   public void joinImmediately() {
      if (this.started) {
         this.join();
         return;
      }
      this.cancel();
      this.runTask.run();
   }

   /**
    * @throws JOIN_A_SLEEPING_TIMEOUT
    */
   public void join() {
      if (this.taskStatus.isEmpty())
         throw new JOIN_A_SLEEPING_TIMEOUT();
      try {
         this.taskStatus.get().get();
      } catch (InterruptedException | ExecutionException e) {
         e.printStackTrace();
         throw new Error(e);
      }
   }

   // class Exception
   public static class JOIN_A_SLEEPING_TIMEOUT extends Error {
      JOIN_A_SLEEPING_TIMEOUT() {
         super("this is a a sleeping timeout, use joinImmediately instead");
      }
   }

}
