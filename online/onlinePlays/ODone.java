package online.onlinePlays;

import globalVariables.GameVariables;

final public class ODone {
   private ODone() {

   }

   public static void exec(int turn, int playerTurnId) {
      GameVariables.turn = turn;
      GameVariables.playerToPlay = GameVariables.players[playerTurnId];
      GameVariables.playerToPlay.updateCards();
      if (GameVariables.turn < 0) {
         GameVariables.playerToPlay.freeColony++;
         GameVariables.playerToPlay.freeRoute++;
      }
      GameVariables.scenes.gameScene.newTurn();

      if (GameVariables.view.backgroundPainting.updatePainting().await())
         GameVariables.view.background.repaint();
   }
}