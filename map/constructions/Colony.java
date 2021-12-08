package map.constructions;

import map.ressources.Cost;
import map.ressources.Ressources;
import player.Player;

public class Colony extends Building {
   public final static Cost cost = new Cost();

   public Colony(Player owner) {
      super(owner);

      Colony.cost.set(Ressources.Ore, 3);
      Colony.cost.set(Ressources.Grain, 2);
   }
}
