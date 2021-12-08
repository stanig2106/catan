package player;

import java.util.ArrayList;
import java.util.List;

import map.Land.BUILD;
import map.Land.BUILD.ROUTE_ON_ROUTE;
import map.constructions.Building;
import map.constructions.Route;
import map.ressources.Cost;
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
      if (!this.haveEnough(cost))
         throw new NOT_ENOUGH_RESSOURCES();
      this.ressources.pay(cost);
   }

   void play(Move move) throws NOT_ENOUGH_RESSOURCES, ROUTE_ON_ROUTE, BUILD {
      if (move instanceof Build) {
         ((Build<?>) move).pay();
         ((Build<?>) move).setConstruction();
      } else if (move instanceof LunchDices) {
         ((LunchDices) move).dicesResult();
      }
   }

   // class Exception
   public static class NOT_ENOUGH_RESSOURCES extends Exception {
      NOT_ENOUGH_RESSOURCES() {
         super();
      }
   }

}

class RealPlayer extends Player {
   void askMoveOnConsole() {
   }
}

class IA extends Player {

}