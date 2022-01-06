package server;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import com.sun.net.httpserver.HttpExchange;

import util_my.Params;

class RessourcesHandler extends Handler {
   @Override
   String getResponse(HttpExchange req, Params params) throws IOException {
      final Optional<Room> room = params.get("room").map(UUID::fromString).map(ServerVariables::getRoom)
            .orElse(Optional.empty());
      final Optional<Integer> id = params.get("id").map(Integer::parseInt);
      if (room.isEmpty())
         return params.get("room").isEmpty() ? "error=bad_req:no_room" : "error=room_not_found";
      if (id.isEmpty())
         return "error=bad_req:no_id";

      return "count=" + room.get().getPlayer(id.get()).get().inventory.getTotal();
   }
}

class CardsCountHandler extends Handler {
   @Override
   String getResponse(HttpExchange req, Params params) throws IOException {
      final Optional<Room> room = params.get("room").map(UUID::fromString).map(ServerVariables::getRoom)
            .orElse(Optional.empty());
      final Optional<Integer> id = params.get("id").map(Integer::parseInt);
      if (room.isEmpty())
         return params.get("room").isEmpty() ? "error=bad_req:no_room" : "error=room_not_found";
      if (id.isEmpty())
         return "error=bad_req:no_id";

      return "count=" + room.get().getPlayer(id.get()).get().inventory.getCardsCount();
   }
}

class RobberCountHandler extends Handler {
   @Override
   String getResponse(HttpExchange req, Params params) throws IOException {
      final Optional<Room> room = params.get("room").map(UUID::fromString).map(ServerVariables::getRoom)
            .orElse(Optional.empty());
      final Optional<Integer> id = params.get("id").map(Integer::parseInt);
      if (room.isEmpty())
         return params.get("room").isEmpty() ? "error=bad_req:no_room" : "error=room_not_found";
      if (id.isEmpty())
         return "error=bad_req:no_id";

      return "count=" + room.get().getPlayer(id.get()).get().getRobberCount();
   }
}