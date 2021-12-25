package player.plays;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import config.Config;
import globalVariables.GameVariables;
import map.CatanMap;
import map.Land;
import map.Land.BUILD;
import map.Land.BUILD.ROUTE_ON_ROUTE;
import map.constructions.City;
import map.constructions.Colony;
import map.constructions.Construction;
import map.constructions.Route;
import map.ressources.Cost;
import player.Player;
import player.Inventory.NOT_ENOUGH_RESSOURCES;
import util_my.HexagonalGrids.InvalidCoordinate;
import util_my.directions.LandCorner;
import util_my.directions.LandSide;

public abstract class Build<T extends Construction> extends Play {
   public Cost cost;
   Function<Player, T> T_new;

   public Build(Player player, Cost cost, Function<Player, T> BuildedConstruction) {
      super(player);
      this.cost = cost;
      this.T_new = BuildedConstruction;
   }

   protected boolean canPay() {
      return this.player.haveEnough(cost);
   }

   protected void pay() throws NOT_ENOUGH_RESSOURCES {
      this.player.pay(this.cost);
   }

   protected T getConstruction() {
      return T_new.apply(this.player);
   };

   public abstract void build() throws ROUTE_ON_ROUTE, BUILD;

   protected abstract void throwIfCanNotBuild() throws BUILD;

   @Override
   public void execute() throws NOT_ENOUGH_RESSOURCES, BUILD {
      Optional<NOT_ENOUGH_RESSOURCES> payException = Optional.empty();
      if (!this.canPay())
         payException = Optional.of(new NOT_ENOUGH_RESSOURCES());
      try {
         this.throwIfCanNotBuild();
      } catch (BUILD e) {
         payException.ifPresent(e2 -> e.addSuppressed(e2));
         throw e;
      }
      if (payException.isPresent())
         throw payException.get();

      this.pay();

      this.build();
   }

   public void forceExecute() throws ROUTE_ON_ROUTE, BUILD {
      this.build();
   }

   public static class BuildRoute extends Build<Route> {
      Land position;
      LandSide positionSide;

      public BuildRoute(Player player, Land position, LandSide positionSide) {
         super(player, Route.cost, Route::new);

         this.position = position;
         this.positionSide = positionSide;
      }

      @Override
      public void build() throws BUILD {
         this.position.setRoute(this.positionSide, this.getConstruction());
      }

      @Override
      protected boolean canPay() {
         return this.player.freeRoute > 0 || super.canPay();
      }

      @Override
      protected void pay() throws NOT_ENOUGH_RESSOURCES {
         if (this.player.freeRoute > 0)
            this.player.freeRoute--;
         else
            super.pay();
      }

      @Override
      protected void throwIfCanNotBuild() throws BUILD {
         this.position.throwIfCanNotSetRoute(this.positionSide, this.getConstruction());
      }
   }

   public static class BuildColony extends Build<Colony> {
      Land position;
      LandCorner positionCorner;

      public BuildColony(Player player, Land position, LandCorner positionCorner) {
         super(player, Colony.cost, Colony::new);

         this.position = position;
         this.positionCorner = positionCorner;
      }

      @Override
      protected boolean canPay() {
         return this.player.freeColony > 0 || super.canPay();
      }

      @Override
      protected void pay() throws NOT_ENOUGH_RESSOURCES {
         if (this.player.freeColony > 0)
            this.player.freeColony--;
         else
            super.pay();
      }

      @Override
      public void build() throws BUILD {
         this.position.setBuilding(positionCorner, this.getConstruction());
      }

      @Override
      protected void throwIfCanNotBuild() throws BUILD {
         this.position.throwIfCanNotSetBuilding(positionCorner, this.getConstruction());
      }

      @Override
      public void execute() throws NOT_ENOUGH_RESSOURCES, BUILD {
         super.execute();

         if (Config.serverMode == true) {
            throw new Error("server mode !");
         }

         if (GameVariables.turn != -1)
            return;
         this.position.getRessource().ifPresent(ressource -> this.player.inventory.add(ressource));
         Stream.of(this.positionCorner.getAdjacentsLandSides())
               .map(landSide -> {
                  try {
                     return GameVariables.map.get(landSide.offsetCoord(this.position.coord));
                  } catch (InvalidCoordinate _e) {
                     return null;
                  }
               }).filter(Objects::nonNull).forEach(land -> land.getRessource()
                     .ifPresent(ressource -> this.player.inventory.add(ressource)));

      }

      @Override
      public void forceExecute() throws ROUTE_ON_ROUTE, BUILD {
         super.forceExecute();
         if (GameVariables.turn != -1)
            return;
         this.position.getRessource().ifPresent(ressource -> this.player.inventory.add(ressource));
         Stream.of(this.positionCorner.getAdjacentsLandSides())
               .map(landSide -> {
                  try {
                     return GameVariables.map.get(landSide.offsetCoord(this.position.coord));
                  } catch (InvalidCoordinate _e) {
                     return null;
                  }
               }).filter(Objects::nonNull).forEach(land -> land.getRessource()
                     .ifPresent(ressource -> this.player.inventory.add(ressource)));
      }

      public void serverExecute(int turn, CatanMap map) throws NOT_ENOUGH_RESSOURCES, BUILD {
         super.execute();
         if (turn != -1)
            return;
         this.position.getRessource().ifPresent(ressource -> this.player.inventory.add(ressource));
         Stream.of(this.positionCorner.getAdjacentsLandSides())
               .map(landSide -> {
                  try {
                     return map.get(landSide.offsetCoord(this.position.coord));
                  } catch (InvalidCoordinate _e) {
                     return null;
                  }
               }).filter(Objects::nonNull).forEach(land -> land.getRessource()
                     .ifPresent(ressource -> this.player.inventory.add(ressource)));

      }
   }

   public static class BuildCity extends Build<City> {
      Land position;
      LandCorner positionCorner;

      public BuildCity(Player player, Land position, LandCorner positionCorner) {
         super(player, City.cost, City::new);

         this.position = position;
         this.positionCorner = positionCorner;
      }

      @Override
      public void build() throws BUILD {
         this.position.setBuilding(positionCorner, this.getConstruction());
      }

      @Override
      protected void throwIfCanNotBuild() throws BUILD {
         this.position.throwIfCanNotSetBuilding(positionCorner, this.getConstruction());
      }

   }
}
