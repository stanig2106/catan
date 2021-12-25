package util_my;

import java.util.HashMap;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import map.ressources.Ressources;

public abstract class RessourceCount extends HashMap<Ressources, Integer> {
   public RessourceCount(int brick, int grain, int lumber, int ore, int wool) {
      this();
      this.replace(Ressources.Brick, brick);
      this.replace(Ressources.Wheat, grain);
      this.replace(Ressources.Lumber, lumber);
      this.replace(Ressources.Ore, ore);
      this.replace(Ressources.Wool, wool);
   }

   public RessourceCount() {
      super();
      Stream.of(Ressources.values()).forEach((ressource) -> this.put(ressource, 0));
   }

   public int getCount(Ressources ressource) {
      return super.get(ressource);
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

   public void add(List<Ressources> ressources) {
      ressources.forEach(ressource -> {
         this.add(ressource);
      });
   }

   public void add(Ressources ressource) {
      this.add(ressource, 1);
   }

   public void add(Ressources ressource, int value) {
      this.replace(ressource, this.get(ressource) + value);
   }

   public int getTotal() {
      return this.values().stream().reduce(0, new BinaryOperator<Integer>() {
         @Override
         public Integer apply(Integer arg0, Integer arg1) {
            return arg0 + arg1;
         }
      });
   }
}