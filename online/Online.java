package online;

import java.util.Optional;
import java.util.UUID;

import globalVariables.GameVariables;
import map.CatanMap;
import online.onlinePlays.Dices;
import online.onlinePlays.Done;
import util_my.Coord;
import util_my.Param;
import util_my.Params;
import util_my.Promise;
import util_my.Timeout;

public final class Online {
   public static String url = "http://localhost:8080";
   public static String publicName = "helloName";
   public static Optional<UUID> personalUuid = Optional.empty();
   public static Optional<UUID> joinedRoomUuid = Optional.empty();
   public static Optional<Room> joinedRoom = Optional.empty();
   public static Room[] rooms = new Room[0];

   private Online() {
   }

   private static boolean notLogged() {
      return personalUuid.isEmpty() || joinedRoomUuid.isEmpty();
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
         if (notLogged()) {
            reject.accept(new Exception("not logged"));
            return;
         }
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
         if (notLogged()) {
            reject.accept(new Exception("not logged"));
            return;
         }

         final String res = new Request(url + "/dices",
               new Param("sender", personalUuid.get().toString()),
               new Param("room", joinedRoomUuid.get().toString())).send()
                     .await();

         final Params resParams = Params.parse(res);

         if (resParams.get("error").isPresent()) {
            reject.accept(new Exception(resParams.get("error").get()));
            return;
         }

         Dices.exec(Integer.parseInt(resParams.get("dice1").get()),
               Integer.parseInt(resParams.get("dice2").get()));
         resolve.accept(null);
      });
   }

   public static Promise<Void> done() {
      return new Promise<Void>((resolve, reject) -> {
         if (notLogged()) {
            reject.accept(new Exception("not logged"));
            return;
         }

         final String res = new Request(url + "/done",
               new Param("sender", personalUuid.get().toString()),
               new Param("room", joinedRoomUuid.get().toString())).send()
                     .await();

         final Params resParams = Params.parse(res);

         if (resParams.get("error").isPresent()) {
            reject.accept(new Exception(resParams.get("error").get()));
            return;
         }

         Done.exec(Integer.parseInt(resParams.get("newTurn").get()),
               Integer.parseInt(resParams.get("player").get()));
         resolve.accept(null);
      });
   }

   public static Promise<Void> waitPlays() {

      return new Promise<Void>((resolve, reject) -> {
         if (notLogged()) {
            reject.accept(new Exception("not logged"));
            return;
         }
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

            if (resParams.contain("dices1")) {
               Dices.exec(Integer.parseInt(resParams.get("dice1").get()),
                     Integer.parseInt(resParams.get("dice2").get()));
               resolve.accept(null);
               return;
            }
         }

         System.out.println("new turn ! " + resParams.get("newTurn").get());

         Done.exec(Integer.parseInt(resParams.get("newTurn").get()),
               Integer.parseInt(resParams.get("player").get()));

         resolve.accept(null);
      });
   }

   public static Promise<Void> sendBuild(String type, Coord coord, String cornerOrBorder) {
      return new Promise<Void>((resolve, reject) -> {
         if (personalUuid.isEmpty()) {
            reject.accept(new Exception("not logged"));
            return;
         }
         String res = new Request(url + "/build",
               new Param("sender", personalUuid.get().toString()),
               new Param("type", type),
               new Param("coord", coord.toWeb()),
               new Param("position", cornerOrBorder)).send()
                     .await();
         Params resParams = Params.parse(res);

         if (resParams.get("error").isPresent()) {
            reject.accept(new Exception(resParams.get("error").get()));
            return;
         }
      });
   }

}
