import java.io.IOException;
import java.util.Scanner;

import config.Config;
import globalVariables.GameVariables;
import online.Online;
import server.Server;
import util_my.Promise;
import util_my.StreamUtils;
import view.View;
import view.scenes.StartMenuScene;
import view.scenes.GameScene.GameScene;

/**
 * Catan
 * 
 * By Stani Gam, Manon Baha
 * 
 * 01 nov. 2021
 */
public class Game {
   public static void main(String[] args) throws IOException {
      Scanner sc = new Scanner(System.in);
      String choix;
      System.out.println("S pour server, ou votre nom pour jouer");
      choix = sc.nextLine();
      if (choix.equals("S")) {
         Server.main();
         Config.serverMode = true;
         sc.close();
         return;
      }
      Online.publicName = choix;
      boolean created = false;
      while (true) {
         Online.downloadRooms().await();
         StreamUtils.StreamIndexed(Online.rooms).forEach(pair -> pair.map((idx, room) -> {
            System.out.println(idx + " - " + room.toString());
         }));
         System.out.println();
         System.out.println("rejoignez une room ou entrez le nom d'une nouvelle room");

         choix = sc.nextLine();
         try {
            int roomIdx = Integer.parseInt(choix);
            Online.joinRoom(Online.rooms[roomIdx].uuid).await();
         } catch (NumberFormatException e) {
            Online.createRoom(choix).await();
            created = true;
         } catch (IndexOutOfBoundsException e) {
            continue;
         }
         break;
      }
      Promise<Void> mapDownload = Online.downloadMap();
      if (created) {
         while (true) {
            System.out.println("S pour start, R pour actualiser");
            choix = sc.nextLine();
            if (choix.equals("S")) {
               Online.startGame().await();
               break;
            } else if (choix.equals("R")) {
               Online.updateRoomJoined();
               System.out.println(Online.joinedRoom.get());
            }
         }
      } else {
         System.out.println("wait for game to start");
         Online.waitGameStarted().await();
      }
      sc.close();

      Online.updateRoomJoined();
      mapDownload.await();

      GameVariables.view = new View();
      GameVariables.scenes.startMenuScene = new StartMenuScene(GameVariables.view);
      GameVariables.scenes.gameScene = new GameScene(GameVariables.view);

      GameVariables.scenes.startMenuScene.enable();
   }
}
