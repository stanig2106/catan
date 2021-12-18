import globalVariables.GameVariables;
import globalVariables.ViewVariables;
import map.CataneMap;
import map.Land.BUILD;
import map.constructions.City​​;
import map.constructions.Colony;
import map.constructions.Route;
import view.View;
import player.Player;
import player.plays.Build;
import util_my.Coord;
import util_my.Promise;
import util_my.Timeout;
import util_my.directions.LandCorner;
import util_my.directions.LandSide;
import util_my.Promise;

import java.awt.Image;
import java.awt.Toolkit;

import Jama.Matrix;

import java.awt.MediaTracker;
import java.awt.Component;

/**
 * Catane
 * 
 * By Stani Gam, Manon Baha
 * 
 * 01 nov. 2021
 */
public class Game {
   public static void main(String[] args) {
      System.out.println("loading...");
      GameVariables.map = new CataneMap();
      final Player player = new Player.RealPlayer();
      View view = new View();
      new Timeout(() -> {
         try {
            GameVariables.map.get(new Coord(0, 0)).setBuilding(LandCorner.topLeft, new Colony(player));
         } catch (BUILD e) {
            e.printStackTrace();
         }
         try {
            GameVariables.map.get(new Coord(0, 0)).setRoute(LandSide.left, new Route(player));
         } catch (BUILD e) {
            e.printStackTrace();
         }

         try {
            GameVariables.map.get(new Coord(0, 0)).setBuilding(LandCorner.topRight, new Colony(player));
         } catch (BUILD e) {
            e.printStackTrace();
         }
         try {
            GameVariables.map.get(new Coord(0, 0)).setBuilding(LandCorner.topRight, new City​​(player));
         } catch (BUILD e) {
            e.printStackTrace();
         }
         view.backgroundPainting.forceUpdatePainting().await();
         view.background.repaint();
      }, 1000);

   }

}
