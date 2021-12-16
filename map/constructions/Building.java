package map.constructions;

import player.Player;
import java.awt.Image;

public abstract class Building extends Construction {

   Building(Player owner, Image image) {
      super(owner, image);
   }
}
