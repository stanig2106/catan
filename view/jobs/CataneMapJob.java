package view.jobs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;
import gameVariables.GameVariables;
import map.Border;
import map.Land;
import map.constructions.Route;
import util_my.Coord;
import util_my.directions.LandSide;
import view.ViewVariables;
import view.Painting.PaintingJob;

public class CataneMapJob extends PaintingJob {
   public static int calledTime;
   public final BackgroundJob backgroundJob = new BackgroundJob();

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      calledTime++;
      System.out.println("CataneMapJob " + calledTime);
      int size = (int) Math.min((dim.getHeight() / 10.), dim.getWidth() / (Math.sqrt(3) * 7.));
      this.backgroundJob.paint(g, dim, imageObserver);
      new LandJob(size).paint(g, dim, imageObserver);
      new RouteJob(size).paint(g, dim, imageObserver);

   }

}

class LandJob extends PaintingJob {
   final int size;

   LandJob(int size) {
      this.size = size;
   }

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {

      int height = 2 * size;
      int width = (int) (Math.sqrt(3) * size);

      g.setColor(new Color(237, 211, 151));
      g.fillOval((int) (dim.getWidth() / 2.) - (int) (width * 2.75),
            (int) (dim.getHeight() / 2.) - (int) (height * 2.5),
            (int) (width * 5.5),
            height * 5);

      g.drawImage(ViewVariables.backgroundImage, (int) (dim.getWidth() / 2) - (int) (11.56 * size / 2.0),
            (int) (dim.getHeight() / 2) - (int) (10.11 * size / 2.0),
            (int) (11.56 * size), (int) (10.11 * size), imageObserver);

      GameVariables.map.forEachCoordinate(coord -> {
         Matrix position = ViewVariables.basisMatrix.times(coord.toMatrix()).times(size);
         int x = (int) position.get(0, 0);
         int y = (int) position.get(1, 0);
         g.drawImage(GameVariables.map.get(coord).image, x + (int) (dim.getWidth() / 2. - width / 2.),
               y + (int) (dim.getHeight() / 2. - height / 2.), width,
               height,
               imageObserver);
      });
   }

}

class RouteJob extends PaintingJob {
   final int size;

   RouteJob(int size) {
      this.size = size;
   }

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      List<Border> drawnBorders = new ArrayList<Border>();
      GameVariables.map.forEachCoordinate(coord -> {
         Land land = GameVariables.map.get(coord);
         LandSide.stream().forEach(landSide -> {
            Border border = land.borders.get(landSide);
            border.route.ifPresent(route -> {
               if (drawnBorders.indexOf(border) == -1) {
                  this.drawRouteOn(g, coord, landSide, size, dim, route, imageObserver);
                  drawnBorders.add(border);
               }
            });

         });
      });
   }

   public void drawRouteOn(Graphics2D g, Coord coord, LandSide side, int size, Dimension dim, Route route,
         ImageObserver imageObserver) {
      Matrix position = ViewVariables.basisMatrix.times(coord.toMatrix()).times(size);
      int x = (int) position.get(0, 0);
      int y = (int) position.get(1, 0);
      int routeHeight = size;
      int routeWidth = (int) (size / 3.);

      Image routeImg = route.owner.routeImage;
      AffineTransform transform = new AffineTransform();

      switch (side) {
         case topLeft:
            transform.translate(x + (int) (dim.getWidth() / 2. - routeWidth / 2.) - (int) (size * 0.20),
                  y + (int) (dim.getHeight() / 2. - routeHeight / 2.) - (int) (size * 0.20));
            transform.rotate(Math.toRadians(60));
            break;
         case topRight:
            transform.translate(x + (int) (dim.getWidth() / 2. - routeWidth / 2.) - (int) (size * 0.30) + size,
                  y + (int) (dim.getHeight() / 2. - routeHeight / 2.) - (int) (size * 0.20));
            transform.rotate(Math.toRadians(-60));
            break;
         case right:
            g.drawImage(routeImg, x + (int) (dim.getWidth() / 2. - routeWidth / 2.) + (int) (size * 0.87),
                  y + (int) (dim.getHeight() / 2. - routeHeight / 2.),
                  routeWidth,
                  routeHeight, imageObserver);
            return;
         case left:
            g.drawImage(routeImg, x + (int) (dim.getWidth() / 2. - routeWidth / 2.) - (int) (size * 0.87),
                  y + (int) (dim.getHeight() / 2. - routeHeight / 2.),
                  routeWidth,
                  routeHeight, imageObserver);

            return;
         case bottomLeft:
            transform.translate(
                  x + (int) (dim.getWidth() / 2. - routeWidth / 2.) + (int) (size * 0.70) - size,
                  y + (int) (dim.getHeight() / 2. - routeHeight / 2.) + (int) (size * 1.3));
            transform.rotate(Math.toRadians(-60));
            break;
         case bottomRight:
            transform.translate(x + (int) (dim.getWidth() / 2. - routeWidth / 2.) + (int) (size * 0.70),
                  y + (int) (dim.getHeight() / 2. - routeHeight / 2.) + (int) (size * 1.3));
            transform.rotate(Math.toRadians(60));
            break;
         default:
            throw new Error("Unknown side");
      }

      transform.scale((double) routeWidth / routeImg.getWidth(imageObserver),
            (double) routeHeight / routeImg.getHeight(imageObserver));
      transform.translate(-routeImg.getWidth((ImageObserver) routeImg) / 2,
            -routeImg.getHeight((ImageObserver) routeImg) / 2);
      ((Graphics2D) g).drawImage(routeImg, transform, imageObserver);
   }

}

class BackgroundJob extends PaintingJob {

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      g.setColor(new Color(22, 145, 198));
      g.fillRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());
   }

}