package map.constructions;

import player.Player;
import util_my.Promise;

import java.awt.*;

public abstract class Construction {
   public final Player owner;
   public final Promise<Image> image;

   Construction(Player owner, Promise<Image> image) {
      this.owner = owner;
      this.image = image;
   }

   public abstract void addToPlayer();
}
