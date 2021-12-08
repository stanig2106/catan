package util_my;

import java.util.HashMap;
import java.util.stream.Stream;

import map.ressources.Ressources;

public abstract class RessourceCount extends HashMap<Ressources, Integer> {
   public RessourceCount(int brick, int grain, int lumber, int ore, int wool) {
      this();
      this.replace(Ressources.Brick, brick);
      this.replace(Ressources.Grain, grain);
      this.replace(Ressources.Lumber, lumber);
      this.replace(Ressources.Ore, ore);
      this.replace(Ressources.Wool, wool);
   }

   public RessourceCount() {
      super();
      Stream.of(Ressources.values()).forEach((ressource) -> this.put(ressource, 0));
   }

   public void set(Ressources ressource, int value) {
      this.replace(ressource, value);
   }

   public void minus(Ressources ressource) {
      this.minus(ressource, 1);
   }

   public void minus(Ressources ressource, int value) {
      this.replace(ressource, this.get(ressource) - value);
   }

   public void add(Ressources ressource) {
      this.add(ressource, 1);
   }
   public void add(Ressources ressource, int value) {
      this.replace(ressource, this.get(ressource) + value);
   }
}