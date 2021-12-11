package player.plays;

import player.Player;

abstract public class Play {
   public Player player;

   public Play(Player player) {
      this.player = player;
   }

   abstract public void execute();
}

