package player;

import java.util.ArrayList;
import java.util.List;

import map.Land.BUILD;
import map.Land.BUILD.ROUTE_ON_ROUTE;
import map.constructions.Building;
import map.constructions.Route;
import map.ressources.Cost;
import player.Inventory.NOT_ENOUGH_RESSOURCES;
import player.moves.Build;
import player.moves.LunchDices;
import player.moves.Move;

public abstract class Player {
   public Inventory ressources;

   final List<Building> buildings = new ArrayList<Building>(); 
   final List<Route> routes = new ArrayList<Route>(); 

   public boolean haveEnough(Cost cost) {
      return this.ressources.hasEnough(cost);
   }

   public void pay(Cost cost) throws NOT_ENOUGH_RESSOURCES {
      this.ressources.pay(cost);
   }

   void play(Move move) throws ROUTE_ON_ROUTE, BUILD, NOT_ENOUGH_RESSOURCES {
      if (move instanceof Build) {
         ((Build<?>) move).pay();
         ((Build<?>) move).setConstruction();
      } else if (move instanceof LunchDices) {
         ((LunchDices) move).dicesResult();
      }
   }


   public static class RealPlayer extends Player {
      void askMoveOnConsole() {
      }
   }
   
   public static class IA extends Player {
   
   }
}

