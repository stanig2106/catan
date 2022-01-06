package view.painting.jobs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import globalVariables.GameVariables;
import globalVariables.ViewVariables;
import map.Border;
import map.CatanMap;
import map.Corner;
import map.Land;
import map.constructions.Building;

import map.constructions.Route;
import util_my.Coord;
import util_my.DrawUtils;
import util_my.Matrix;
import util_my.Promise;
import util_my.Pair.Triple;
import util_my.directions.LandCorner;
import util_my.directions.LandSide;
import view.View;
import view.painting.Painting.PaintingJob;

public class CatanMapJob extends PaintingJob {
   public final BackgroundJob backgroundJob = new BackgroundJob();
   public static Map<Coord, Point> landsPosition;

   final View view;
   int landSize;
   Point mapCenter;

   // Optional<Pair<Pair<Coord, LandCorner>, Building>> shadowBuilding =
   // Optional.empty();
   private Optional<Triple<Coord, LandCorner, Building>> shadowBuilding = Optional.empty();
   private Optional<Triple<Coord, LandSide, Route>> shadowRoute = Optional.empty();
   private Optional<Coord> shadowRobber = Optional.empty();
   private boolean shadowChanged = false;

   public CatanMapJob(final View view) {
      this.view = view;
   }

   public void setShadow(Coord coord, LandCorner landCorner, Building building) {
      this.shadowChanged = true;
      this.shadowRoute = Optional.empty();
      this.shadowBuilding = Optional.of(Triple.tripleOf(coord, landCorner, building));
      this.shadowRobber = Optional.empty();
   }

   public void setShadow(Coord coord, LandSide landSide, Route route) {
      this.shadowChanged = true;
      this.shadowBuilding = Optional.empty();
      this.shadowRoute = Optional.of(Triple.tripleOf(coord, landSide, route));
      this.shadowRobber = Optional.empty();
   }

   public void setShadowRobber(Coord coord) {
      this.shadowChanged = true;
      this.shadowBuilding = Optional.empty();
      this.shadowRoute = Optional.empty();
      this.shadowRobber = Optional.of(coord);
   }

   public void removeShadow() {
      this.shadowChanged = true;
      this.shadowRoute = Optional.empty();
      this.shadowBuilding = Optional.empty();
      this.shadowRobber = Optional.empty();
   }

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      this.shadowChanged = false;

      this.landSize = view.getLandSize();
      this.mapCenter = view.getMapCenter();

      this.backgroundJob.paint(g, dim, imageObserver);
      new LandJob(this.landSize, this.mapCenter).paint(g, dim, imageObserver);
      new NumberJob(this.landSize, this.mapCenter).paint(g, dim, imageObserver);
      new RoberJob(this.landSize, this.mapCenter, shadowRobber).paint(g, dim, imageObserver);
      new RouteJob(this.landSize, this.mapCenter, shadowRoute).paint(g, dim, imageObserver);
      new BuildingJob(this.landSize, this.mapCenter, shadowBuilding).paint(g, dim, imageObserver);
   }

   @Override
   public boolean needReload() {
      return this.shadowChanged || this.landSize != view.getLandSize()
            || this.mapCenter.getX() != view.getMapCenter().getX()
            || this.mapCenter.getY() != view.getMapCenter().getY();
   }

   public static void init(CatanMap map) { // called after map creation
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
}

class LandJob extends PaintingJob {
   final int size;
   private final Point mapCenter;

   public LandJob(int size, Point mapCenter) {
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

      g.drawImage(CatanMap.backgroundImage.await(), (int) (mapCenter.getX() -
            11.56 * size / 2.0),
            (int) (mapCenter.getY() - 10.11 * size / 2.0),
            (int) (11.56 * size), (int) (10.11 * size), imageObserver);

      GameVariables.map.forEachCoordinate(coord -> {
         Matrix position = ViewVariables.hexToPixelMatrix.times(coord.toMatrix()).times(size);
         int x = (int) position.get(0, 0);
         int y = (int) position.get(1, 0);
         CatanMapJob.landsPosition.replace(coord,
               new Point(x + (int) mapCenter.getX(), y + (int) mapCenter.getY()));
         g.drawImage(GameVariables.map.get(coord).image.await(), x + (int) (mapCenter.getX() - width / 2.),
               y + (int) (mapCenter.getY() - height / 2.), width,
               height,
               imageObserver);
      });
   }

}

class NumberJob extends PaintingJob {
   private final int size;
   private final Point mapCenter;

   public NumberJob(int size, Point mapCenter) {
      this.size = size;
      this.mapCenter = mapCenter;
   }

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      g.setFont(ViewVariables.numberPileFont.deriveFont((float) (size * 0.75)));
      g.setColor(new Color(250, 233, 232));
      final String numbers = "__CDEFGHIJKLM";

      GameVariables.map.forEachCoordinate(coord -> {
         Matrix position = ViewVariables.hexToPixelMatrix.times(coord.toMatrix()).times(size);
         int x = (int) position.get(0, 0);
         int y = (int) position.get(1, 0);
         final int number;
         if ((number = GameVariables.map.get(coord).getNumber()) == -1)
            return;
         g.drawString(numbers.charAt(number) + "",
               x + (int) (mapCenter.getX() + (size * 0.75 * -0.60)),
               y + (int) (mapCenter.getY() + (size * 0.75 * 0.39)));
      });
   }

}

class RouteJob extends PaintingJob {
   final int size;
   private final Point mapCenter;
   final Optional<Triple<Coord, LandSide, Route>> shadowRoute;

   public RouteJob(int size, Point mapCenter, Optional<Triple<Coord, LandSide, Route>> shadowRoute) {
      this.size = size;
      this.mapCenter = mapCenter;
      this.shadowRoute = shadowRoute;
   }

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      List<Border> drawnBorders = new ArrayList<Border>();
      GameVariables.map.forEachCoordinate(coord -> {
         Land land = GameVariables.map.get(coord);
         LandSide.stream().forEach(landSide -> {
            Border border = land.borders.get(landSide);
            if (this.shadowRoute.map(shadowRoute -> {
               return shadowRoute.getA().equals(coord) && shadowRoute.getB().equals(landSide);
            }).orElse(false)) {
               drawnBorders.add(border);
               this.drawShadowRouteOn(g, coord, landSide, dim, shadowRoute.get().getC(), imageObserver);
            } else
               border.route.ifPresent(route -> {
                  if (drawnBorders.indexOf(border) == -1) {
                     this.drawRouteOn(g, coord, landSide, dim, route, imageObserver);
                     drawnBorders.add(border);
                  }
               });

         });
      });
   }

   void drawShadowRouteOn(Graphics2D g, Coord coord, LandSide side, Dimension dim, Route route,
         ImageObserver imageObserver) {
      final Composite defaultComposite = g.getComposite();
      g.setComposite(AlphaComposite.SrcOver.derive(0.7f));
      this.drawRouteOn(g, coord, side, dim, route, imageObserver);
      g.setComposite(defaultComposite);
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
            transform.translate(x + (int) (mapCenter.getX() - routeWidth / 2.) - (int) (size * 0.17 + size * 0.10),
                  y + (int) (mapCenter.getY() - routeHeight / 2.) - (int) (size * 0.20));
            transform.rotate(Math.toRadians(60));
            break;
         case topRight:
            transform.translate(x + (int) (mapCenter.getX() - routeWidth / 2.) - (int) (size * 0.45) + size,
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
            transform.translate(x + (int) (mapCenter.getX() - routeWidth / 2.) + (int) (size * 0.70 - size * 0.10),
                  y + (int) (mapCenter.getY() - routeHeight / 2.) + (int) (size * 1.3));
            transform.rotate(Math.toRadians(60));
            break;
         default:
            throw new Error("Unknown side");
      }

      transform.scale((double) routeWidth / routeImg.getWidth(imageObserver),
            (double) routeHeight / routeImg.getHeight(imageObserver));
      transform.translate(-routeImg.getWidth(imageObserver) / 2.,
            -routeImg.getHeight(imageObserver) / 2.);
      g.drawImage(routeImg, transform, imageObserver);
   }

}

class BuildingJob extends PaintingJob {
   final int size;
   private final Point mapCenter;
   final Optional<Triple<Coord, LandCorner, Building>> shadowBuilding;

   public BuildingJob(int size, Point mapCenter, Optional<Triple<Coord, LandCorner, Building>> shadowBuilding) {
      this.size = size;
      this.mapCenter = mapCenter;
      this.shadowBuilding = shadowBuilding;
   }

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      List<Corner> drawnCorners = new ArrayList<Corner>();
      GameVariables.map.forEachCoordinate(coord -> {
         Land land = GameVariables.map.get(coord);
         LandCorner.stream().forEach(landCorner -> {
            Corner corner = land.corners.get(landCorner);
            if (this.shadowBuilding.map(shadowBuilding -> {
               return shadowBuilding.getA().equals(coord) && shadowBuilding.getB().equals(landCorner);
            }).orElse(false)) {
               drawnCorners.add(corner); // sometimes, colony before shadow city
               this.drawShadowBuildingOn(g, coord, landCorner, dim, shadowBuilding.get().getC(), imageObserver);
            } else
               corner.building.ifPresent(building -> {
                  if (drawnCorners.indexOf(corner) == -1) {
                     this.drawBuildingOn(g, coord, landCorner, dim, building, imageObserver);
                     drawnCorners.add(corner);
                  }
               });
         });
      });

   }

   void drawShadowBuildingOn(Graphics2D g, Coord coord, LandCorner corner, Dimension dim, Building building,
         ImageObserver imageObserver) {
      final Composite defaultComposite = g.getComposite();
      g.setComposite(AlphaComposite.SrcOver.derive(0.7f));
      this.drawBuildingOn(g, coord, corner, dim, building, imageObserver);
      g.setComposite(defaultComposite);
   }

   protected void drawBuildingOn(Graphics2D g, Coord coord, LandCorner corner, Dimension dim, Building building,
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
                  y + (int) (mapCenter.getY() - height / 2. - size / 1.9),
                  width,
                  height, imageObserver);
            break;
         case topRight:
            g.drawImage(image, x + (int) (mapCenter.getX() - width / 2.) - (int) (size * 1.15) + size * 2,
                  y + (int) (mapCenter.getY() - height / 2. - size / 1.9),
                  width,
                  height, imageObserver);
            break;
         case bottomLeft:
            g.drawImage(image, x + (int) (mapCenter.getX() - width / 2.) - (int) (size * 0.87),
                  y + (int) (mapCenter.getY() - height / 2. + size / 1.9),
                  width,
                  height, imageObserver);
            break;
         case bottomRight:
            g.drawImage(image, x + (int) (mapCenter.getX() - width / 2.) - (int) (size * 1.15) + size * 2,
                  y + (int) (mapCenter.getY() - height / 2. + size / 1.9),
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

class RoberJob extends PaintingJob {
   final static Promise<Image> robberImage = ViewVariables.importImage("assets/Robber.png");
   final int landSize;
   final Point mapCenter;
   final Coord robber;
   final Optional<Coord> shadowRobber;

   public RoberJob(int landSize, Point mapCenter, Optional<Coord> shadowRobber) {
      this.landSize = landSize;
      this.mapCenter = mapCenter;
      this.robber = GameVariables.map.robber.position.coord;
      this.shadowRobber = shadowRobber;
   }

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      Coord coord = GameVariables.map.robber.position.coord;
      Matrix position = ViewVariables.hexToPixelMatrix.times(coord.toMatrix()).times(landSize);
      int x = (int) position.get(0, 0);
      int y = (int) position.get(1, 0);

      DrawUtils.drawCenteredImage(g, robberImage.await(), landSize * .75, landSize * .75,
            new Rectangle(x + mapCenter.x - (int) (landSize * 0.10), y + mapCenter.y - (int) (landSize * 0.10),
                  (int) (landSize * .75), (int) (landSize * .75)),
            imageObserver);

      //

      if (this.shadowRobber.isEmpty())
         return;
      coord = this.shadowRobber.get();
      position = ViewVariables.hexToPixelMatrix.times(coord.toMatrix()).times(landSize);
      x = (int) position.get(0, 0);
      y = (int) position.get(1, 0);
      final Composite defaultComposite = g.getComposite();
      g.setComposite(AlphaComposite.SrcOver.derive(0.8f));
      DrawUtils.drawCenteredImage(g, robberImage.await(), landSize * .75, landSize * .75,
            new Rectangle(x + mapCenter.x - (int) (landSize * 0.10), y + mapCenter.y - (int) (landSize * 0.10),
                  (int) (landSize * .75), (int) (landSize * .75)),
            imageObserver);
      g.setComposite(defaultComposite);

   }

}