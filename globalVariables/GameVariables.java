package globalVariables;

import map.CatanMap;
import player.Player;
import view.View;
import view.scenes.StartMenuScene;
import view.scenes.GameScene.GameScene;
import text_view.TextView;

public abstract class GameVariables {
   public static CatanMap map;
   public static View view;
   public static TextView textView;

   public abstract static class scenes {
      public static StartMenuScene startMenuScene;
      public static GameScene gameScene;
   }

   public static boolean console = false;
   public static Player[] players;
   public static Player playerToPlay;
}
