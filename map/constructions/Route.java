package map.constructions;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import map.ressources.Cost;
import player.Player;
import player.Player.PlayerColors;
import util_my.Promise;
import globalVariables.ViewVariables;

import java.awt.*;

public class Route extends Construction {
   public static final Map<PlayerColors, Promise<Image>> images = new HashMap<PlayerColors, Promise<Image>>() {
      {
         Stream.of(PlayerColors.values()).forEach(color -> {
            String colorString = color.toString().substring(0, 1).toUpperCase() + color.toString().substring(1);
            this.put(color, ViewVariables.importImage("assets/routes/Route" + colorString + ".png", -1));
         });
      }
   };

   public final static Cost cost = new Cost(1, 0, 1, 0, 0);

   public Route(Player owner) {
      super(owner, Route.images.get(owner.color));
   }

   public void addToPlayer() {
      this.owner.routes.add(this);
   }
}
