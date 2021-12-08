package map.constructions;

import map.ressources.Cost;
import map.ressources.Ressources;
import player.Player;

public class City​​ extends Building {

   public final static Cost cost = new Cost();

   public City​​(Player owner) {
      super(owner);

      City​​.cost.set(Ressources.Brick, 1);
      City​​.cost.set(Ressources.Lumber, 1);
      City​​.cost.set(Ressources.Wool, 1);
      City​​.cost.set(Ressources.Grain, 1);
   }
}
