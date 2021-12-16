package map.constructions;

import map.ressources.Cost;
import player.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import map.ressources.Cost;
import player.Player;
import player.Player.Color;
import view.ViewVariables;

import java.awt.Image;

public class Colony extends Building {
   public static final Map<Color, Image> images = new HashMap<Color, Image>() {
      {
         Stream.of(Color.values()).forEach(color -> {
            String colorString = color.toString().substring(0, 1).toUpperCase() + color.toString().substring(1);
            this.put(color, ViewVariables.importImage("assets/colonies/Colony" + colorString + ".png", false));
         });
      }
   };
   public final static Cost cost = new Cost(0, 2, 0, 3, 0);

   public Colony(Player owner) {
      super(owner, Colony.images.get(owner.color));
   }
}
