package view.painting.jobs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Jama.Matrix;
import globalVariables.GameVariables;
import globalVariables.ViewVariables;
import map.Border;
import map.CataneMap;
import map.Corner;
import map.Land;
import map.constructions.Building;
import map.constructions.Route;
import util_my.Coord;
import util_my.directions.LandCorner;
import util_my.directions.LandSide;
import view.painting.Painting.PaintingJob;

public class CataneMapJob extends PaintingJob {
   public final BackgroundJob backgroundJob = new BackgroundJob();
   public static Map<Coord, Point> landsPosition;
   private final int landSize;
   private final Point mapCenter;

   public CataneMapJob(int landSize, Point mapCenter) {
      this.landSize = landSize;
      this.mapCenter = mapCenter;
   }

   public static void init(CataneMap map) { // called after map creation
      landsPosition = new HashMap<Coord, Point>(map.numberOfCase()) {
         @Override
         public Point replace(Coord arg0, Point arg1) {
            return this.keySet().stream().filter(coord -> coord.equals(arg0)).findFirst()
                  .map(coord -> super.replace(coord, arg1)).orElse(null);
         }

         @Override
         public Point get(Object key) {
            return this.keySet().stream().filter(coord -> coord.equals(key)).findFirst()
                  .map(coord -> super.get(coord)).orElse(null);
         }
      };
      map.forEachCoordinate(coord -> {
         landsPosition.put(coord, null);
      });
   }

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      this.backgroundJob.paint(g, dim, imageObserver);
      new LandJob(this.landSize, this.mapCenter).paint(g, dim, imageObserver);
      new RouteJob(this.landSize, this.mapCenter).paint(g, dim, imageObserver);
      new BuildingJob(this.landSize, this.mapCenter).paint(g, dim, imageObserver);
   }

}

class LandJob extends PaintingJob {
   final int size;
   private final Point mapCenter;

   LandJob(int size, Point mapCenter) {
      this.size = size;
      this.mapCenter = mapCenter;
   }

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {

      int height = 2 * size;
      int width = (int) (Math.sqrt(3) * size);

      g.setColor(new Color(237, 211, 151));
      g.fillOval((int) (mapCenter.getX()) - (int) (width * 2.75),
            (int) (mapCenter.getY()) - (int) (height * 2.5),
            (int) (width * 5.5),
            (int) (height * 4.75));

      g.drawImage(CataneMap.backgroundImage.await(), (int) (mapCenter.getX() -
            11.56 * size / 2.0),
            (int) (mapCenter.getY() - 10.11 * size / 2.0),
            (int) (11.56 * size), (int) (10.11 * size), imageObserver);

      GameVariables.map.forEachCoordinate(coord -> {
         Matrix position = ViewVariables.hexToPixelMatrix.times(coord.toMatrix()).times(size);
         int x = (int) position.get(0, 0);
         int y = (int) position.get(1, 0);
         CataneMapJob.landsPosition.replace(coord, new Point(x + (int) mapCenter.getX(), y + (int) mapCenter.getY()));
         g.drawImage(GameVariables.map.get(coord).image.await(), x + (int) (mapCenter.getX() - width / 2.),
               y + (int) (mapCenter.getY() - height / 2.), width,
               height,
               imageObserver);
      });
   }

}

class RouteJob extends PaintingJob {
   final int size;
   private final Point mapCenter;

   RouteJob(int size, Point mapCenter) {
      this.size = size;
      this.mapCenter = mapCenter;
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
                  this.drawRouteOn(g, coord, landSide, dim, route, imageObserver);
                  drawnBorders.add(border);
               }
            });

         });
      });
   }

   public void drawRouteOn(Graphics2D g, Coord coord, LandSide side, Dimension dim, Route route,
         ImageObserver imageObserver) {
      Matrix position = ViewVariables.hexToPixelMatrix.times(coord.toMatrix()).times(size);
      int x = (int) position.get(0, 0);
      int y = (int) position.get(1, 0);
      int routeHeight = size;
      int routeWidth = (int) (size / 3.);

      Image routeImg = route.image.await();
      AffineTransform transform = new AffineTransform();

      switch (side) {
         case topLeft:
            transform.translate(x + (int) (mapCenter.getX() - routeWidth / 2.) - (int) (size * 0.20),
                  y + (int) (mapCenter.getY() - routeHeight / 2.) - (int) (size * 0.20));
            transform.rotate(Math.toRadians(60));
            break;
         case topRight:
            transform.translate(x + (int) (mapCenter.getX() - routeWidth / 2.) - (int) (size * 0.30) + size,
                  y + (int) (mapCenter.getY() - routeHeight / 2.) - (int) (size * 0.20));
            transform.rotate(Math.toRadians(-60));
            break;
         case right:
            g.drawImage(routeImg, x + (int) (mapCenter.getX() - routeWidth / 2.) + (int) (size * 0.87),
                  y + (int) (mapCenter.getY() - routeHeight / 2.),
                  routeWidth,
                  routeHeight, imageObserver);
            return;
         case left:
            g.drawImage(routeImg, x + (int) (mapCenter.getX() - routeWidth / 2.) - (int) (size * 0.87),
                  y + (int) (mapCenter.getY() - routeHeight / 2.),
                  routeWidth,
                  routeHeight, imageObserver);

            return;
         case bottomLeft:
            transform.translate(
                  x + (int) (mapCenter.getX() - routeWidth / 2.) + (int) (size * 0.70) - size,
                  y + (int) (mapCenter.getY() - routeHeight / 2.) + (int) (size * 1.3));
            transform.rotate(Math.toRadians(-60));
            break;
         case bottomRight:
            transform.translate(x + (int) (mapCenter.getX() - routeWidth / 2.) + (int) (size * 0.70),
                  y + (int) (mapCenter.getY() - routeHeight / 2.) + (int) (size * 1.3));
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

class BuildingJob extends PaintingJob {
   final int size;
   private final Point mapCenter;

   BuildingJob(int size, Point mapCenter) {
      this.size = size;
      this.mapCenter = mapCenter;
   }

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      List<Corner> drawnCorners = new ArrayList<Corner>();
      GameVariables.map.forEachCoordinate(coord -> {
         Land land = GameVariables.map.get(coord);
         LandCorner.stream().forEach(landCorner -> {
            Corner corner = land.corners.get(landCorner);
            corner.building.ifPresent(building -> {
               if (drawnCorners.indexOf(corner) == -1) {
                  this.drawBuildingOn(g, coord, landCorner, dim, building, imageObserver);
                  drawnCorners.add(corner);
               }
            });

         });
      });

   }

   private void drawBuildingOn(Graphics2D g, Coord coord, LandCorner corner, Dimension dim, Building building,
         ImageObserver imageObserver) {
      Image image = building.image.await();

      Matrix position = ViewVariables.hexToPixelMatrix.times(coord.toMatrix()).times(size);
      int x = (int) position.get(0, 0);
      int y = (int) position.get(1, 0);
      int height = (int) (size / 1.7);
      int width = (int) (size / 1.7);
      switch (corner) {
         case top:
            g.drawImage(image, x + (int) (mapCenter.getX() - width / 2.),
                  y + (int) (mapCenter.getY() - height / 2. - size),
                  width,
                  height, imageObserver);
            break;
         case bottom:
            g.drawImage(image, x + (int) (mapCenter.getX() - width / 2.),
                  y + (int) (mapCenter.getY() - height / 2. + size),
                  width,
                  height, imageObserver);
            break;
         case topLeft:
            g.drawImage(image, x + (int) (mapCenter.getX() - width / 2.) - (int) (size * 0.87),
                  y + (int) (mapCenter.getY() - height / 2. - size / 2.3),
                  width,
                  height, imageObserver);
            break;
         case topRight:
            g.drawImage(image, x + (int) (mapCenter.getX() - width / 2.) - (int) (size * 1.1) + size * 2,
                  y + (int) (mapCenter.getY() - height / 2. - size / 2.3),
                  width,
                  height, imageObserver);
            break;
         case bottomLeft:
            g.drawImage(image, x + (int) (mapCenter.getX() - width / 2.) - (int) (size * 0.87),
                  y + (int) (mapCenter.getY() - height / 2. + size / 2.3),
                  width,
                  height, imageObserver);
            break;
         case bottomRight:
            g.drawImage(image, x + (int) (mapCenter.getX() - width / 2.) - (int) (size * 1.1) + size * 2,
                  y + (int) (mapCenter.getY() - height / 2. + size / 2.3),
                  width,
                  height, imageObserver);
            break;

         default:
            throw new Error("Unknown corner");
      }
   }

}

class BackgroundJob extends PaintingJob {

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      g.setColor(new Color(22, 145, 198));
      g.fillRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());
   }

}