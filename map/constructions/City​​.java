package map.constructions;

import java.util.HashMap;
import java.util.Map;

import map.ressources.Brick;
import map.ressources.Grain;
import map.ressources.Lumber;
import map.ressources.Wool;
import player.Player;

public class City​​ extends Building {

   public final static Map<Integer, Integer> cost = new HashMap<Integer, Integer>();

   public City​​(Player owner) {
      super(owner);

      City​​.cost.put(Brick.ressourceCode, 1);
      City​​.cost.put(Lumber.ressourceCode, 1);
      City​​.cost.put(Wool.ressourceCode, 1);
      City​​.cost.put(Grain.ressourceCode, 1);
   }
}
