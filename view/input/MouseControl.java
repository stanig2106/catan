package view.input;

import java.awt.*;
import java.util.Optional;

import Jama.Matrix;
import globalVariables.GameVariables;
import globalVariables.ViewVariables;
import util_my.Coord;
import util_my.Pair;
import util_my.directions.LandCorner;

/**
 * MouseControl
 */
public class MouseControl {

   public static Optional<Coord> positionToLandCoord(Point position, int landSize,
         Point mapCenter) {
      Matrix positionMatrix = new Matrix(
            new double[][] { { position.getX() - mapCenter.getX() },
                  { position.getY() - mapCenter.getY() } });
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
      else
         return Optional.empty();
   }

   public static Optional<Pair<Coord, LandCorner>> positionToCornerCoord(Point position, Dimension dimOfScreen,
         int landSize) {
      new Pair<Coord, LandCorner>(null, null);
      throw new Error("NI");
   }

}