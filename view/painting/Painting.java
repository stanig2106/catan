package view.painting;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Optional;
import java.util.stream.Stream;

import javax.swing.JPanel;

import util_my.Promise;
import view.painting.jobs.AndJob;

public class Painting {
   private BufferedImage image;
   private int width, height;
   private PaintingJob job;

   private Painting() {

   }

   public PaintingJob getJobs() {
      return this.job;
   }

   public static Promise<Painting> newPainting(Dimension dim, PaintingJob job) {
      return Painting.newPainting((int) dim.getWidth(), (int) dim.getHeight(), job);
   }

   public static Promise<Painting> newPainting(int width, int height, PaintingJob job) {
      return new Promise<Painting>((resolve, reject) -> {
         final Painting res = new Painting();
         res.updatePainting(width, height, job).await();
         resolve.accept(res);
      });
   }

   public Promise<Void> addJobs(PaintingJob... jobs) {
      return new Promise<Void>((resolve, reject) -> {
         final Graphics2D g = (Graphics2D) this.image.getGraphics();

         this.job = new AndJob(this.job, jobs);
         Stream.of(jobs).forEach(job -> job.paint(g, new Dimension(this.image.getWidth(), this.image.getHeight())));

         resolve.accept(null);
      });
   }

   public Promise<Boolean> forceUpdatePainting() {
      return this.updatePainting(width, height, job, true);
   }

   public Promise<Boolean> updatePainting(Painting painting) {
      return this.updatePainting(painting.width, painting.height, painting.job, false);
   }

   public Promise<Boolean> updatePainting(Promise<Painting> promisedPainting) {
      return new Promise<Boolean>((resolve, reject) -> {
         final Painting painting = promisedPainting.await();
         resolve.accept(this.updatePainting(painting.width, painting.height, painting.job, false).await());
      });
   }

   public Promise<Boolean> updatePainting(int width, int height, PaintingJob job) {
      return this.updatePainting(width, height, job, false);
   }

   private Promise<Boolean> updatePainting(int width, int height, PaintingJob job, boolean force) {
      final int oldWidth = this.width;
      final int oldHeight = this.height;
      final PaintingJob oldJob = this.job;
      this.width = width;
      this.height = height;
      this.job = job;
      return new Promise<Boolean>((resolve, reject) -> {
         if (width <= 0 || height <= 0) {
            resolve.accept(false);
            return;
         }
         if (oldWidth == this.width && oldHeight == this.height && oldJob == this.job && !force
               && !this.job.needReload()) {
            // System.out.println("Already worked");
            resolve.accept(false);
            return;
         }

         final BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
         final Graphics2D g = (Graphics2D) image.getGraphics();

         this.job.paint(g, new Dimension(image.getWidth(), image.getHeight()));
         this.image = image;
         resolve.accept(true);
      });
   }

   public Promise<Boolean> updatePainting(Dimension dim, PaintingJob job) {
      return this.updatePainting((int) dim.getWidth(), (int) dim.getHeight(), job);
   }

   public Promise<Boolean> updatePainting(Dimension dim) {
      return this.updatePainting((int) dim.getWidth(), (int) dim.getHeight(), this.job);
   }

   public Promise<Boolean> updatePainting(int width, int height) {
      return this.updatePainting(width, height, this.job);
   }

   public Promise<Boolean> updatePainting(PaintingJob job) {
      return this.updatePainting(this.width, this.height, job);
   }

   public Promise<Boolean> updatePainting() {
      return this.updatePainting(this.width, this.height, this.job);
   }

   public Promise<Void> paintTo(JPanel to, Graphics2D g) {
      return new Promise<Void>((resolve, reject) -> {
         g.drawImage(this.image, 0, 0, to);
         resolve.accept(null);
      });
   }

   public Promise<Void> paintSubImageTo(Canvas to, Rectangle rect) {
      return new Promise<Void>((resolve, reject) -> {
         to.getGraphics().drawImage(this.image, 0, 0, (int) rect.getWidth(), (int) rect.getHeight(),
               (int) rect.getX(),
               (int) rect.getY(),
               (int) rect.getX() + (int) rect.getWidth(),
               (int) rect.getY() + (int) rect.getHeight(), to);
         resolve.accept(null);
      });
   }

   public Promise<Void> paintTo(Canvas to) {
      return new Promise<Void>((resolve, reject) -> {
         to.getGraphics().drawImage(this.image, 0, 0, to);
         resolve.accept(null);
      });
   }

   public static abstract class PaintingJob {
      public final void paint(Graphics2D g, Dimension dim) {
         this.paint(g, dim, null);
      }

      public boolean needReload() {
         return false;
      }

      abstract public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver);
   }

}
