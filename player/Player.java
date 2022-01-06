package player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import globalVariables.GameVariables;

import java.awt.*;

import map.constructions.Building;
import map.constructions.City;
import map.constructions.Route;
import map.ressources.Cost;
import online.Online;
import online.Request;
import player.Inventory.NOT_ENOUGH_RESSOURCES;
import player.developmentCards.Card;
import util_my.Pair;
import util_my.Param;
import util_my.Params;
import util_my.StreamUtils;

public abstract class Player {
   public Inventory inventory = new Inventory();

   public final LinkedList<Building> buildings = new LinkedList<Building>();
   public final LinkedList<Route> routes = new LinkedList<Route>();

   static int playersCount = 0;

   public final PlayerColors color;

   final protected String name;

   final public int id;
   public int freeRoute = 0;
   public int freeColony = 0;
   private int robberCount = 0;

   Player(String name, int id) {
      this.name = name;
      this.id = id;
      this.color = PlayerColors.values()[id];
   }

   public boolean canBuyCard() {
      return this.haveEnough(Card.cost);
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

   public void addCard(Card card) {
      this.inventory.cards.add(Pair.of(card, false));
   }

   public void updateCards() {
      this.inventory.cards = this.inventory.cards.stream()
            .map(pair -> pair.mapValue((card, playable) -> card != Card.Library))
            .collect(Collectors.toList());
   }

   public int getPublicScore() {
      return buildings.stream().mapToInt(building -> (building instanceof City ? 2 : 1)).sum() +
            (Stream.of(GameVariables.players).collect(StreamUtils
                  .maxSupCollector((Player p1, Player p2) -> Integer.compare(p1.getRobberCount(), p2.getRobberCount())))
                  .map(player -> player.id == this.id).orElse(false) ? 1 : 0);
   }

   public int getScore() {
      return this.getPublicScore()
            + (int) this.inventory.cards.stream().filter(card -> card.getKey().equals(Card.Library)).count();
   }

   public int getRobberCount() {
      return robberCount;
   }

   public void incrRobberCount() {
      this.robberCount++;
   }

   public static class Me extends Player {

      public Me(String name, int id) {
         super(name, id);
      }
   }

   public static class Server extends Player {
      public final UUID uuid = UUID.randomUUID();
      private final Deque<String> waitedPlays = new LinkedList<String>();

      public Server(String name, int id) {
         super(name, id);
      }

      public void addWaitedPlays(String play) {
         this.waitedPlays.add(play);
      }

      public Optional<String> popWaitedPlay() {
         if (this.waitedPlays.isEmpty())
            return Optional.empty();
         return Optional.of(this.waitedPlays.removeFirst());
      }

   }

   public static class Online extends Player {

      public Online(String name, int id) {
         super(name, id);
         player.Player.Online me = this;
         this.inventory = new Inventory() {
            public int getTotal() {
               String res = new Request(online.Online.url + "/ressources", new Param("id", "" + me.id),
                     new Param("room", online.Online.joinedRoom.get().uuid.toString())).send().await();
               return Integer.parseInt(Params.parse(res).get("count").get());
            };

            @Override
            public int getCardsCount() {
               String res = new Request(online.Online.url + "/cardsCount", new Param("id", "" + me.id),
                     new Param("room", online.Online.joinedRoom.get().uuid.toString())).send().await();
               return Integer.parseInt(Params.parse(res).get("count").get());
            }
         };
      }

      @Override
      public int getRobberCount() {
         String res = new Request(online.Online.url + "/robberCount", new Param("id", "" + this.id),
               new Param("room", online.Online.joinedRoom.get().uuid.toString())).send().await();
         return Integer.parseInt(Params.parse(res).get("count").get());
      }

   }

   public static class IA extends Player {

      IA(String name, int id) {
         super(name, id);
      }

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
