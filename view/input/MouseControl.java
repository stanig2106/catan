package view.input;

import java.awt.*;
import java.util.Optional;

import Jama.Matrix;
import globalVariables.GameVariables;
import globalVariables.ViewVariables;
import map.CataneMap;
import util_my.Box;
import util_my.Coord;
import util_my.HexagonalGrids;
import util_my.Line;
import util_my.Pair;
import util_my.directions.LandCorner;
import util_my.directions.LandSide;
import view.painting.jobs.CataneMapJob;

/**
 * MouseControl
 */
public class MouseControl {

   private static Optional<Coord> positionToLandCoord(Point mousePosition, int landSize,
         Point mapCenter) {
      Matrix positionMatrix = new Matrix(
            new double[][] { { mousePosition.getX() - mapCenter.getX() },
                  { mousePosition.getY() - mapCenter.getY() } });
      Matrix coordMatrix = ViewVariables.PixelToHexMatrix.times(positionMatrix).times(1. / landSize);
      double q = coordMatrix.get(0, 0);
      double r = coordMatrix.get(1, 0);
      double s = -q - r;
      long rounded_q = Math.round(q);
      long rounded_r = Math.round(r);
      long rounded_s = Math.round(s);
      double diff_q = Math.abs(q - rounded_q);
      double diff_r = Math.abs(r - rounded_r);
      double diff_s = Math.abs(s - rounded_s);
      if (diff_q > diff_r && diff_q > diff_s)
         rounded_q = -rounded_r - rounded_s;
      else if (diff_r > diff_s)
         rounded_r = -rounded_q - rounded_s;
      else
         rounded_s = -rounded_q - rounded_r;
      int x = (int) rounded_q;
      int y = (int) rounded_r;
      Coord res = new Coord(x, y);
      if (GameVariables.map.isValidCoordinate(res))
         return Optional.of(res);
      else {
         Box<Optional<Pair<Coord, Double>>> nearestCoord = Box.of(Optional.empty());
         GameVariables.map.forEachCoordinate(coord -> {
            if (!CataneMap.isCoordAdjacentCoord(res, coord))
               return;
            Point center = CataneMapJob.landsPosition.get(coord);
            Line line = new Line(center, mousePosition);
            if (line.distance() < 1.10 * landSize
                  && (nearestCoord.data.isEmpty() || nearestCoord.data.get().getValue() > line.distance()))
               nearestCoord.data = Optional.of(new Pair<Coord, Double>(coord, line.distance()));
         });
         return nearestCoord.data.map(nearestPair -> nearestPair.getKey());
      }

   }

   public static MousePositionSummary getMousePositionSummary(Point mousePosition, int landSize,
         Point mapCenter) {
      Optional<Coord> coord = positionToLandCoord(mousePosition, landSize, mapCenter);
      if (coord.isEmpty())
         return new MousePositionSummary();

      Point center = CataneMapJob.landsPosition.get(coord.get());
      Line line = new Line(center, mousePosition);

      if (!(line.distance() > 0.70 * landSize))
         return new MousePositionSummary(coord.get());

      Double angle = Line.abscise().angleV1(line);

      LandCorner[] landCorners = { LandCorner.topRight, LandCorner.top, LandCorner.topLeft, LandCorner.bottomLeft,
            LandCorner.bottom, LandCorner.bottomRight };
      if (angle % 60 > 15 && angle % 60 < 45) // Corner
         return new MousePositionSummary(coord.get(), landCorners[(int) Math.floor(angle / 60)]);

      LandSide[] landSides = { LandSide.right, LandSide.topRight, LandSide.topLeft, LandSide.left, LandSide.bottomLeft,
            LandSide.bottomRight };
      return new MousePositionSummary(coord.get(), landSides[(int) Math.floor(angle / 60 + 0.5) % 6]);

   }

   public static class MousePositionSummary {
      public final Optional<Coord> nearestLandCoord;
      public final Optional<LandSide> nearestLandSide;
      public final Optional<LandCorner> nearestLandCorner;

      MousePositionSummary() {
         this.nearestLandCoord = Optional.empty();
         this.nearestLandSide = Optional.empty();
         this.nearestLandCorner = Optional.empty();
      }

      MousePositionSummary(Coord nearestLandCoord) {
         this.nearestLandCoord = Optional.of(nearestLandCoord);
         this.nearestLandSide = Optional.empty();
         this.nearestLandCorner = Optional.empty();
      }

      MousePositionSummary(Coord nearestLandCoord, LandSide nearestLandSide) {
         this.nearestLandCoord = Optional.of(nearestLandCoord);
         this.nearestLandSide = Optional.of(nearestLandSide);
         this.nearestLandCorner = Optional.empty();
      }

      MousePositionSummary(Coord nearestLandCoord, LandCorner nearestLandCorner) {
         this.nearestLandCoord = Optional.of(nearestLandCoord);
         this.nearestLandSide = Optional.empty();
         this.nearestLandCorner = Optional.of(nearestLandCorner);
      }
   }
}