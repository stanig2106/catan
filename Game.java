import javax.swing.SwingConstants;

import gameVariables.GameVariables;
import map.CataneMap;
import map.Land.BUILD.ROUTE_ON_ROUTE;
import map.constructions.Route;
import view.View;
import view.ViewVariables;
import player.Player;
import util_my.Coord;
import util_my.directions.LandSide;

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
      Player player = new Player.RealPlayer();
      try {
         GameVariables.map.get(new Coord(0, 0)).setRoute(LandSide.left, new Route(player));
      } catch (ROUTE_ON_ROUTE e) {
         e.printStackTrace();
      }
      ViewVariables.waitAllImageLoaded();
      System.out.println("now !");

      new View();
   }

}
