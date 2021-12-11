package map.constructions;

import java.util.Optional;

import map.ressources.Cost;
import player.Player;

public class Route extends Construction {
   public Optional<Building> adjacentsBuildingClockwise;
   public Optional<Building> adjacentsBuildingCounterClockwise;
   public Optional<Route> adjacentsRouteClockwise;
   public Optional<Route> adjacentsRouteCounterClockwise;

   public final static Cost cost = new Cost(1, 0, 1, 0, 0);

   public Route(Player owner) {
      super(owner);
   }
}
