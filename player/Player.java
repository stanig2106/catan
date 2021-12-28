package player;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

import map.constructions.Building;
import map.constructions.Route;
import map.ressources.Cost;
import player.Inventory.NOT_ENOUGH_RESSOURCES;
import player.plays.Build;
import player.plays.LunchDices;
import player.plays.PlaceThief;
import player.plays.Play;

public abstract class Player {
   final public Inventory ressources = new Inventory();

   final List<Building> buildings = new ArrayList<Building>();
   final List<Route> routes = new ArrayList<Route>();

   static int playersCount = 0;

   public final PlayerColors color;
   public final int playerNumber = Player.playersCount++;

   public final String name = "hello world";

   Player() {
      this.color = PlayerColors.values()[playerNumber];
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

   public static enum PlayerColors {
      blue, green, yellow, purple, red, orange;

      public Color getColor() {
         switch (this) {
            case blue:
               return new Color(0, 174, 255);
            case green:
               return new Color(0, 147, 0);
            case yellow:
               return new Color(233, 189, 0);
            case purple:
               return new Color(163, 73, 255);
            case red:
               return new Color(252, 0, 45);
            case orange:
               return new Color(234, 125, 0);
            default:
               throw new EnumConstantNotPresentException(this.getClass(), this.name());
         }
      }
   }
}
