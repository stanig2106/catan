package view.painting.jobs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import view.painting.Painting.PaintingJob;

public class TestJob extends PaintingJob {
   public static int calledTime;

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      calledTime++;
      g.setColor(new Color(255, 0, 0));
      g.fillRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());
   }

}
