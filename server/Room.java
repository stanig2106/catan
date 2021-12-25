package server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import map.CatanMap;
import player.Player;
import player.Player.Server;
import player.developmentCards.Card;

class Room {
   final String name;
   final UUID uuid = UUID.randomUUID();
   private Player.Server owner;
   final List<Player.Server> players = new ArrayList<Player.Server>();
   final CatanMap map = new CatanMap();
   Player.Server playerToPlay;
   private boolean started = false;
   public int turn = -2;
   public final LinkedList<Card> poolCards = Card.newPoolCards();

   Room(String name) {
      this.name = ServerVariables.secureString(name, 10);
   }

   public Player.Server createAndAddPlayer(String name) {

      final Player.Server newPlayer = new Player.Server(ServerVariables.secureString(name, 10),
            this.players.size());

      this.players.add(newPlayer);
      return newPlayer;
   }

   public boolean hasPlayerUuid(UUID uuid) {
      return this.players.stream().anyMatch(player -> player.uuid.equals(uuid));
   }

   public void removePlayer(int id) {
      this.players.removeIf(player -> player.id == id);
   }

   public Optional<Player.Server> getPlayer(int id) {
      return this.players.stream().filter(player -> player.id == id).findFirst();
   }

   public Optional<Player.Server> getPlayer(UUID uuid) {
      return this.players.stream().filter(player -> player.uuid.equals(uuid)).findFirst();
   }

   public String toWeb() {
      return toWeb(Optional.empty());
   }

   public String toWeb(UUID senderUuid) {
      return toWeb(Optional.of(senderUuid));
   }

   String toWeb(Optional<UUID> senderUuid) {
      return this.name + ":UUID" + "=" + uuid + "&" + players.stream()
            .map(player -> (this.name + ":" + player.id
                  + senderUuid.map(senderUuid_ -> senderUuid_.equals(player.uuid) ? ":" : "").orElse("")
                  + "=" + player.getName()))
            .collect(Collectors.joining("&"));
   }

   public void setOwner(Server owner) {
      this.owner = owner;
      this.playerToPlay = owner;
      this.owner.freeColony++;
      this.owner.freeRoute++;
   }

   public boolean isOwner(Server player) {
      return isOwner(player.uuid);
   }

   public boolean isOwner(UUID uuidOfPlayer) {
      return this.owner.uuid.equals(uuidOfPlayer);
   }

   public Player.Server getOwner() {
      return owner;
   }

   public boolean isStarted() {
      return started;
   }

   public void start() {
      this.started = true;
   }

   public void addMove(String play) {
      this.players.stream().filter(Predicate.not(this.playerToPlay::equals))
            .forEach(player -> player.addWaitedPlays(play));
   }

   public void addMoveToAll(String play) {
      this.players.stream()
            .forEach(player -> player.addWaitedPlays(play));
   }

   public void nextPlayer() {
      if (playerToPlay.id + 1 >= players.size()) {
         playerToPlay = players.get(0);
         turn++;
      } else
         playerToPlay = players.get(playerToPlay.id + 1);
   }

}
