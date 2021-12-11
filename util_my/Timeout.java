package util_my;

public class Timeout {
   private boolean began = false;
   private boolean canceled = false;
   private final Thread runThread;

   public Timeout(Runnable runnable, int delay) {
      this.runThread = new Thread(() -> {
         try {
            Thread.sleep(delay);
            this.began = true;
            if (!this.canceled)
               runnable.run();
         } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
         }
      });

      this.runThread.start();
   }

   public void cancel() {
      if (!this.began)
         this.canceled = true;
   }

}
