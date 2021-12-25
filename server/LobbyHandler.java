package server;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;

import player.Player;
import util_my.Params;

class RoomsHandler extends Handler {

   String getResponse(HttpExchange req, Params params) throws IOException {
      Optional<UUID> roomUuid = params.get("of").map(UUID::fromString);
      Optional<UUID> sender = params.get("sender").map(UUID::fromString);

      if (ServerVariables.rooms.size() == 0)
         return "warning=void";

      if (roomUuid.isPresent())
         return ServerVariables.rooms.stream().filter(room -> room.uuid.equals(roomUuid.get())).findFirst()
               .map(room -> room.toWeb(sender)).orElse("warning=void");

      return ServerVariables.rooms.stream().filter(Predicate.not(Room::isStarted)).map(Room::toWeb)
            .collect(Collectors.joining("&"));
   }
}

class RoomHandler extends Handler {

   String getResponse(HttpExchange req, Params params) throws IOException {
      final Optional<String> playerName = params.get("player");

      final Optional<Room> joinRoom = params.get("join").map(uuidOfRoom -> {
         return ServerVariables.rooms.stream().filter(room -> room.uuid.equals(UUID.fromString(uuidOfRoom)))
               .findFirst();
      }).orElse(Optional.empty());

      final Optional<Room> createdRoom = params.get("create").map(Room::new);

      if (playerName.isEmpty() || joinRoom.isEmpty() && createdRoom.isEmpty())
         return "error=bad_req:no_player_name_or_unknown_room";

      if (createdRoom.map(
            newRoom -> ServerVariables.rooms.stream()
                  .anyMatch(room -> !room.isStarted() && room.name.equals(newRoom.name)))
            .orElse(false))
         return "error=room_name:room_name_already_existe";

      if (joinRoom.map(Room::isStarted).orElse(false))
         return "error=room_started";

      final Room room = joinRoom.orElseGet(() -> {
         ServerVariables.rooms.add(createdRoom.get());
         return createdRoom.get();
      });

      final Player.Server player = room.createAndAddPlayer(playerName.get());
      createdRoom.ifPresent(room_ -> room_.setOwner(player));

      return "playerUUID=" + player.uuid + "&" + "roomUUID=" + room.uuid;
   }
}

class StartHandler extends Handler {

   String getResponse(HttpExchange req, Params params) throws IOException {
      Optional<Room> room = params.get("room").map(UUID::fromString).map(ServerVariables::getRoom)
            .orElse(Optional.empty());
      Optional<UUID> sender = params.get("sender").map(UUID::fromString);
      if (room.isEmpty())
         return params.get("room").isEmpty() ? "error=bad_req:no_room" : "error=room_not_found";
      if (sender.isEmpty())
         return "error=bad_req:no_sender";

      if (!room.get().isOwner(sender.get()))
         return "error=not_owner";
      room.get().start();
      final String play = "newTurn=" + room.get().turn + "&player=" + room.get().playerToPlay.id;
      room.get().addMoveToAll(play);
      return play;
   }
}

class StartedHandler extends Handler {
   String getResponse(HttpExchange req, Params params) throws IOException {
      Optional<Room> room = params.get("room").map(UUID::fromString).map(ServerVariables::getRoom)
            .orElse(Optional.empty());
      Optional<UUID> sender = params.get("sender").map(UUID::fromString);
      if (room.isEmpty())
         return params.get("room").isEmpty() ? "error=bad_req:no_room" : "error=room_not_found";
      if (sender.isEmpty())
         return "error=bad_req:no_sender";

      return room.get().isStarted() ? "started"
            : "wait=" + room.get().getOwner().getName();
   }
}
