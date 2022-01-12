import java.io.IOException;
import java.util.Scanner;

import globalVariables.GameVariables;
import online.Online;
import server.Server;
import util_my.Promise;
import view.View;
import view.scenes.StartMenuScene;
import view.scenes.GameScene.GameScene;

class Test {
   public static void main(String[] args) {
   }
}

class TestGame {
   public static void main(String[] args) throws IOException {
      Server.main();

      Online.publicName = "user";

      Online.createRoom("room").await();

      Promise<Void> mapDownload = Online.downloadMap();

      Scanner sc = new Scanner(System.in);
      System.out.println("run ? ");
      sc.nextLine();
      Online.startGame().await();
      sc.close();

      Online.updateRoomJoined();
      mapDownload.await();

      GameVariables.view = new View();
      GameVariables.scenes.startMenuScene = new StartMenuScene(GameVariables.view);
      GameVariables.scenes.gameScene = new GameScene(GameVariables.view);

      GameVariables.scenes.startMenuScene.enable();
   }
}
