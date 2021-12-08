package map.constructions;

import java.util.Optional;

import map.ressources.Cost;
import map.ressources.Ressources;
import player.Player;

public class Route extends Construction {
   public Optional<Building> adjacentsBuildingClockwise;
   public Optional<Building> adjacentsBuildingCounterClockwise;
   public Optional<Route> adjacentsRouteClockwise;
   public Optional<Route> adjacentsRouteCounterClockwise;

   public final static Cost cost = new Cost();

   public Route(Player owner) {
      super(owner);

      Route.cost.set(Ressources.Brick, 1);
      Route.cost.set(Ressources.Lumber, 1);
   }
}
