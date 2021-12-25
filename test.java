import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.util.Scanner;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import config.Config;
import globalVariables.GameVariables;
import online.Online;
import server.Server;
import util_my.Promise;
import util_my.StreamUtils;
import view.View;
import view.scenes.StartMenuScene;
import view.scenes.GameScene.GameScene;

class Test {
   public static void main(String[] args) {
      System.out.println(1.0 % 1);
   }
}

class TestGame {
   public static void main(String[] args) throws IOException {
      Server.main();

      Online.publicName = "user";

      Online.createRoom("room").await();

      Promise<Void> mapDownload = Online.downloadMap();

      Online.startGame().await();

      Online.updateRoomJoined();
      mapDownload.await();

      GameVariables.view = new View();
      GameVariables.scenes.startMenuScene = new StartMenuScene(GameVariables.view);
      GameVariables.scenes.gameScene = new GameScene(GameVariables.view);

      GameVariables.scenes.startMenuScene.enable();
   }
}
