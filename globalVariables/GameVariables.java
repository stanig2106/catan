package globalVariables;

import map.CatanMap;
import player.Player;
import view.View;
import view.scenes.GameScene;
import view.scenes.StartMenuScene;

public abstract class GameVariables {
   public static CatanMap map;
   public static View view;

   public abstract static class scenes {
      public static StartMenuScene startMenuScene;
      public static GameScene gameScene;
   }

   public static boolean console = false;
   public static Player[] players;
}
