import Jama.Matrix;
import gameVariables.GameVariables;
import map.CataneMap;
import map.Land;
import map.Land.BUILD;
import map.constructions.Colony;
import util_my.Coord;
import util_my.directions.LandCorner;
import views.View;
import views.ViewVariables;

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
      new View();
   }

}
