package map.constructions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import map.ressources.Ressources;
import map.Land;
import map.Land.LAND_DONT_PRODUCE;
import player.Player;
import util_my.directions.BuildingAdjacent;
import util_my.directions.LandCorner;
import util_my.directions.LandSide;

public abstract class Building extends Construction {
   final Map<BuildingAdjacent, Optional<Land>> adjacentLands = new HashMap<>() {
      {
         Stream.of(BuildingAdjacent.values())
               .forEach((buildingAdjacent -> this.put(buildingAdjacent, Optional.empty())));
      }
   };

   Building(Player owner) {
      super(owner);
   }

   public List<Ressources> getRessource(){
      List<Ressources> res = new ArrayList<Ressources>();
      this.adjacentLands.values().forEach((optionalLand) -> optionalLand.ifPresent((land) -> {
         try {
            res.addAll(land.getRessource());
         } catch (LAND_DONT_PRODUCE e) {
         }
      }));
      return res;
   }

   public static Map<LandSide, LandCorner> getLandLinks(LandCorner corner) {
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
