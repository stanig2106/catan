package view.painting.jobs.gameInterface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import globalVariables.ViewVariables;
import util_my.DrawUtils;
import util_my.Promise;
import view.painting.Painting.PaintingJob;

public abstract class MenuJob extends PaintingJob {
   public static final Promise<Image> ParchemineTexture = ViewVariables
         .importImage("assets/menu/ParchemineTexture.png").work();
   public static final Promise<Image> woodTexture = ViewVariables
         .importImage("assets/menu/WoodTexture.jpg").work();
   public static final Promise<Image> woodTextureLight = ViewVariables
         .importImage("assets/menu/WoodTextureLight.jpg").work();
   public static final Promise<Image> woodTextureDark = ViewVariables
         .importImage("assets/menu/WoodTextureDark.jpg");

   final double pourcentage;

   public MenuJob() {
      this(100);
   }

   private MenuJob(double pourcentage) { // FIXME: not working
      this.pourcentage = pourcentage;
   }

   @Override
   public final void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      g.setColor(new Color(248, 227, 193));
      g.fillRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());
      final Image parcheminTexture = MenuJob.ParchemineTexture.await();
      double scale = dim.getWidth() / (double) parcheminTexture.getWidth(imageObserver);
      DrawUtils.drawCenteredImage(g, parcheminTexture, dim.getWidth() * this.pourcentage / 100.,
            parcheminTexture.getHeight(imageObserver) * scale * this.pourcentage / 100., new Rectangle(dim),
            imageObserver);

      this.paintContent(g, dim, imageObserver);
   }

   protected abstract void paintContent(Graphics2D g, Dimension dim, ImageObserver imageObserver);

}
