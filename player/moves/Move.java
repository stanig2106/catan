package player.moves;

import player.Player;

abstract public class Move {
   public Player player;

   public Move(Player player) {
      this.player = player;
   }
}

class LunchDices extends Move {

   LunchDices(Player player) {
      super(player);
   }

   public int dicesResult() {
      return (int) Math.random() * 6 + (int) Math.random() * 6 + 2;
   }
}
