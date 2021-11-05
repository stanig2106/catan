package map.constructions;

import java.util.HashMap;
import java.util.Map;

import map.Land;
import player.Player;
import util_my.directions.BuildingAdjacent;
import util_my.directions.LandCorner;
import util_my.directions.LandSide;

public abstract class Building extends Construction {
   final Land[] adjacentLands = new Land[BuildingAdjacent.values().length];

   Building(Player owner) {
      super(owner);
   }

   public static Map<LandSide, LandCorner> getNewLandLinks(LandCorner corner) {
      Map<LandSide, LandCorner> res = new HashMap<LandSide, LandCorner>();

      switch (corner) {
      case top:
         res.put(LandSide.topLeft, LandCorner.bottomRight);
         res.put(LandSide.topRight, LandCorner.bottomLeft);
         return res;
      case bottom:
         res.put(LandSide.bottomLeft, LandCorner.topLeft);
         res.put(LandSide.bottomRight, LandCorner.topRight);
         return res;
      case topLeft:
         res.put(LandSide.topLeft, LandCorner.bottom);
         res.put(LandSide.left, LandCorner.topRight);
         return res;
      case topRight:
         res.put(LandSide.topRight, LandCorner.bottom);
         res.put(LandSide.right, LandCorner.topLeft);
         return res;
      case bottomLeft:
         res.put(LandSide.bottomLeft, LandCorner.top);
         res.put(LandSide.left, LandCorner.bottomRight);
         return res;
      case bottomRight:
         res.put(LandSide.bottomRight, LandCorner.top);
         res.put(LandSide.right, LandCorner.bottomLeft);
         return res;
      default:
         throw new Error("unknown corner");
      }
   }
}
