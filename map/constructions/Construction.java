package map.constructions;

import player.Player;
import java.awt.Image;

public abstract class Construction {
   public final Player owner;
   public final Image image;

   Construction(Player owner, Image image) {
      this.owner = owner;
      this.image = image;
   }
}
