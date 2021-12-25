package player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.awt.*;

import map.constructions.Building;
import map.constructions.Route;
import map.ressources.Cost;
import player.Inventory.NOT_ENOUGH_RESSOURCES;

public abstract class Player {
   public final Inventory inventory = new Inventory();

   public final LinkedList<Building> buildings = new LinkedList<Building>();
   public final LinkedList<Route> routes = new LinkedList<Route>();

   static int playersCount = 0;

   public final PlayerColors color;
   public final int playerNumber = Player.playersCount++;

   protected String name = "Player " + (int) (playerNumber + 1);

   public int freeRoute = 0;
   public int freeColony = 0;

   Player() {
      this.color = PlayerColors.values()[playerNumber];
   }

   public boolean haveEnough(Cost cost) {
      return this.inventory.hasEnough(cost);
   }

   public void pay(Cost cost) throws NOT_ENOUGH_RESSOURCES {
      this.inventory.pay(cost);
   }

   public String getName() {
      return name;
   }

   public static class RealPlayer extends Player {
      public void setName(String name) {
         this.name = name;
      }
   }

   public static class IA extends Player {

   }

   //

   //

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
