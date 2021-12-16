package player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import map.constructions.Building;
import map.constructions.Route;
import map.ressources.Cost;
import player.Inventory.NOT_ENOUGH_RESSOURCES;
import player.plays.Build;
import player.plays.LunchDices;
import player.plays.PlaceThief;
import player.plays.Play;

import java.awt.Image;

public abstract class Player {
   public Inventory ressources;

   final List<Building> buildings = new ArrayList<Building>();
   final List<Route> routes = new ArrayList<Route>();

   static int playersCount = 0;

   public final Color color;
   public final int playerNumber = Player.playersCount++;

   Player() {
      this.color = Color.values()[playerNumber];
   }

   public boolean haveEnough(Cost cost) {
      return this.ressources.hasEnough(cost);
   }

   public void pay(Cost cost) throws NOT_ENOUGH_RESSOURCES {
      this.ressources.pay(cost);
   }

   abstract public LunchDices askLunchDices();

   abstract public Build<?> askBuild();

   abstract public PlaceThief askPlaceThief();

   public static class RealPlayer extends Player {
      Play askPlayOnConsole() {
         throw new Error("NI");
      }

      @Override
      public LunchDices askLunchDices() {
         throw new Error("NI");
      }

      @Override
      public Build<?> askBuild() {
         throw new Error("NI");
      }

      @Override
      public PlaceThief askPlaceThief() {
         throw new Error("NI");
      }
   }

   public static class IA extends Player {

      @Override
      public LunchDices askLunchDices() {
         throw new Error("NI");
      }

      @Override
      public Build<?> askBuild() {
         throw new Error("NI");
      }

      @Override
      public PlaceThief askPlaceThief() {
         throw new Error("NI");
      }

   }

   public static enum Color {
      blue, green, yellow, purple, red, orange;
   }
}
