package map.constructions;

import map.ressources.Cost;
import player.Player;

public class Colony extends Building {
   public final static Cost cost = new Cost(0, 2, 0, 3, 0);

   public Colony(Player owner) {
      super(owner);
   }
}
