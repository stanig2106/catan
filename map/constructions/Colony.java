package map.constructions;

import java.util.HashMap;
import java.util.Map;

import map.ressources.Grain;
import map.ressources.Ore;
import player.Player;

public class Colony extends Building {
   public final static Map<Integer, Integer> cost = new HashMap<Integer, Integer>();

   public Colony(Player owner) {
      super(owner);

      Colony.cost.put(Ore.ressourceCode, 3);
      Colony.cost.put(Grain.ressourceCode, 2);

   }
}
