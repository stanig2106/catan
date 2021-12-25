package view.inputs;

import java.awt.event.MouseEvent;
import java.awt.*;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import globalVariables.GameVariables;
import map.Land.BUILD;
import map.Land.BUILD.CITY_WITHOUT_COLONY;
import map.constructions.Building;
import map.constructions.City​​;
import map.constructions.Colony;
import map.constructions.Route;
import player.Inventory.NOT_ENOUGH_RESSOURCES;
import player.plays.Build;
import view.View;
import view.inputCalculs.MouseControl;
import view.inputCalculs.MouseControl.MousePositionSummary;
import view.painting.jobs.CatanMapJob;
import view.scenes.GameScene.BuildScene;

public class BuildInputController extends InputController {
   final View view;
   final CatanMapJob catanMapJob;
   public Set<Modes> modes = Modes.all();
   final BuildScene buildScene;

   public BuildInputController(final View view, CatanMapJob catanMapJob, BuildScene buildScene) {
      this.view = view;
      this.catanMapJob = catanMapJob;
      this.buildScene = buildScene;
   }

   Optional<MousePositionSummary> oldSummary = Optional.empty();

   @Override
   public void mouseExited(MouseEvent event) {
      this.view.foreground.setBounds(0, 0, 0, 0);
   }

   boolean mouseDragOrigineSafe = true;

   @Override
   public void mouseDragged(MouseEvent event) {
      if (!mouseDragOrigineSafe || !view.inSafeZone(event.getPoint())) {
         // this.view.foreground.setBounds(0, 0, 0, 0);
         return;
      }

      this.view.foreground.setBounds(event.getX() + 20, event.getY() + 20, 30, 30);
   }

   @Override
   public void mousePressed(MouseEvent event) {
      mouseDragOrigineSafe = view.inSafeZone(event.getPoint());
   }

   @Override
   public void mouseMoved(final MouseEvent event) {
      if (!view.inSafeZone(event.getPoint())) {
         oldSummary = Optional.empty();
         this.catanMapJob.removeShadow();
         this.view.foreground.setBounds(0, 0, 0, 0);
         return;
      }

      this.view.foreground.setBounds(event.getX() + 20, event.getY() + 20, 30, 30);

      final MousePositionSummary summary = MouseControl.getMousePositionSummary(event.getPoint(),
            view.getLandSize(),
            view.getMapCenter());
      if (oldSummary.map(oldSummary -> oldSummary.same(summary)).orElse(false))
         return;
      oldSummary = Optional.of(summary);
      if (summary.nearestLandCoord.isEmpty() || (summary.nearestLandCorner.isEmpty()
            && summary.nearestLandSide.isEmpty())) {
         this.catanMapJob.removeShadow();
         if (view.backgroundPainting.updatePainting().await())
            view.background.repaint();
         return;
      }

      if (summary.nearestLandSide.isPresent()
            && GameVariables.map.get(summary.nearestLandCoord.get()).canSetRoute(summary.nearestLandSide.get(),
                  new Route(GameVariables.playerToPlay))
            && this.modes.contains(Modes.route) && !(GameVariables.turn < 0 &&
                  Stream.of(summary.nearestLandSide.get().getAdjacentsCorners())
                        .map(adjacentLandCorner -> GameVariables.map.get(summary.nearestLandCoord.get()).corners
                              .get(adjacentLandCorner))
                        .noneMatch(corner -> corner.building
                              .map(building -> GameVariables.playerToPlay.buildings.getLast()
                                    .equals(building))
                              .orElse(false))))
         this.catanMapJob.setShadow(summary.nearestLandCoord.get(), summary.nearestLandSide.get(),
               new Route(GameVariables.playerToPlay));
      else if (summary.nearestLandCorner.isPresent())
         if (GameVariables.map.get(summary.nearestLandCoord.get()).canSetBuilding(summary.nearestLandCorner.get(),
               new Colony(GameVariables.playerToPlay)) && this.modes.contains(Modes.colony))
            this.catanMapJob.setShadow(summary.nearestLandCoord.get(), summary.nearestLandCorner.get(),
                  new Colony(GameVariables.playerToPlay));
         else if (GameVariables.map.get(summary.nearestLandCoord.get()).canSetBuilding(summary.nearestLandCorner.get(),
               new City​​(GameVariables.playerToPlay)) && this.modes.contains(Modes.city))
            this.catanMapJob.setShadow(summary.nearestLandCoord.get(), summary.nearestLandCorner.get(),
                  new City​​(GameVariables.playerToPlay));
         else
            this.catanMapJob.removeShadow();
      else
         this.catanMapJob.removeShadow();

      if (view.backgroundPainting.updatePainting().await())
         view.background.repaint();
   }

   //
   //
   //

   @Override
   public void mouseClicked(final MouseEvent event) {
      if (!view.inSafeZone(event.getPoint()))
         return;
      final MousePositionSummary summary = MouseControl.getMousePositionSummary(event.getPoint(),
            view.getLandSize(),
            view.getMapCenter());
      if (summary.nearestLandCoord.isEmpty() || (summary.nearestLandCorner.isEmpty()
            && summary.nearestLandSide.isEmpty()))
         return;
      if (summary.nearestLandSide.isPresent() && this.modes.contains(Modes.route)) {
         this.catanMapJob.removeShadow();
         if (GameVariables.turn < 0 &&
               Stream.of(summary.nearestLandSide.get().getAdjacentsCorners())
                     .map(adjacentLandCorner -> GameVariables.map.get(summary.nearestLandCoord.get()).corners
                           .get(adjacentLandCorner))
                     .noneMatch(corner -> corner.building
                           .map(building -> GameVariables.playerToPlay.buildings.getLast()
                                 .equals(building))
                           .orElse(false)))
            return;
         try {
            GameVariables.map.get(summary.nearestLandCoord.get()).setRoute(summary.nearestLandSide.get(),
                  new Route(GameVariables.playerToPlay));
            if (GameVariables.turn < 0)
               this.buildScene.disable();
         } catch (final BUILD e) {
            return;
         }
         view.backgroundPainting.forceUpdatePainting().await();
         view.background.repaint();

      } else if (summary.nearestLandCorner.isPresent()
            && (this.modes.contains(Modes.city) || this.modes.contains(Modes.colony))) {
         this.catanMapJob.removeShadow();
         try {
            new Build.BuildCity(GameVariables.playerToPlay, GameVariables.map.get(summary.nearestLandCoord.get()),
                  summary.nearestLandCorner.get()).execute();
         } catch (final CITY_WITHOUT_COLONY _e) {
            try {
               new Build.BuildColony(GameVariables.playerToPlay, GameVariables.map.get(summary.nearestLandCoord.get()),
                     summary.nearestLandCorner.get()).execute();
               if (GameVariables.turn < 0)
                  this.modes = Set.of(Modes.route);
            } catch (final BUILD __e) {
               return;
            } catch (NOT_ENOUGH_RESSOURCES e) {
               System.out.println("pas assez de ressource");
            }
         } catch (final BUILD | NOT_ENOUGH_RESSOURCES e) {
            if (!(e instanceof NOT_ENOUGH_RESSOURCES)
                  && !Stream.of(e.getSuppressed())
                        .anyMatch(suppressed -> (suppressed instanceof NOT_ENOUGH_RESSOURCES)))
               return;
            System.out.println("pas assez de ressource");
         }

         view.backgroundPainting.forceUpdatePainting().await();
         view.background.repaint();
      }
   }

   public static enum Modes {
      route, colony, city;

      static Set<Modes> all() {
         return Set.of(Modes.values());
      }
   }

}
