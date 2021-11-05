package player;

import map.ressources.Ressource;
import player.moves.Build;
import player.moves.Move;

public abstract class Player {
   static final Error NOT_ENOUGH_RESSOURCES_ERROR = new Error();
   public int[] ressources = new int[Ressource.totalRessource];

   public boolean haveEnough(int ressourceCode, int value) {
      return ressources[ressourceCode] >= value;
   }

   /**
    * @throws NOT_ENOUGH_RESSOURCES_ERROR
    */
   public void pay(int ressourceCode, int value) {
      if (!this.haveEnough(ressourceCode, value))
         throw NOT_ENOUGH_RESSOURCES_ERROR;
      ressources[ressourceCode] -= value;
   }

   void play(Move move) {
      if (move instanceof Build) {
         if (!((Build<?>) move).havePlayerEnoughRessource())
            throw new Error("todo"); // TODO:
         ((Build<?>) move).pay();
         ((Build<?>) move).setConstruction();
      }
   }

}

class RealPlayer extends Player {
   void askMoveOnConsole() {
   }
}

class IA extends Player {

}