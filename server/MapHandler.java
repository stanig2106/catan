package server;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import com.sun.net.httpserver.HttpExchange;

import util_my.Params;

class MapHandler extends Handler {
   // ./map
   @Override
   String getResponse(HttpExchange req, Params params) throws IOException {
      final Optional<UUID> sender = params.get("sender").map(UUID::fromString);

      if (sender.isEmpty())
         return "error=bad_req:NO_SENDER";

      Optional<Room> room = ServerVariables.getRoomOfPlayer(sender.get());

      if (room.isEmpty())
         return "error=bad_uuid";

      return room.get().map.toWeb();
   }
}