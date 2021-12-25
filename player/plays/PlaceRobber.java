package player.plays;

import map.Land;
import player.Player;

/**
 * PlaceRobber
 */
public class PlaceRobber extends Play {
   final Land landToPlace;

   PlaceRobber(Player player, Land land) {
      super(player);
      this.landToPlace = land;
   }

   @Override
   public void execute() {
      throw new Error("NI");
      // déplace le voleur
   }
}