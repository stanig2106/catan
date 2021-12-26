package view.painting.jobs.gameInterface;

import java.awt.Dimension;
import java.awt.*;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.awt.AWTEventMulticaster;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import globalVariables.ViewVariables;
import util_my.DrawUtils;
import util_my.Promise;
import view.painting.Painting.PaintingJob;

public class DicesJob extends PaintingJob {
   public static Map<Integer, Promise<Image>> facesImage = new HashMap<>() {
      {
         IntStream.rangeClosed(1, 6)
               .forEach(i -> this.put(i, ViewVariables.importImage("assets/dices/" + i + "_dots.png", -1)));
      }
   };

   private int first;
   private int second;

   public DicesJob() {
      this.nextRandomDices();
   }

   private boolean modified = false;

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      this.modified = false;
      DrawUtils.drawCenteredImage(g, facesImage.get(first).await(), 200, 200,
            new Rectangle(0, 0, (int) dim.getWidth() - 300, (int) dim.getHeight()),
            imageObserver);

      DrawUtils.drawCenteredImage(g, facesImage.get(second).await(), 200, 200,
            new Rectangle(300, 0, (int) dim.getWidth() - 300, (int) dim.getHeight()),
            imageObserver);

   }

   public void nextRandomDices() {
      this.modified = true;
      this.first = (int) (Math.random() * 6) + 1;
      this.second = (int) (Math.random() * 6) + 1;
   }

   public void showResult(int first, int second) {
      this.modified = true;
      this.first = first;
      this.second = second;
   }

   @Override
   public boolean needReload() {
      return this.modified;
   }

}
