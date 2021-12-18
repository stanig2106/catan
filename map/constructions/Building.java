package map.constructions;

import player.Player;
import util_my.Promise;

import java.awt.Image;

public abstract class Building extends Construction {

   Building(Player owner, Promise<Image> image) {
      super(owner, image);
   }
}
