package util_my;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Promise<T> {
   protected boolean done = false;
   protected boolean rejected = false;
   private Optional<ForkJoinTask<?>> taskStatus = Optional.empty();
   protected final Optional<Timeout> taskTimeout;
   private final static ForkJoinPool executor = ForkJoinPool.commonPool();
   Exception rejectedException;
   T resolvedValue;

   public Promise(final BiConsumer<Consumer<T>, Consumer<Exception>> task,
         final Function<Throwable, T> onException) {
      this(task, onException, 0);
   }

   public Promise(final BiConsumer<Consumer<T>, Consumer<Exception>> task, final Function<Throwable, T> onException,
         long delay) {

      Runnable submit = () -> this.taskStatus = Optional.of(executor.submit(() -> {
         try {
            task.accept(this::resolve, this::reject);
         } catch (Throwable e) {
            if (onException != null)
               this.resolve(onException.apply(e));
            else
               this.reject(new RejectedExecutionException(e));
         }
      }));

      if (delay == 0) {
         this.taskTimeout = Optional.empty();
         submit.run();
      } else
         this.taskTimeout = Optional.of(new Timeout(() -> {
            submit.run();
         }, delay));

   }

   public Promise(final BiConsumer<Consumer<T>, Consumer<Exception>> task) {
      this(task, null, 0);
   }

   public Promise(final BiConsumer<Consumer<T>, Consumer<Exception>> task, final long delay) {
      this(task, null, delay);
   }

   public Promise(final T value, final long delay) {
      if (delay != 0)
         this.taskTimeout = Optional.of(new Timeout(() -> {
            this.resolve(value);
         }, delay));
      else {
         this.taskTimeout = Optional.empty();
         this.resolve(value);
      }
   }

   public Promise(final T value) {
      this(value, 0);
   }

   public Promise() {
      this((T) null);
   }

   public Promise(final long delay) {
      this((T) null, delay);
   }

   private Promise(final boolean rejected, final Exception reason, final long delay) {
      if (!this.rejected)
         throw new Error("this constructor only accept true, use Promise() instead");
      if (delay != 0)
         this.taskTimeout = Optional.of(new Timeout(() -> {
            this.reject(new Exception("this is a RejectedPromise"));
         }, delay));
      else {
         this.reject(new Exception("this is a RejectedPromise"));
         this.taskTimeout = Optional.empty();
      }
   }

   public static <T> Promise<T> rejectedPromise(final Exception reason, final long delay) {
      return new Promise<T>(true, reason, delay);
   }

   public static <T> Promise<T> rejectedPromise() {
      return rejectedPromise(new Exception("this is a RejectedPromise"), 0);
   }

   public static <T> Promise<T> rejectedPromise(final long delay) {
      return rejectedPromise(new Exception("this is a RejectedPromise"), delay);
   }

   public static <T> Promise<T> rejectedPromise(final Exception reason) {
      return rejectedPromise(reason, 0);
   }

   /**
    * @throws PromiseRejectedException as error
    */
   public static void awaitAll(final Promise<?>... promises) {
      for (final Promise<?> promise : promises) {
         promise.await();
      }
   }

   public static Promise<Void> combineAll(final Promise<?>... promises) {
      return new Promise<Void>((resolve, reject) -> {
         for (final Promise<?> promise : promises) {
            try {
               promise._await();
            } catch (final PromiseRejectedException e) {
               reject.accept(e);
               return;
            }
         }
         resolve.accept(null);
      });
   }

   protected void resolve(final T value) {
      if (this.done)
         return;
      this.done = true;
      this.resolvedValue = value;
   }

   protected void reject(final Exception reason) {
      if (this.done)
         return;
      this.done = true;
      this.rejected = true;
      this.rejectedException = reason;
   }

   private T _await() throws PromiseRejectedException {
      if (!this.done) { // run the promise or wait it done
         this.taskTimeout.ifPresent(taskTimeout -> taskTimeout.joinImmediately());
         this.taskStatus.ifPresent(taskStatus -> {
            try {
               taskStatus.get();
            } catch (InterruptedException | ExecutionException e) {
               e.printStackTrace();
               throw new Error(e);
            }
         });
      }
      if (!this.done)
         throw new Error("The promise never resolve or resolve asynchronously");
      if (this.rejected)
         if (this.rejectedException != null)
            throw new PromiseRejectedException(this.rejectedException);
         else
            throw new PromiseRejectedException("the promise reject with a null Error");
      return this.resolvedValue;
   }

   // public method
   public boolean resolved() {
      return this.done && !this.rejected;
   }

   public boolean rejected() {
      return this.done && this.rejected;
   }

   public boolean done() {
      return this.done;
   }

   /**
    * like await(), but don't get the result
    * 
    * @throws PromiseRejectedException as Error
    */
   public Promise<T> work() {
      this.await();
      return this;
   }

   public Promise<Void> then(final Consumer<T> onResolve) {
      return new Promise<Void>((resolve, reject) -> {
         try {
            onResolve.accept(this._await());
            resolve.accept(null);
         } catch (final PromiseRejectedException e) {
            reject.accept(e);
         }
      });
   }

   public Promise<Void> then(final Consumer<T> onResolve, Consumer<PromiseRejectedException> catch_) {
      return new Promise<Void>((resolve, reject) -> {
         try {
            onResolve.accept(this._await());
            resolve.accept(null);
         } catch (final PromiseRejectedException e) {
            catch_.accept(e);
            reject.accept(null);
         }
      });
   }

   public <R> Promise<R> then(final Function<T, R> onResolve) {
      return new Promise<R>((Consumer<R> resolve, Consumer<Exception> reject) -> {
         try {
            resolve.accept(onResolve.apply(this._await()));
         } catch (final PromiseRejectedException e) {
            reject.accept(e);
         }
      });
   }

   public <R> Promise<R> then(final Function<T, R> onResolve,
         final Function<PromiseRejectedException, R> catch_) {
      return new Promise<R>((Consumer<R> resolve, Consumer<Exception> reject) -> {
         try {
            resolve.accept(onResolve.apply(this._await()));
         } catch (final PromiseRejectedException e) {
            resolve.accept(catch_.apply(e));
         }
      });
   }

   public Promise<T> catch_(final Function<PromiseRejectedException, T> catch_) {
      return new Promise<T>((resolve, reject) -> {
         try {
            resolve.accept(this._await());
         } catch (final PromiseRejectedException e) {
            resolve.accept(catch_.apply(e));
         }
      });
   }

   /**
    * @throws PromiseRejectedException as Error
    */
   public Promise<T> catchToError() {
      return new Promise<T>((resolve, reject) -> {
         try {
            resolve.accept(this._await());
         } catch (final PromiseRejectedException e) {
            e.printStackTrace();
            throw new Error(e);
         }
      });
   }

   public void awaitThen(final Consumer<T> onResolve, Consumer<PromiseRejectedException> catch_) {
      try {
         onResolve.accept(this._await());
      } catch (final PromiseRejectedException e) {
         catch_.accept(e);
      }
   }

   public <R> R awaitThen(final Function<T, R> onResolve, Function<PromiseRejectedException, R> catch_) {
      try {
         return onResolve.apply(this._await());
      } catch (final PromiseRejectedException e) {
         return catch_.apply(e);
      }
   }

   public T awaitOr(final T valueOnReject) {
      try {
         return this._await();
      } catch (final Exception e) {
         return valueOnReject;
      }
   }

   public T await(final Function<PromiseRejectedException, T> catch_) {
      try {
         return this._await();
      } catch (PromiseRejectedException e) {
         return catch_.apply(e);
      }
   }

   /**
    * @throws PromiseRejectedException as Error
    */
   public T await() {
      try {
         return this._await();
      } catch (final PromiseRejectedException e) {
         e.printStackTrace();
         throw new Error(e);
      }
   }

   public T awaitOrThrow() throws PromiseRejectedException {
      return this._await();
   }

   // class Exception
   public static class PromiseRejectedException extends Exception {
      PromiseRejectedException(final String message) {
         super(message);
      }

      PromiseRejectedException(final Throwable throwable) {
         super(throwable);
      }
   }
}
