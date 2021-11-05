package map.constructions;

import java.util.HashMap;
import java.util.Map;

import map.ressources.Brick;
import map.ressources.Lumber;
import player.Player;

public class Route extends Construction {
   public Building adjacentsBuildingClockwise;
   public Building adjacentsBuildingCounterClockwise;
   public Route adjacentsRouteClockwise;
   public Route adjacentsRouteCounterClockwise;

   public final static Map<Integer, Integer> cost = new HashMap<Integer, Integer>();

   public Route(Player owner) {
      super(owner);

      Route.cost.put(Brick.ressourceCode, 1);
      Route.cost.put(Lumber.ressourceCode, 1);
   }
}
