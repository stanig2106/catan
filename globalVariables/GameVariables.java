package globalVariables;

import java.util.LinkedList;
import java.util.stream.Stream;

import map.CatanMap;
import player.Player;
import player.Player.Me;
import player.developmentCards.Card;
import view.View;
import view.scenes.StartMenuScene;
import view.scenes.GameScene.GameScene;
import view.scenes.GameScene.RobberScene;

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

   public static Player.Me getMe() {
      return (Me) Stream.of(players).filter(player -> (player instanceof Player.Me)).findFirst().get();
   }

   public static int turn;

   public static LinkedList<Card> poolCards = Card.newPoolCards();
}
