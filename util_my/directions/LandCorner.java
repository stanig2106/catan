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

   public LandSide getLandSideClockwise() {
      switch (this) {
         case topLeft:
            return LandSide.topLeft;
         case top:
            return LandSide.topRight;
         case topRight:
            return LandSide.right;
         case bottomRight:
            return LandSide.bottomRight;
         case bottom:
            return LandSide.bottomLeft;
         case bottomLeft:
            return LandSide.left;
         default:
            throw new EnumConstantNotPresentException(this.getClass(), this.name());
      }
   }

   public LandSide getLandSideCounterClockwise() {
      switch (this) {
         case topLeft:
            return LandSide.left;
         case top:
            return LandSide.topLeft;
         case topRight:
            return LandSide.topRight;
         case bottomRight:
            return LandSide.right;
         case bottom:
            return LandSide.bottomRight;
         case bottomLeft:
            return LandSide.bottomLeft;
         default:
            throw new EnumConstantNotPresentException(this.getClass(), this.name());
      }
   }

   public LandSide[] getAdjacentsLandSides() {
      return new LandSide[] { this.getLandSideClockwise(), this.getLandSideCounterClockwise() };
   }

   public String toWeb() {
      return this.ordinal() + "";
   }

   public static LandCorner fromWeb(String s) {
      return LandCorner.values()[Integer.parseInt(s)];
   }
}