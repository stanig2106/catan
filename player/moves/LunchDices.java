package player.moves;

import player.Player;

public class LunchDices extends Move {

   LunchDices(Player player) {
      super(player);
   }

   public int dicesResult() {
      return (int) Math.random() * 6 + (int) Math.random() * 6 + 2;
   }
}
