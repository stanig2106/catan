package view.painting.jobs;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import view.painting.Painting.PaintingJob;

public class AndJob extends PaintingJob {
   final PaintingJob[] jobs;

   AndJob(PaintingJob... jobs) {
      this.jobs = jobs;
   }

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      for (PaintingJob job : jobs)
         job.paint(g, dim, imageObserver);
   }

}
