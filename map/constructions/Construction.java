package map.constructions;

import player.Player;

public abstract class Construction {
   public final Player owner;

   Construction(Player owner) {
      this.owner = owner;
   }
}
