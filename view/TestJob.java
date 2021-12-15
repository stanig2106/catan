package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import view.Painting.PaintingJob;

public class TestJob extends PaintingJob {
   public static int calledTime;

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      calledTime++;
      System.out.println("job1 red");
      g.setColor(new Color(255, 0, 0));
      g.fillRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());
   }

}
