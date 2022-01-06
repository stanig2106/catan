package online.onlinePlays;

import java.util.Set;

import globalVariables.GameVariables;
import map.Land.BUILD;
import player.Inventory.NOT_ENOUGH_RESSOURCES;
import player.plays.Build;
import util_my.Coord;
import util_my.directions.LandCorner;
import util_my.directions.LandSide;
import view.inputs.BuildInputController.Modes;

final public class OBuild {
   private OBuild() {
   }

   public static void exec(String type, Coord coord, String position) {
      switch (type) {
         case "colony":
            try {
               new Build.BuildColony(GameVariables.playerToPlay,
                     GameVariables.map.get(coord),
                     LandCorner.fromWeb(position)).execute();
            } catch (NOT_ENOUGH_RESSOURCES | BUILD _e) {
               try {
                  new Build.BuildColony(GameVariables.playerToPlay,
                        GameVariables.map.get(coord),
                        LandCorner.fromWeb(position)).forceExecute();
               } catch (BUILD e) {
                  e.printStackTrace();
                  throw new Error(e);
               }
            }
            if (GameVariables.turn < 0)
               GameVariables.scenes.gameScene.buildScene.inputController.modes = Set.of(Modes.route);
            break;
         case "city":
            try {
               new Build.BuildCity(GameVariables.playerToPlay,
                     GameVariables.map.get(coord),
                     LandCorner.fromWeb(position)).execute();
            } catch (NOT_ENOUGH_RESSOURCES | BUILD _e) {
               try {
                  new Build.BuildCity(GameVariables.playerToPlay,
                        GameVariables.map.get(coord),
                        LandCorner.fromWeb(position)).forceExecute();
               } catch (BUILD e) {
                  e.printStackTrace();
                  throw new Error(e);
               }
            }
            break;
         case "route":
            try {
               new Build.BuildRoute(GameVariables.playerToPlay,
                     GameVariables.map.get(coord),
                     LandSide.fromWeb(position)).execute();
            } catch (NOT_ENOUGH_RESSOURCES | BUILD _e) {
               try {
                  new Build.BuildRoute(GameVariables.playerToPlay,
                        GameVariables.map.get(coord),
                        LandSide.fromWeb(position)).forceExecute();
               } catch (BUILD e) {
                  e.printStackTrace();
                  throw new Error(e);
               }
            }
            if (GameVariables.turn < 0)
               GameVariables.scenes.gameScene.buildScene.disable();
            if (GameVariables.scenes.gameScene.isRoadBuildingMode() && GameVariables.getMe().freeRoute == 0)
               GameVariables.scenes.gameScene.disableRoadBuildingMode();

            break;
         default:
            throw new Error("unknown type");
      }
   }
}
