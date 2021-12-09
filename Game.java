import gameVariables.GameVariables;
import map.CataneMap;

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
      GameVariables.map.initRandomLand();
      System.out.println(GameVariables.map);

      GameVariables.map.forEach(land -> System.out.println(land.toStringAll()));
   }
}