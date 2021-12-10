import gameVariables.GameVariables;
import map.CataneMap;
import map.Land;
import map.Land.BUILD;
import map.constructions.Building;
import map.constructions.Colony;
import util_my.Coord;
import util_my.directions.LandCorner;
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
      GameVariables.map = new CataneMap();
      GameVariables.map.initRandomLand();
      System.out.println(GameVariables.map);
      CataneMap map = GameVariables.map;
      Land land =  GameVariables.map.get(new Coord(0, 0));
      try {
        land.setBuilding(LandCorner.topLeft, new Colony(null));
     } catch (BUILD e) {
        e.printStackTrace();
        throw new Error(e);
     }
     land.debug2();
     Building b = land.getBuilding(LandCorner.topLeft).get();
     System.out.println(b);
     map.forEachAdjacent(new Coord(0, 0), (_land, side) -> {
        if (side == LandSide.topLeft) {
           System.out.println(_land + "ah ?");
           _land.debug();
        }
         LandCorner.stream().forEach(corner -> {
            _land.getBuilding(corner).ifPresent((route) -> {
               System.out.println("ok");
               System.out.println(corner);
              System.out.println(_land);
               _land.debug3();
            });
         });
         
      });
   }
}

