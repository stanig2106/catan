package online.onlinePlays;

import java.util.Optional;

import globalVariables.GameVariables;
import globalVariables.ViewVariables;
import map.constructions.City;
import util_my.Pair;

final public class ODices {
   private ODices() {

   }

   public static void exec(int dice1, int dice2) {
      GameVariables.scenes.gameScene.gameInterfaceJob.overedButton = Optional.empty();
      GameVariables.scenes.gameScene.setDicesLunched(true);
      GameVariables.scenes.gameScene.gameInterfaceJob.setAllDisabled(true);

      GameVariables.scenes.gameScene.dicesScene.enable(dice1, dice2);

      if (dice1 + dice2 != 7)
         GameVariables.map.getAll().stream()
               .filter(land -> land.getNumber() == dice1 + dice2 && GameVariables.map.robber.position != land)
               .forEach(land -> {
                  land.buildings().forEach(building -> {
                     land.getRessource().ifPresent(ressource -> {
                        building.owner.inventory.add(ressource, building instanceof City ? 2 : 1);
                     });
                  });
               });
      GameVariables.scenes.gameScene.gameInterfaceJob.lastDice = Optional.of(Pair.of(dice1, dice2));
      GameVariables.scenes.gameScene.gameInterfaceJob.setAllDisabled(false);
      if (GameVariables.view.backgroundPainting.updatePainting().await())
         GameVariables.view.background.repaint();

   }

}
