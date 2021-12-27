package player.plays;

import globalVariables.GameVariables;
import map.Land;
import map.constructions.City​​;
import player.Player;

public class LunchDices extends Play {
   public final int firstDice = (int) (Math.random() * 6) + 1;
   public final int secondDice = (int) (Math.random() * 6) + 1;
   public final int dicesResult = firstDice + secondDice;

   public LunchDices(Player player) {
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
      GameVariables.map.getAll().stream().filter(land -> land.getNumber() == dicesResult).forEach(land -> {
         land.buildings().forEach(building -> {
            land.getRessource().ifPresent(ressource -> {
               building.owner.ressources.add(ressource, building instanceof City​​ ? 2 : 1);
            });
         });
      });
   }

   private void is7execute() {
      // this.player.askPlaceThief().execute();
   }
}
