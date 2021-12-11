package map.constructions;

import map.ressources.Cost;
import player.Player;

public class City​​ extends Building {

   public final static Cost cost = new Cost(1, 1, 1, 0, 1);

   public City​​(Player owner) {
      super(owner);
   }
}
