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
}
