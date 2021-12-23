package view.painting.jobs;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.AlphaComposite;

import java.awt.geom.AffineTransform;

import map.constructions.Route;
import util_my.directions.LandSide;
import view.painting.Painting.PaintingJob;

public class OneRouteJob extends PaintingJob {
   final Route route;
   final LandSide direction;
   final int size;

   public OneRouteJob(Route route, int size, LandSide direction) {
      this.route = route;
      this.direction = direction;
      this.size = size;
   }

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      // g.setColor(new Color(255, 145, 198));
      // g.fillRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());
      Image routeImg = this.route.image.await();
      AffineTransform transform = new AffineTransform();
      final int routeHeight = size;
      final int routeWidth = (int) (size / 3.);

      switch (this.direction) {
         case topLeft:
         case bottomRight:
            transform.translate(routeHeight * Math.sin(Math.toRadians(60)), 0);
            transform.rotate(Math.toRadians(60));
            break;
         case topRight:
         case bottomLeft:
            transform.translate(0, routeWidth * Math.sin(Math.toRadians(60)));
            transform.rotate(Math.toRadians(-60));
            break;
         default:
            break;
      }

      // transform.translate(dim.getWidth() / 2., dim.getHeight() / 2.);
      transform.scale((double) routeWidth / (double) routeImg.getWidth(imageObserver),
            (double) routeHeight / (double) routeImg.getHeight(imageObserver));
      // transform.translate(-routeImg.getWidth((ImageObserver) routeImg) / 2.,
      // -routeImg.getHeight((ImageObserver) routeImg) / 2.);
      g.setComposite(AlphaComposite.SrcOver.derive(0.7f));

      ((Graphics2D) g).drawImage(routeImg, transform, imageObserver);

   }

}
