package player.moves;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import map.Land;
import map.constructions.City​​;
import map.constructions.Colony;
import map.constructions.Construction;
import map.constructions.Route;

import player.Player;
import util_my.directions.LandCorner;
import util_my.directions.LandSide;

interface BuildMove {
   public void setConstruction();
}

public abstract class Build<T extends Construction> extends Move implements BuildMove {
   public Map<Integer, Integer> cost;
   Function<Player, T> T_new;

   public Build(Player player, Map<Integer, Integer> cost, Function<Player, T> BuildedConstruction) {
      super(player);
      this.cost = cost;
      this.T_new = BuildedConstruction;
   }

   public boolean havePlayerEnoughRessource() {
      for (Entry<Integer, Integer> test : this.cost.entrySet())
         if (!this.player.haveEnough(test.getKey(), test.getValue()))
            return false;
      return true;
   }

   public void pay() {
      for (Entry<Integer, Integer> costEntry : this.cost.entrySet())
         this.player.pay(costEntry.getKey(), costEntry.getValue());
   }

   public T getConstruction() {
      return T_new.apply(this.player);
   };
}

class BuildRoute extends Build<Route> {
   Land position;
   LandSide positionSide;

   BuildRoute(Player player, Land position, LandSide positionSide) {
      super(player, Route.cost, Route::new);

      this.position = position;
      this.positionSide = positionSide;
   }

   public void setConstruction() {
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

   public void setConstruction() {
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

   public void setConstruction() {
      this.position.setBuilding(positionCorner, this.getConstruction());
   }
}
