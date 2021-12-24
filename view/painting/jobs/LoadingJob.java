package view.painting.jobs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import globalVariables.ViewVariables;
import util_my.DrawUtils;
import view.painting.Painting.PaintingJob;

public class LoadingJob extends PaintingJob {

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      g.setColor(new Color(0, 0, 0));
      g.fillRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());
      g.setFont(ViewVariables.GameFont.deriveFont(54f));
      g.setColor(new Color(130, 130, 130));
      DrawUtils.drawCenteredString(g, "Do you prefer a pretty or a fast load ?",
            new Rectangle(new Point(0, 0), dim));
   }

}
