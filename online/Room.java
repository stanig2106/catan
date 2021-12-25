package online;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import player.Player;
import util_my.Pair;
import util_my.Param;
import util_my.Params;
import util_my.StreamUtils;

public class Room {
   public final String name;
   public final UUID uuid;
   public final Player[] players;

   public Room(String name, UUID uuid, Player... players) {
      this.name = name;
      this.uuid = uuid;
      this.players = players;
   }

   @Override
   public String toString() {
      return this.name + " : " + Stream.of(this.players).map(Player::getName).collect(Collectors.joining(","));
   }

   public static Room[] fromWeb(String s) {
      final Params resParams = Params.parse(s);
      if (resParams.get("warning").map(warning -> warning.equals("void")).orElse(false))
         return new Room[0];
      return resParams.stream().collect(Collectors.groupingBy(param -> param.getKey().split(":")[0]))
            .entrySet().stream()
            .map(entry -> Pair.of(entry).map((name, params) -> {
               final Map<Boolean, List<Param>> isUUID = new Params(params.toArray(Param[]::new)).stream()
                     .collect(Collectors.partitioningBy(param -> param.getKey().endsWith(":UUID")));
               final Pair<String, UUID> UUIDParam = isUUID.get(true).get(0)
                     .map((String roomName, String UUIDString) -> Pair.of(roomName.split(":")[0],
                           UUID.fromString(UUIDString)));
               return new Room(UUIDParam.getKey(), UUIDParam.getValue(),
                     isUUID.get(false).stream().map(
                           pair -> pair.mapKey((roomAndId, playerName) -> Integer.parseInt(roomAndId.split(":")[1])
                                 + (roomAndId.endsWith(":") ? 0.5 : 0.0)))
                           .map(pair -> pair
                                 .map((Double id, String playerName) -> id % 1 == 0
                                       ? new Player.Online(playerName, id.intValue())
                                       : new Player.Me(playerName, id.intValue())))
                           .toArray(Player[]::new));
            })).toArray(Room[]::new);
   }
}