package player.moves;

import player.Player;

abstract public class Move {
   public Player player;

   public Move(Player player) {
      this.player = player;
   }
}
