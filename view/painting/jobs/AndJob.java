package view.painting.jobs;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.stream.Stream;

import view.painting.Painting.PaintingJob;

public class AndJob extends PaintingJob {
   final PaintingJob[] jobs;

   public AndJob(PaintingJob... jobs) {
      this.jobs = jobs;
   }

   public AndJob(PaintingJob job, PaintingJob... jobs) {
      this.jobs = Stream.concat(Stream.of(job), Stream.of(jobs)).toArray(PaintingJob[]::new);
   }

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      for (PaintingJob job : jobs)
         job.paint(g, dim, imageObserver);
   }

   @Override
   public boolean needReload() {
      return Stream.of(jobs).anyMatch(PaintingJob::needReload);
   }

}
