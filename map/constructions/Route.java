package map.constructions;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import map.ressources.Cost;
import player.Player;
import player.Player.Color;
import util_my.Promise;
import globalVariables.ViewVariables;

import java.awt.*;

public class Route extends Construction {
   public static final Map<Color, Promise<Image>> images = new HashMap<Color, Promise<Image>>() {
      {
         Stream.of(Color.values()).forEach(color -> {
            String colorString = color.toString().substring(0, 1).toUpperCase() + color.toString().substring(1);
            this.put(color, ViewVariables.importImage("assets/routes/Route" + colorString + ".png"));
         });
      }
   };

   public final static Cost cost = new Cost(1, 0, 1, 0, 0);

   public Route(Player owner) {
      super(owner, Route.images.get(owner.color));
   }
}
