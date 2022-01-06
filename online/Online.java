package online;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import globalVariables.GameVariables;
import map.CatanMap;
import map.ressources.Cost;
import map.ressources.Ressources;
import online.onlinePlays.OBuild;
import online.onlinePlays.ODices;
import online.onlinePlays.ODone;
import online.onlinePlays.OPlayCard;
import player.Player;
import player.developmentCards.Card;
import util_my.Coord;
import util_my.Param;
import util_my.Params;
import util_my.Promise;
import util_my.Timeout;
import util_my.directions.LandCorner;
import util_my.directions.LandSide;

public final class Online {
   public static String url = "http://localhost:8080";
   public static String publicName = "helloName";
   public static Optional<UUID> personalUuid = Optional.empty();
   public static Optional<UUID> joinedRoomUuid = Optional.empty();
   public static Optional<Room> joinedRoom = Optional.empty();
   public static Room[] rooms = new Room[0];

   private Online() {
   }

   private static boolean notLogged(Consumer<Exception> reject) {
      if (personalUuid.isEmpty() || joinedRoomUuid.isEmpty()) {
         reject.accept(new Exception("not logged"));
         return true;
      }
      return false;
   }

   public static Promise<Void> downloadRooms() {
      return new Promise<Void>((resolve, reject) -> {
         String res = new Request(url + "/rooms").send()
               .await();
         Params resParams = Params.parse(res);

         if (resParams.get("error").isPresent()) {
            reject.accept(new Exception(resParams.get("error").get()));
            return;
         }

         rooms = Room.fromWeb(res);
         resolve.accept(null);
      });
   }

   public static Promise<Void> createRoom(String nameRoom) {
      return joinOrCreateRoom("create", nameRoom);

   }

   public static Promise<Void> joinRoom(UUID uuidOfRoom) {
      return joinOrCreateRoom("join", uuidOfRoom.toString());
   }

   private static Promise<Void> joinOrCreateRoom(String action, String value) {
      return new Promise<Void>((resolve, reject) -> {
         String res = new Request(url + "/room", new Param("player", publicName),
               new Param(action, value)).send()
                     .await();
         Params resParams = Params.parse(res);

         if (resParams.get("error").isPresent()) {
            reject.accept(new Exception(resParams.get("error").get()));
            return;
         }

         personalUuid = Optional.of(UUID.fromString(resParams.get("playerUUID").get()));
         joinedRoomUuid = Optional.of(UUID.fromString(resParams.get("roomUUID").get()));

         updateRoomJoined().await();
         resolve.accept(null);
      });
   }

   public static Promise<Void> updateRoomJoined() {
      return new Promise<Void>((resolve, reject) -> {
         if (joinedRoomUuid.isEmpty() || personalUuid.isEmpty()) {
            reject.accept(new Exception("not logged"));
            return;
         }
         String res = new Request(url + "/rooms", new Param("of", joinedRoomUuid.get().toString()),
               new Param("sender", personalUuid.get().toString())).send()
                     .await();
         Params resParams = Params.parse(res);

         if (resParams.get("error").isPresent()) {
            reject.accept(new Exception(resParams.get("error").get()));
            return;
         }

         Room[] rooms = Room.fromWeb(res);
         joinedRoom = rooms.length > 0 ? Optional.of(rooms[0]) : Optional.empty();

         if (joinedRoom.isEmpty()) {
            joinedRoomUuid = Optional.empty();
            personalUuid = Optional.empty();
         } else
            GameVariables.players = joinedRoom.get().players;

         resolve.accept(null);
      });
   }

   public static Promise<Void> downloadMap() {
      return new Promise<Void>((resolve, reject) -> {
         if (personalUuid.isEmpty()) {
            reject.accept(new Exception("not logged"));
            return;
         }
         String res = new Request(url + "/map", new Param("sender", personalUuid.get().toString())).send()
               .await();
         Params resParams = Params.parse(res);

         if (resParams.get("error").isPresent()) {
            reject.accept(new Exception(resParams.get("error").get()));
            return;
         }

         GameVariables.map = CatanMap.fromWeb(res);
         resolve.accept(null);
      });
   }

   public static Promise<Void> startGame() {
      return new Promise<Void>((resolve, reject) -> {
         if (personalUuid.isEmpty() || joinedRoomUuid.isEmpty()) {
            reject.accept(new Exception("not logged"));
            return;
         }

         final String res = new Request(url + "/start",
               new Param("sender", personalUuid.get().toString()),
               new Param("room", joinedRoomUuid.get().toString())).send()
                     .await();

         final Params resParams = Params.parse(res);

         if (resParams.get("error").isPresent()) {
            reject.accept(new Exception(resParams.get("error").get()));
            return;
         }

         resolve.accept(null);
      });
   }

   public static Promise<Void> waitGameStarted() {
      return new Promise<Void>((resolve, reject) -> {
         if (notLogged(reject))
            return;
         Params resParams = new Params(new Param("wait"));
         while (resParams.contain("wait")) {
            Online.updateRoomJoined();
            new Timeout(1000).join();

            final String res = new Request(url + "/started",
                  new Param("sender", personalUuid.get().toString()),
                  new Param("room", joinedRoomUuid.get().toString())).send()
                        .await();

            resParams = Params.parse(res);

            if (resParams.get("error").isPresent()) {
               reject.accept(new Exception(resParams.get("error").get()));
               return;
            }
         }
         resolve.accept(null);
      });
   }

   public static Promise<Void> lunchDices() {
      return new Promise<Void>((resolve, reject) -> {
         if (notLogged(reject))
            return;

         final String res = new Request(url + "/dices",
               new Param("sender", personalUuid.get().toString()),
               new Param("room", joinedRoomUuid.get().toString())).send()
                     .await();

         final Params resParams = Params.parse(res);

         if (resParams.get("error").isPresent()) {
            reject.accept(new Exception(resParams.get("error").get()));
            return;
         }

         ODices.exec(Integer.parseInt(resParams.get("dice1").get()),
               Integer.parseInt(resParams.get("dice2").get()));
         resolve.accept(null);
      });
   }

   public static Promise<Void> done() {
      return new Promise<Void>((resolve, reject) -> {
         if (notLogged(reject))
            return;

         final String res = new Request(url + "/done",
               new Param("sender", personalUuid.get().toString()),
               new Param("room", joinedRoomUuid.get().toString())).send()
                     .await();

         final Params resParams = Params.parse(res);

         if (resParams.get("error").isPresent()) {
            reject.accept(new Exception(resParams.get("error").get()));
            return;
         }

         ODone.exec(Integer.parseInt(resParams.get("newTurn").get()),
               Integer.parseInt(resParams.get("player").get()));
         resolve.accept(null);
      });
   }

   public static Promise<Void> watchPlays() {

      return new Promise<Void>((resolve, reject) -> {
         if (notLogged(reject))
            return;
         Params resParams = new Params(new Param("wait"));
         while (!resParams.contain("newTurn")) {
            new Timeout(1000).join();

            final String res = new Request(url + "/wait",
                  new Param("sender", personalUuid.get().toString()),
                  new Param("room", joinedRoomUuid.get().toString())).send()
                        .await();

            resParams = Params.parse(res);

            if (resParams.get("error").isPresent()) {
               reject.accept(new Exception(resParams.get("error").get()));
               return;
            }

            if (resParams.contain("dice1"))
               ODices.exec(Integer.parseInt(resParams.get("dice1").get()),
                     Integer.parseInt(resParams.get("dice2").get()));

            if (resParams.contain("buildType"))
               OBuild.exec(resParams.get("buildType").get(), Coord.fromWeb(resParams.get("coord").get()),
                     resParams.get("position").get());

            if (resParams.contain("steal"))
               GameVariables.getMe().inventory.minus(Ressources.fromWeb(resParams.get("steal").get()));

            if (resParams.contain("stealAll")) {
               GameVariables.getMe().inventory.set(Ressources.fromWeb(resParams.get("steal").get()), 0);
               System.out.println("steal all !");
            }

            if (resParams.contain("moveRobber"))
               GameVariables.map.robber.position = GameVariables.map
                     .get(Coord.fromWeb(resParams.get("moveRobber").get()));

            GameVariables.view.backgroundPainting.forceUpdatePainting().await();
            GameVariables.view.background.repaint();
         }

         ODone.exec(Integer.parseInt(resParams.get("newTurn").get()),
               Integer.parseInt(resParams.get("player").get()));

         resolve.accept(null);
      });
   }

   public static Promise<Void> buildRoute(Coord coord, LandSide side) {
      return build("route", coord, side.toWeb());
   }

   public static Promise<Void> buildColony(Coord coord, LandCorner corner) {
      return build("colony", coord, corner.toWeb());
   }

   public static Promise<Void> buildCity(Coord coord, LandCorner corner) {
      return build("city", coord, corner.toWeb());
   }

   private static Promise<Void> build(String type, Coord coord, String cornerOrSide) {
      return new Promise<Void>((resolve, reject) -> {
         if (notLogged(reject))
            return;

         OBuild.exec(type, coord, cornerOrSide);

         String res = new Request(url + "/build",
               new Param("sender", personalUuid.get().toString()),
               new Param("room", joinedRoomUuid.get().toString()),
               new Param("buildType", type),
               new Param("coord", coord.toWeb()),
               new Param("position", cornerOrSide)).send()
                     .await();
         Params resParams = Params.parse(res);

         if (resParams.get("error").isPresent()) {
            reject.accept(new Exception(resParams.get("error").get()));
            return;
         }

      });
   }

   public static Promise<Void> buyCard() {
      return new Promise<Void>((resolve, reject) -> {
         if (notLogged(reject))
            return;

         final String res = new Request(url + "/buyCard",
               new Param("sender", personalUuid.get().toString()),
               new Param("room", joinedRoomUuid.get().toString())).send()
                     .await();

         final Params resParams = Params.parse(res);

         if (resParams.get("error").isPresent()) {
            reject.accept(new Exception(resParams.get("error").get()));
            return;
         }

         GameVariables.playerToPlay.addCard(Card.fromWeb(resParams.get("card").get()));
         resolve.accept(null);
      });
   }

   public static Promise<Void> playCard(int indexOfCard) {
      return new Promise<Void>((resolve, reject) -> {
         if (notLogged(reject))
            return;

         final String res = new Request(url + "/playCard",
               new Param("sender", personalUuid.get().toString()),
               new Param("room", joinedRoomUuid.get().toString()),
               new Param("index", "" + indexOfCard)).send()
                     .await();

         final Params resParams = Params.parse(res);

         if (resParams.get("error").isPresent()) {
            reject.accept(new Exception(resParams.get("error").get()));
            return;
         }

         OPlayCard.selfExec(indexOfCard);
         resolve.accept(null);
      });
   }

   public static Promise<Void> steal(int idOfPlayer) {
      return new Promise<Void>((resolve, reject) -> {
         if (notLogged(reject))
            return;

         final String res = new Request(url + "/steal",
               new Param("sender", personalUuid.get().toString()),
               new Param("room", joinedRoomUuid.get().toString()),
               new Param("id", "" + idOfPlayer)).send()
                     .await();

         final Params resParams = Params.parse(res);

         if (resParams.get("error").isPresent()) {
            reject.accept(new Exception(resParams.get("error").get()));
            return;
         }

         GameVariables.getMe().inventory.add(Ressources.fromWeb(resParams.get("ressource").get()));
         resolve.accept(null);
      });
   }

   public static Promise<Void> stealAll(Ressources ressource) {
      return new Promise<Void>((resolve, reject) -> {
         if (notLogged(reject))
            return;

         final String res = new Request(url + "/stealAll",
               new Param("sender", personalUuid.get().toString()),
               new Param("room", joinedRoomUuid.get().toString()),
               new Param("ressource", "" + ressource.toWeb())).send()
                     .await();

         final Params resParams = Params.parse(res);

         if (resParams.get("error").isPresent()) {
            reject.accept(new Exception(resParams.get("error").get()));
            return;
         }

         GameVariables.getMe().inventory.set(ressource, Integer.parseInt(resParams.get("newCount").get()));
         resolve.accept(null);
      });
   }

   public static Promise<Void> draw2Ressources(Ressources ressource1, Ressources ressource2) {
      return new Promise<Void>((resolve, reject) -> {
         if (notLogged(reject))
            return;

         final String res = new Request(url + "/draw2",
               new Param("sender", personalUuid.get().toString()),
               new Param("room", joinedRoomUuid.get().toString()),
               new Param("ressource2", "" + ressource2.toWeb()),
               new Param("ressource1", "" + ressource1.toWeb())).send()
                     .await();

         final Params resParams = Params.parse(res);

         if (resParams.get("error").isPresent()) {
            reject.accept(new Exception(resParams.get("error").get()));
            return;
         }

         GameVariables.getMe().inventory.add(ressource1);
         GameVariables.getMe().inventory.add(ressource2);

         resolve.accept(null);
      });
   }

   public static Promise<Void> placeRobber(Coord coord) {
      return new Promise<Void>((resolve, reject) -> {
         if (notLogged(reject))
            return;

         final String res = new Request(url + "/placeRobber",
               new Param("sender", personalUuid.get().toString()),
               new Param("room", joinedRoomUuid.get().toString()),
               new Param("position", coord.toWeb())).send()
                     .await();

         final Params resParams = Params.parse(res);

         if (resParams.get("error").isPresent()) {
            reject.accept(new Exception(resParams.get("error").get()));
            return;
         }

         GameVariables.getMe().incrRobberCount();

         resolve.accept(null);
      });
   }
}
