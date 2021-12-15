package util_my;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Promise<T> {
   private boolean done, rejected = false;
   Exception rejectedException;
   T resolvedValue;
   private final Thread thread;

   public Promise(BiConsumer<Consumer<T>, Consumer<Exception>> executor, Function<Throwable, T> onException) {
      this.thread = new Thread(() -> executor.accept(this::resolve, this::reject));
      Promise<T> me = this;
      this.thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
         @Override
         public void uncaughtException(Thread thread, Throwable exception) {
            me.resolve(onException.apply(exception));
         }
      });
      this.thread.start();
   }

   public Promise(BiConsumer<Consumer<T>, Consumer<Exception>> executor) {
      this.thread = new Thread(() -> executor.accept(this::resolve, this::reject));
      Promise<T> me = this;
      this.thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
         @Override
         public void uncaughtException(Thread thread, Throwable exception) {
            me.reject(new PromiseRejectedException(exception));
         }
      });
      this.thread.start();
   }

   public Promise(T value) {
      this.thread = new Thread();
      this.resolve(value);
   }

   public Promise() {
      this.thread = new Thread();
      this.resolve(null);
   }

   public static <T> Promise<T> rejectedPromise() {
      return new Promise<T>() {
         {
            reject(new Exception("this is a rejectedPromise"));
         }
      };
   }

   public static <T> Promise<T> rejectedPromise(Exception reason) {
      return new Promise<T>() {
         {
            reject(reason);
         }
      };
   }

   /**
    * @throws PromiseRejectedException as error
    */
   public static void awaitAll(Promise<?>... promises) {
      for (Promise<?> promise : promises) {
         promise.await();
      }
   }

   public static Promise<Void> combineAll(Promise<?>... promises) {
      return new Promise<Void>((resolve, reject) -> {
         for (Promise<?> promise : promises) {
            try {
               promise.awaitOrThrow();
            } catch (PromiseRejectedException e) {
               reject.accept(e);
               return;
            }
         }
      });
   }

   protected void resolve(T value) {
      if (this.done)
         return;
      this.thread.interrupt();
      this.done = true;
      this.resolvedValue = value;
   }

   protected void reject(Exception reason) {
      if (this.done)
         return;
      this.thread.interrupt();
      this.done = true;
      this.rejected = true;
      this.rejectedException = reason;
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

   public Promise<Void> then(Consumer<T> onResolve) {
      return new Promise<Void>((resolve, reject) -> {
         try {
            onResolve.accept(this.awaitOrThrow());
            resolve.accept(null);
         } catch (PromiseRejectedException e) {
            reject.accept(e);
         }
      });
   }

   public Promise<Void> then(Consumer<T> onResolve, Consumer<PromiseRejectedException> catch_) {
      return new Promise<Void>((resolve, reject) -> {
         try {
            onResolve.accept(this.awaitOrThrow());
            resolve.accept(null);
         } catch (PromiseRejectedException e) {
            catch_.accept(e);
            reject.accept(null);
         }
      });
   }

   public <R> Promise<R> then(Function<T, R> onResolve) {
      return new Promise<R>((Consumer<R> resolve, Consumer<Exception> reject) -> {
         try {
            resolve.accept(onResolve.apply(this.awaitOrThrow()));
         } catch (PromiseRejectedException e) {
            reject.accept(e);
         }
      });
   }

   public <R> Promise<R> then(Function<T, R> onResolve, Function<PromiseRejectedException, R> catch_) {
      return new Promise<R>((Consumer<R> resolve, Consumer<Exception> reject) -> {
         try {
            resolve.accept(onResolve.apply(this.awaitOrThrow()));
         } catch (PromiseRejectedException e) {
            resolve.accept(catch_.apply(e));
         }
      });
   }

   public Promise<T> catch_(Function<PromiseRejectedException, T> catch_) {
      return new Promise<T>((resolve, reject) -> {
         try {
            resolve.accept(this.awaitOrThrow());
         } catch (PromiseRejectedException e) {
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
            resolve.accept(this.awaitOrThrow());
         } catch (PromiseRejectedException e) {
            e.printStackTrace();
            throw new Error(e);
         }
      });
   }

   public void awaitThen(Consumer<T> onResolve, Consumer<PromiseRejectedException> catch_) {
      try {
         onResolve.accept(this.awaitOrThrow());
      } catch (PromiseRejectedException e) {
         catch_.accept(e);
      }
   }

   public <R> R awaitThen(Function<T, R> onResolve, Function<PromiseRejectedException, R> catch_) {
      try {
         return onResolve.apply(this.awaitOrThrow());
      } catch (PromiseRejectedException e) {
         return catch_.apply(e);
      }
   }

   public T awaitOr(T valueOnReject) {
      try {
         return this.awaitOrThrow();
      } catch (PromiseRejectedException e) {
         return valueOnReject;
      }
   }

   public T await(Function<PromiseRejectedException, T> catch_) {
      try {
         return this.awaitOrThrow();
      } catch (PromiseRejectedException e) {
         return catch_.apply(e);
      }
   }

   /**
    * @throws PromiseRejectedException as Error
    */
   public T await() {
      try {
         return this.awaitOrThrow();
      } catch (PromiseRejectedException e) {
         System.out.println("Une erreur est survenue");
         e.printStackTrace();
         throw new Error(e);
      }
   }

   public T awaitOrThrow() throws PromiseRejectedException {
      try {
         this.thread.join();
         if (!done) {
            Error error = new Error("the promise never resolve");
            error.printStackTrace();
            throw error;
         }
      } catch (InterruptedException e) {
         if (!done) {
            e.printStackTrace();
            throw new Error(e);
         }
      }
      if (rejected)
         if (this.rejectedException != null)
            throw new PromiseRejectedException(this.rejectedException);
         else
            throw new PromiseRejectedException("the promise reject with a null Error");

      return this.resolvedValue;
   }

   // class Exception
   public static class PromiseRejectedException extends Exception {
      PromiseRejectedException(Exception exception) {
      }

      PromiseRejectedException(String message) {
      }

      PromiseRejectedException(Throwable throwable) {
         super(throwable);

      }
   }
}
