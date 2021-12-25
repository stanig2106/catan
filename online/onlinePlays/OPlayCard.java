package online.onlinePlays;

import java.util.Optional;

import globalVariables.GameVariables;
import player.developmentCards.Card;

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
            break;
         default:
            break;
      }
   }

}
