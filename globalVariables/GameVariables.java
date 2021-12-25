package globalVariables;

import java.util.LinkedList;

import map.CatanMap;
import player.Player;
import player.developmentCards.Card;
import view.View;
import view.scenes.StartMenuScene;
import view.scenes.GameScene.GameScene;

public abstract class GameVariables {
   public static CatanMap map;
   public static View view;

   public abstract static class scenes {
      public static StartMenuScene startMenuScene;
      public static GameScene gameScene;
   }

   public static boolean console = false;
   public static Player[] players;
   public static Player playerToPlay;
   private static int idOfPlayerToPlay = -1;

   public static void nextPlayer() {
      idOfPlayerToPlay++;
      if (idOfPlayerToPlay >= players.length) {
         idOfPlayerToPlay = 0;
         turn++;
      }
      playerToPlay = players[idOfPlayerToPlay];
   }

   public static int turn = -2;

   public static LinkedList<Card> poolCards = Card.newPoolCards();
}
