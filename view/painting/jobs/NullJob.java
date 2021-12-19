package view.painting.jobs;

import java.awt.*;

import java.awt.image.*;

import view.painting.Painting.PaintingJob;

public class NullJob extends PaintingJob {

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
   }

}
