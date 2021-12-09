package player.moves;

import java.util.function.Function;

import map.Land;
import map.Land.BUILD;
import map.Land.BUILD.ROUTE_ON_ROUTE;
import map.constructions.City​​;
import map.constructions.Colony;
import map.constructions.Construction;
import map.constructions.Route;
import map.ressources.Cost;
import player.Player;
import util_my.directions.LandCorner;
import util_my.directions.LandSide;

public abstract class Build<T extends Construction> extends Move {
   public Cost cost;
   Function<Player, T> T_new;

   public Build(Player player, Cost cost, Function<Player, T> BuildedConstruction) {
      super(player);
      this.cost = cost;
      this.T_new = BuildedConstruction;
   }

   public void pay() throws player.Inventory.NOT_ENOUGH_RESSOURCES {
      this.player.pay(this.cost);
   }

   public T getConstruction() {
      return T_new.apply(this.player);
   };

   public abstract void setConstruction() throws ROUTE_ON_ROUTE, BUILD;
}

class BuildRoute extends Build<Route> {
   Land position;
   LandSide positionSide;

   BuildRoute(Player player, Land position, LandSide positionSide) {
      super(player, Route.cost, Route::new);

      this.position = position;
      this.positionSide = positionSide;
   }

   public void setConstruction() throws ROUTE_ON_ROUTE {
      this.position.setRoute(this.positionSide, this.getConstruction());
   }
}

class BuildColony extends Build<Colony> {
   Land position;
   LandCorner positionCorner;

   BuildColony(Player player, Land position, LandCorner positionCorner) {
      super(player, Colony.cost, Colony::new);

      this.position = position;
      this.positionCorner = positionCorner;
   }

   public void setConstruction() throws BUILD {
      this.position.setBuilding(positionCorner, this.getConstruction());
      
   }
}

class BuildCity extends Build<City​​> {
   Land position;
   LandCorner positionCorner;

   BuildCity(Player player, Land position, LandCorner positionCorner) {
      super(player, City​​.cost, City​​::new);

      this.position = position;
      this.positionCorner = positionCorner;
   }

   public void setConstruction() throws BUILD {
      this.position.setBuilding(positionCorner, this.getConstruction());
   }
}
