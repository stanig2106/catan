package util_my.directions;

import util_my.Coord;

public enum LandSide {
   topLeft, topRight, right, left, bottomLeft, bottomRight;

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
}