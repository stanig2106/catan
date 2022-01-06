package util_my;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import java.awt.image.ImageObserver;
import java.util.function.Consumer;

public abstract class DrawUtils {

   public static void drawCenteredString(Graphics2D g, String text, Rectangle rect, Consumer<Rectangle> bgDrawer) {
      final FontMetrics metrics = g.getFontMetrics(g.getFont());
      final int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
      final int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();

      final Rectangle2D rectBg = metrics.getStringBounds(" " + text + " ", g);
      final Color saveColor = g.getColor();

      bgDrawer.accept(new Rectangle(x,
            y - metrics.getAscent(),
            (int) rectBg.getWidth(),
            (int) rectBg.getHeight()));

      g.setColor(saveColor);
      g.drawString(text, x, y);
   }

   public static void drawCenteredString(Graphics2D g, String text, Rectangle rect) {
      FontMetrics metrics = g.getFontMetrics(g.getFont());
      int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
      int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
      g.drawString(text, x, y);
   }

   public static void drawVerticalCenteredString(Graphics2D g, String text, Rectangle rect) {
      FontMetrics metrics = g.getFontMetrics(g.getFont());
      // int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
      int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
      g.drawString(text, rect.x, y);
   }

   public static void drawCenteredImage(Graphics2D g, Image image, double width, double height, Rectangle rect,
         ImageObserver imageObserver) {
      double xOffset = (rect.getWidth() - width) / 2. + rect.getX();
      double yOffset = (rect.getHeight() - height) / 2. + rect.getY();

      g.drawImage(image, (int) xOffset, (int) yOffset, (int) width, (int) height, imageObserver);
   }
}
