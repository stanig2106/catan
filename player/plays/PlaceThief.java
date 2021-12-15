package player.plays;

import map.Land;
import player.Player;

/**
 * PlaceThief
 */
public class PlaceThief extends Play {
   final Land landToPlace;

   PlaceThief(Player player, Land land) {
      super(player);
      this.landToPlace = land;
   }

   @Override
   public void execute() {
      throw new Error("NI");
      // déplace le voleur
   }
}