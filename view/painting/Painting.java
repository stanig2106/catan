package view.painting;

import java.awt.Canvas;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Optional;

import javax.swing.JPanel;

import util_my.Promise;
import java.awt.Rectangle;

public class Painting {
   private BufferedImage image;
   private Optional<BufferedImage> backup = Optional.empty();
   private Optional<Dimension> backupDimension = Optional.empty();
   private int width, height;
   private PaintingJob job;

   private Painting() {

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

   public Promise<Void> forceUpdatePainting() {
      return this.updatePainting(width, height, job, true);
   }

   public Promise<Void> updatePainting(int width, int height, PaintingJob job) {
      return this.updatePainting(width, height, job, false);
   }

   private Promise<Void> updatePainting(int width, int height, PaintingJob job, boolean force) {
      final int oldWidth = this.width;
      final int oldHeight = this.height;
      final PaintingJob oldJob = this.job;
      this.width = width;
      this.height = height;
      this.job = job;
      return new Promise<Void>((resolve, reject) -> {
         if (width <= 0 || height <= 0) {
            System.out.println("dim 0");
            resolve.accept(null);
            return;
         }
         if (oldWidth == this.width && oldHeight == this.height && oldJob == this.job && !force) {
            // System.out.println("Already worked");
            resolve.accept(null);
            return;
         }
         resolve.accept(null);

         final BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
         final Graphics2D g = (Graphics2D) image.getGraphics();

         this.job.paint(g, new Dimension(image.getWidth(), image.getHeight()));
         this.image = image;
         resolve.accept(null);
      });
   }

   public Promise<Void> updatePainting(Dimension dim, PaintingJob job) {
      return this.updatePainting((int) dim.getWidth(), (int) dim.getHeight(), job);
   }

   public Promise<Void> updatePainting(Dimension dim) {
      return this.updatePainting((int) dim.getWidth(), (int) dim.getHeight(), this.job);
   }

   public Promise<Void> updatePainting(int width, int height) {
      return this.updatePainting(width, height, this.job);
   }

   public Promise<Void> updatePainting(PaintingJob job) {
      return this.updatePainting(this.width, this.height, job);
   }

   public Promise<Void> updatePainting() {
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

   public Promise<Void> paintBackupTo(JPanel to, Graphics2D g) {
      return new Promise<Void>((resolve, reject) -> {
         g.drawImage(this.backup.orElse(this.image), 0, 0, to);
         resolve.accept(null);
      });
   }

   public Promise<Void> paintBackupTo(Canvas to) {
      return new Promise<Void>((resolve, reject) -> {
         to.getGraphics().drawImage(this.backup.orElse(this.image), 0, 0, to);
         resolve.accept(null);
      });
   }

   public void backup() {
      this.setBackup(this.image);
   }

   public void destroyBackup() {
      this.backup = Optional.empty();
      this.backupDimension = Optional.empty();
   }

   public Optional<Dimension> getBackupDimension() {
      return backupDimension;
   }

   public boolean hasBackup() {
      return this.backup.isPresent();
   }

   public Optional<BufferedImage> getBackup() {
      return backup;
   }

   public Optional<Promise<BufferedImage>> getBackupClone() {
      return this.backup.map(backup -> new Promise<BufferedImage>((resolve, reject) -> {
         BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
         Graphics2D g = (Graphics2D) image.getGraphics();
         g.drawImage(backup, 0, 0, null);
         resolve.accept(image);
      }));
   }

   public void setBackup(BufferedImage backup) {
      this.backup = Optional.of(backup);
      this.backupDimension = Optional.of(new Dimension(this.image.getWidth(), this.image.getHeight()));
   }

   public static abstract class PaintingJob {
      public final void paint(Graphics2D g, Dimension dim) {
         this.paint(g, dim, null);
      }

      abstract public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver);
   }

}
