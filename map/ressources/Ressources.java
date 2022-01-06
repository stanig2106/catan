package map.ressources;

import util_my.Promise;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import globalVariables.ViewVariables;

public enum Ressources {
   Ore, Wheat, Brick, Lumber, Wool;

   static Map<Ressources, Promise<Image>> images = new HashMap<>() {
      {
         Stream.of(Ressources.values()).forEach(ressource -> {
            this.put(ressource, ViewVariables.importImage("assets/ressources/" + ressource + ".png", -1));
         });
      }
   };

   public Promise<Image> getImage() {
      return Ressources.images.get(this);
   }

   public String toWeb() {
      return this.ordinal() + "";
   }

   public static Ressources fromWeb(String s) {
      return Ressources.values()[Integer.parseInt(s)];
   }
}
