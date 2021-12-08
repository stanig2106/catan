package player;

import map.ressources.Cost;
import util_my.RessourceCount;

public class Inventory extends RessourceCount {
   Inventory() {
      super();
   }

   public void pay(Cost cost) {
      cost.forEach((ressource, value) -> this.minus(ressource, value));
   }

   public boolean hasEnough(Cost cost) {
      return cost.entrySet().stream().allMatch((entry) -> {
         return this.get(entry.getKey()) >= entry.getValue();
      });
   }
}
