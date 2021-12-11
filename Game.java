import gameVariables.GameVariables;
import map.CataneMap;
import map.Land;
import map.Land.BUILD;
import map.constructions.Colony;
import util_my.Coord;
import util_my.directions.LandCorner;

/**
 * Catane
 * 
 * By Stani Gam, Manon Baha
 * 
 * 01 nov. 2021
 */
public class Game {
   public static void main(String[] args) {
      GameVariables.map = new CataneMap();
      System.out.println(GameVariables.map);
      CataneMap map = GameVariables.map;
      Land land =  GameVariables.map.get(new Coord(0, 0));
      try {
        land.setBuilding(LandCorner.topLeft, new Colony(null));
     } catch (BUILD e) {
        e.printStackTrace();
        throw new Error(e);
     }
   }
}

