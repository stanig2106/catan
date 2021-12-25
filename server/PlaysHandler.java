package server;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import com.sun.net.httpserver.HttpExchange;

import map.Land.BUILD;
import map.constructions.City;
import player.Inventory.NOT_ENOUGH_RESSOURCES;
import player.developmentCards.Card;
import player.plays.Build;
import util_my.Coord;
import util_my.Params;
import util_my.directions.LandCorner;
import util_my.directions.LandSide;

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

      room.get().map.getAll().stream()
            .filter(land -> land.getNumber() == dicesResult && room.get().map.robber.position != land).forEach(land -> {
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

class BuildHandler extends Handler {
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

      if (!(params.contain("buildType") && params.contain("coord") && params.contain("position")))
         return "error=bad_req:missing_build_field";

      final String type = params.get("buildType").get();
      final Coord coord = Coord.fromWeb(params.get("coord").get());
      final String position = params.get("position").get();

      switch (type) {
         case "colony":
            try {
               new Build.BuildColony(room.get().getPlayer(sender.get()).get(),
                     room.get().map.get(coord),
                     LandCorner.fromWeb(position)).serverExecute(room.get().turn, room.get().map);
            } catch (NOT_ENOUGH_RESSOURCES | BUILD e) {
               return "illegal=" + e.getClass().getSimpleName();
            }
            break;
         case "city":
            try {
               new Build.BuildCity(room.get().getPlayer(sender.get()).get(),
                     room.get().map.get(coord),
                     LandCorner.fromWeb(position)).execute();
            } catch (NOT_ENOUGH_RESSOURCES | BUILD e) {
               return "illegal=" + e.getClass().getSimpleName();
            }
            break;
         case "route":
            try {
               new Build.BuildRoute(room.get().getPlayer(sender.get()).get(),
                     room.get().map.get(coord),
                     LandSide.fromWeb(position)).execute();
            } catch (NOT_ENOUGH_RESSOURCES | BUILD e) {
               return "illegal=" + e.getClass().getSimpleName();
            }
            break;
         default:
            return "error=bad_req:unknown_build_type";
      }

      final String play = params.without("sender", "room").getPostDataString();
      System.out.println("server send : " + play);
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

      if (room.get().turn < 0) {
         room.get().playerToPlay.freeColony++;
         room.get().playerToPlay.freeRoute++;
      }

      final String play = "newTurn=" + room.get().turn + "&player=" + room.get().playerToPlay.id;
      room.get().addMoveToAll(play);
      return play;
   }
}

class BuyCardHandler extends Handler {
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

      // TODO: uncomment
      // if (!room.get().playerToPlay.canBuyCard())
      // return "illegal:NOT_ENOUGH_RESSOURCES";

      if (room.get().poolCards.isEmpty())
         room.get().poolCards.addAll(Card.newPoolCards());
      final Card card = room.get().poolCards.pop();

      room.get().playerToPlay.addCard(card);

      return "card=" + card.toWeb();
   }
}

class PlayCardHandler extends Handler {
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

      int indexOfCard = Integer.parseInt(params.get("index").get());

      final Card card = room.get().playerToPlay.inventory.cards.get(indexOfCard).getKey();
      room.get().playerToPlay.inventory.cards.remove(indexOfCard);

      switch (card) {
         case RoadBuilding:
            room.get().playerToPlay.freeRoute += 2;
            break;
         default:
            break;
      }
      final String play = "card=" + card.toWeb();
      room.get().addMove(play);
      return play;
   }
}