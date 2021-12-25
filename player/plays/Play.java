package player.plays;

import map.Land.BUILD;
import player.Player;
import player.Inventory.NOT_ENOUGH_RESSOURCES;

abstract public class Play {
   public Player player;

   public Play(Player player) {
      this.player = player;
   }

   abstract public void execute() throws NOT_ENOUGH_RESSOURCES, BUILD;

}
