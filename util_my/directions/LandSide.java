package util_my.directions;

import java.util.stream.Stream;

import util_my.Coord;

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
            throw new EnumConstantNotPresentException(this.getClass(), this.name());
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
            throw new EnumConstantNotPresentException(this.getClass(), this.name());
      }
   }

   public LandSide[] getAdjacentsSides() {
      return new LandSide[] { this.getSideClockwise(), this.getSideCounterClockwise() };
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
            throw new EnumConstantNotPresentException(this.getClass(), this.name());
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
            throw new EnumConstantNotPresentException(this.getClass(), this.name());
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
            throw new EnumConstantNotPresentException(this.getClass(), this.name());
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
            throw new EnumConstantNotPresentException(this.getClass(), this.name());
      }
   }

   public LandCorner[] getAdjacentsCorners() {
      return new LandCorner[] { this.getCornerClockwise(), this.getCornerCounterClockwise() };
   }

}