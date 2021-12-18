package player.plays;

import globalVariables.GameVariables;
import map.constructions.City​​;
import player.Player;

public class LunchDices extends Play {
   final int dicesResult = (int) Math.random() * 6 + (int) Math.random() * 6 + 2;

   LunchDices(Player player) {
      super(player);
   }

   @Override
   public void execute() {
      if (dicesResult == 7)
         is7execute();
      else
         not7Execute();
   }

   private void not7Execute() {
      GameVariables.map.forEach((land) -> {
         if (land.getNumber() != dicesResult)
            return;
         land.buildings().filter((building) -> {
            return building.owner == this.player;
         }).forEach((building) -> {
            this.player.ressources.add(land.getRessource());
            if (building instanceof City​​)
               this.player.ressources.add(land.getRessource());
         });
      });
   }

   private void is7execute() {
      this.player.askPlaceThief().execute();
   }
}
