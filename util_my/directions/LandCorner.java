package util_my.directions;

import java.util.stream.Stream;

public enum LandCorner {
   top, topLeft, topRight, bottomLeft, bottomRight, bottom;

   public static Stream<LandCorner> stream() {
      return Stream.of(LandCorner.values());
   }
}