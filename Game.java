import globalVariables.GameVariables;
import map.CatanMap;
import player.Player;
import text_view.TextView;
import util_my.Promise;
import view.Scene;
import view.View;
import view.scenes.StartMenuScene;
import view.scenes.GameScene.GameScene;

import java.util.Scanner;

/**
 * Catan
 *
 * By Stani Gam, Manon Baha
 *
 * 01 nov. 2021
 */
public class Game {
   public static void main(String[] args) {
      Scanner sc = new Scanner(System.in);
      System.out.println("Interface Graphique (G) ou Textuelle (T) ?");
      String choix = sc.nextLine();
      while ((!choix.equals("T")) && (!choix.equals("G"))) {
         System.out.println("Interface Graphique (G) ou Textuelle (T) ?");
         choix = sc.nextLine();
      }
      Promise<Void> mapLoading = new Promise<Void>((resolve, reject) -> {
         System.out.println("mapLoading...");
         GameVariables.map = new CatanMap();
         final Player player = new Player.RealPlayer();
         GameVariables.players = new Player[] { player };
         GameVariables.playerToPlay = player;
         resolve.accept(null);
      });
      mapLoading.await();
      if (choix.equals("G")) {
         System.out.println("viewLoading...");
         GameVariables.view = new View();
         GameVariables.scenes.startMenuScene = new StartMenuScene(GameVariables.view);
         GameVariables.scenes.gameScene = new GameScene(GameVariables.view);
         GameVariables.scenes.startMenuScene.enable();
         System.out.println("done");
      }
      else {
         System.out.println("===== Mode textuel =====");
         GameVariables.textView = new TextView();
         GameVariables.textView.show();
      }
   }
}
