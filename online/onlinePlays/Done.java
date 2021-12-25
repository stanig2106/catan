package online.onlinePlays;

import globalVariables.GameVariables;

final public class Done {
   private Done() {

   }

   public static void exec(int turn, int playerTurnId) {
      GameVariables.turn = turn;
      GameVariables.playerToPlay = GameVariables.players[playerTurnId];
      GameVariables.scenes.gameScene.newTurn();

      if (GameVariables.view.backgroundPainting.updatePainting().await())
         GameVariables.view.background.repaint();
   }
}