package server;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import com.sun.net.httpserver.HttpExchange;

import map.constructions.City;
import util_my.Params;

class DicesHandler extends Handler {
   @Override
   String getResponse(HttpExchange req, Params params) throws IOException {
      final Optional<Room> room = params.get("room").map(UUID::fromString).map(ServerVariables::getRoom)
            .orElse(Optional.empty());
      final Optional<UUID> sender = params.get("sender").map(UUID::fromString);
      if (room.isEmpty())
         return params.get("room").isEmpty() ? "error=bad_req:no_room" : "error=room_not_found";
      if (sender.isEmpty())
         return "error=bad_req:no_sender";

      if (!room.get().playerToPlay.uuid.equals(sender.get()))
         return "error=illegal:not_your_turn";

      final int firstDice = (int) (Math.random() * 6) + 1;
      final int secondDice = (int) (Math.random() * 6) + 1;
      final int dicesResult = firstDice + secondDice;

      room.get().map.getAll().stream().filter(land -> land.getNumber() == dicesResult).forEach(land -> {
         land.buildings().forEach(building -> {
            land.getRessource().ifPresent(ressource -> {
               building.owner.inventory.add(ressource, building instanceof City ? 2 : 1);
            });
         });
      });

      final String play = "dice1=" + firstDice + "&dice2=" + secondDice;
      room.get().addMove(play);
      return play;
   }
}

class DoneHandler extends Handler {
   @Override
   String getResponse(HttpExchange req, Params params) throws IOException {
      final Optional<Room> room = params.get("room").map(UUID::fromString).map(ServerVariables::getRoom)
            .orElse(Optional.empty());
      final Optional<UUID> sender = params.get("sender").map(UUID::fromString);
      if (room.isEmpty())
         return params.get("room").isEmpty() ? "error=bad_req:no_room" : "error=room_not_found";
      if (sender.isEmpty())
         return "error=bad_req:no_sender";

      if (!room.get().playerToPlay.uuid.equals(sender.get()))
         return "error=illegal:not_your_turn";

      room.get().nextPlayer();

      final String play = "newTurn=" + room.get().turn + "&player=" + room.get().playerToPlay.id;
      room.get().addMoveToAll(play);
      return play;
   }
}
