package util_my.directions;

import java.util.stream.Stream;

public enum LandCorner {
   top, topLeft, topRight, bottomLeft, bottomRight, bottom;

   public static Stream<LandCorner> stream() {
      return Stream.of(LandCorner.values());
   }

   public LandCorner getCornerClockwise() {
      switch (this) {
         case topLeft:
            return top;
         case top:
            return topRight;
         case topRight:
            return bottomRight;
         case bottomRight:
            return bottom;
         case bottom:
            return bottomRight;
         case bottomLeft:
            return topLeft;
         default:
            throw new EnumConstantNotPresentException(this.getClass(), this.name());
      }
   }

   public LandCorner getCornerCounterClockwise() {
      switch (this) {
         case topLeft:
            return bottomLeft;
         case top:
            return topLeft;
         case topRight:
            return top;
         case bottomRight:
            return topRight;
         case bottom:
            return bottomRight;
         case bottomLeft:
            return bottom;
         default:
            throw new EnumConstantNotPresentException(this.getClass(), this.name());
      }
   }

   public LandCorner[] getAdjacentsCorners() {
      return new LandCorner[] { this.getCornerClockwise(), this.getCornerCounterClockwise() };
   }

}