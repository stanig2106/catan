package util_my.directions;

import java.util.stream.Stream;

import javax.swing.JPanel;

import Jama.Matrix;
import map.constructions.Route;

import util_my.Coord;
import views.ViewContent;
import views.ViewVariables;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.*;

public enum LandSide {
   topLeft, topRight, right, left, bottomLeft, bottomRight;

   public static Stream<LandSide> stream() {
      return Stream.of(LandSide.values());
   }

   public Coord offsetCoord(Coord c) {
      switch (this) {
         case topLeft:
            return new Coord(c.x, c.y - 1);
         case topRight:
            return new Coord(c.x + 1, c.y - 1);
         case right:
            return new Coord(c.x + 1, c.y);
         case left:
            return new Coord(c.x - 1, c.y);
         case bottomLeft:
            return new Coord(c.x - 1, c.y + 1);
         case bottomRight:
            return new Coord(c.x, c.y + 1);
         default:
            throw new Error("Unknown side");
      }
   }

   public LandSide getOpposite() {
      switch (this) {
         case topLeft:
            return bottomRight;
         case topRight:
            return bottomLeft;
         case right:
            return left;
         case left:
            return right;
         case bottomLeft:
            return topRight;
         case bottomRight:
            return topLeft;
         default:
            throw new Error("Unknown side");
      }
   }

   public LandSide[] getAdjacent() {
      switch (this) {
         case topLeft:
            return new LandSide[] { topRight, left };
         case topRight:
            return new LandSide[] { topLeft, right };
         case right:
            return new LandSide[] { topRight, bottomRight };
         case left:
            return new LandSide[] { topLeft, bottomLeft };
         case bottomLeft:
            return new LandSide[] { bottomRight, left };
         case bottomRight:
            return new LandSide[] { bottomLeft, right };
         default:
            throw new Error("Unknown side");
      }
   }

   public LandSide getSideClockwise() {
      switch (this) {
         case topLeft:
            return topRight;
         case topRight:
            return right;
         case right:
            return bottomRight;
         case left:
            return topLeft;
         case bottomLeft:
            return left;
         case bottomRight:
            return bottomLeft;
         default:
            throw new Error("Unknown side");
      }
   }

   public LandSide getSideCounterClockwise() {
      switch (this) {
         case topLeft:
            return left;
         case topRight:
            return topLeft;
         case right:
            return topRight;
         case left:
            return bottomLeft;
         case bottomLeft:
            return bottomRight;
         case bottomRight:
            return right;
         default:
            throw new Error("Unknown side");
      }
   }

   public LandCorner getCornerClockwise() {
      switch (this) {
         case topLeft:
            return LandCorner.top;
         case topRight:
            return LandCorner.topRight;
         case right:
            return LandCorner.bottomRight;
         case left:
            return LandCorner.topLeft;
         case bottomLeft:
            return LandCorner.bottomLeft;
         case bottomRight:
            return LandCorner.bottom;
         default:
            throw new Error("Unknown side");
      }
   }

   public LandCorner getCornerCounterClockwise() {
      switch (this) {
         case topLeft:
            return LandCorner.topLeft;
         case topRight:
            return LandCorner.top;
         case right:
            return LandCorner.topRight;
         case left:
            return LandCorner.bottomLeft;
         case bottomLeft:
            return LandCorner.bottom;
         case bottomRight:
            return LandCorner.bottomRight;

         default:
            throw new Error("Unknown side");
      }
   }

   public void drawRouteOn(Graphics g, Coord coord, int dim, ViewContent canvas, Route route) {
      Matrix position = ViewVariables.basisMatrix.times(coord.toMatrix()).times(dim);
      int x = (int) position.get(0, 0);
      int y = (int) position.get(1, 0);
      int routeHeight = dim;
      int routeWidth = (int) (dim / 3.);

      Image routeImg = route.owner.routeImage;
      AffineTransform transform = new AffineTransform();

      switch (this) {
         case topLeft:
            transform.translate(x + (int) (canvas.getWidth() / 2. - routeWidth / 2.) - (int) (dim * 0.20),
                  y + (int) (canvas.getHeight() / 2. - routeHeight / 2.) - (int) (dim * 0.20));
            transform.rotate(Math.toRadians(60));
            break;
         case topRight:
            transform.translate(x + (int) (canvas.getWidth() / 2. - routeWidth / 2.) - (int) (dim * 0.30) + dim,
                  y + (int) (canvas.getHeight() / 2. - routeHeight / 2.) - (int) (dim * 0.20));
            transform.rotate(Math.toRadians(-60));
            break;
         case right:
            g.drawImage(routeImg, x + (int) (canvas.getWidth() / 2. - routeWidth / 2.) + (int) (dim * 0.87),
                  y + (int) (canvas.getHeight() / 2. - routeHeight / 2.),
                  routeWidth,
                  routeHeight, canvas);
            return;
         case left:
            g.drawImage(routeImg, x + (int) (canvas.getWidth() / 2. - routeWidth / 2.) - (int) (dim * 0.87),
                  y + (int) (canvas.getHeight() / 2. - routeHeight / 2.),
                  routeWidth,
                  routeHeight, canvas);

            return;
         case bottomLeft:
            transform.translate(
                  x + (int) (canvas.getWidth() / 2. - routeWidth / 2.) + (int) (dim * 0.70) - dim,
                  y + (int) (canvas.getHeight() / 2. - routeHeight / 2.) + (int) (dim * 1.3));
            transform.rotate(Math.toRadians(-60));
            break;
         case bottomRight:
            transform.translate(x + (int) (canvas.getWidth() / 2. - routeWidth / 2.) + (int) (dim * 0.70),
                  y + (int) (canvas.getHeight() / 2. - routeHeight / 2.) + (int) (dim * 1.3));
            transform.rotate(Math.toRadians(60));
            break;
         default:
            throw new Error("Unknown side");
      }

      transform.scale((double) routeWidth / routeImg.getWidth(null),
            (double) routeHeight / routeImg.getHeight(null));
      transform.translate(-routeImg.getWidth(canvas) / 2, -routeImg.getHeight(canvas) / 2);
      ((Graphics2D) g).drawImage(routeImg, transform, canvas);
   }
}