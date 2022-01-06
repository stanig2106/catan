package online.onlinePlays;

import java.util.Optional;

import globalVariables.GameVariables;
import map.ressources.Ressources;
import online.Online;
import player.developmentCards.Card;
import util_my.Box;
import view.scenes.GameScene.ChoseRessourceScene;

final public class OPlayCard {
   private OPlayCard() {

   }

   public static void selfExec(int indexOfCard) {
      final Card card = GameVariables.getMe().inventory.cards.get(indexOfCard).getKey();
      GameVariables.getMe().inventory.cards.remove(indexOfCard);

      switch (card) {
         case RoadBuilding:
            GameVariables.getMe().freeRoute += 2;
            GameVariables.scenes.gameScene.enableRoadBuildingMode();
            break;
         case Knight:
            GameVariables.scenes.gameScene.robberScene.enable();
            break;
         case Monopoly:
            new ChoseRessourceScene().enable("Chose 1 type of resource to steal", (ressource, disable) -> {
               Online.stealAll(ressource);
               disable.run();
            });
            break;
         case YearOfPlenty:
            new ChoseRessourceScene().enable("Chose first type of resource to draw", (ressource1, disable1) -> {
               disable1.run();
               new ChoseRessourceScene().enable("Chose second type of resource to draw", (ressource2, disable2) -> {
                  Online.draw2Ressources(ressource1, ressource2);
                  disable2.run();
               });
            });
            break;
         default:
            break;
      }
   }

}
