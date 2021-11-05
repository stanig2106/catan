import map.Map;

/**
 * Catane
 * 
 * By Stani Gam, Manon Baha
 * 
 * 01 nov. 2021
 */
public class Game {
   public static void main(String[] args) {
      Map map = new Map();
      map.initRandomLand();
      System.out.println(map);
   }
}