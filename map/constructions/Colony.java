package map.constructions;

import map.ressources.Cost;
import player.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import player.Player.Color;
import util_my.Promise;
import globalVariables.ViewVariables;

import java.awt.*;

public class Colony extends Building {
   public static final Map<Color, Promise<Image>> images = new HashMap<Color, Promise<Image>>() {
      {
         Stream.of(Color.values()).forEach(color -> {
            String colorString = color.toString().substring(0, 1).toUpperCase() + color.toString().substring(1);
            this.put(color, ViewVariables.importImage("assets/colonies/Colony" + colorString + ".png", -1));
         });
      }
   };
   public final static Cost cost = new Cost(0, 2, 0, 3, 0);

   public Colony(Player owner) {
      super(owner, Colony.images.get(owner.color));
   }
}
