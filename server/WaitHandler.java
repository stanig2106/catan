package server;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import com.sun.net.httpserver.HttpExchange;

import util_my.Params;

class WaitHandler extends Handler {

   @Override
   String getResponse(HttpExchange req, Params params) throws IOException {
      Optional<Room> room = params.get("room").map(UUID::fromString).map(ServerVariables::getRoom)
            .orElse(Optional.empty());
      Optional<UUID> sender = params.get("sender").map(UUID::fromString);
      if (room.isEmpty())
         return params.get("room").isEmpty() ? "error=bad_req:no_room" : "error=room_not_found";
      if (sender.isEmpty())
         return "error=bad_req:no_sender";

      return room.get().getPlayer(sender.get()).get().popWaitedPlay().orElse("wait");
   }

}
