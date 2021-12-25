package view.painting.jobs;

import java.awt.Dimension;
import java.awt.*;
import java.awt.image.ImageObserver;

import globalVariables.ViewVariables;
import util_my.DrawUtils;
import view.painting.Painting.PaintingJob;

public class ErrorSizeJob extends PaintingJob {

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      g.setColor(new Color(0, 0, 0));
      g.fillRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());
      g.setFont(ViewVariables.GameFont.deriveFont(54f));
      g.setColor(new Color(130, 130, 130));
      DrawUtils.drawCenteredString(g, "Please increase",
            new Rectangle(new Point(0, -40), dim));
      DrawUtils.drawCenteredString(g, "the window size",
            new Rectangle(new Point(0, 40), dim));
   }

}
