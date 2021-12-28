package view.inputs;

import java.awt.event.MouseEvent;
import java.util.Optional;

import globalVariables.GameVariables;
import map.Land.BUILD;
import map.Land.BUILD.CITY_WITHOUT_COLONY;
import map.constructions.City;
import map.constructions.Colony;
import map.constructions.Route;
import view.View;
import view.inputCalculs.MouseControl;
import view.inputCalculs.MouseControl.MousePositionSummary;
import view.painting.jobs.CatanMapJob;

public class BuildInputController extends InputController {
   final View view;
   final CatanMapJob catanMapJob;

   public BuildInputController(final View view, CatanMapJob catanMapJob) {
      this.view = view;
      this.catanMapJob = catanMapJob;
   }

   Optional<MousePositionSummary> oldSummary = Optional.empty();

   @Override
   public void mouseExited(MouseEvent event) {
      this.view.foreground.setBounds(0, 0, 0, 0);
   }

   @Override
   public void mouseDragged(MouseEvent event) {
      this.view.foreground.setBounds(event.getX() + 20, event.getY() + 20, 30, 30);
   }

   @Override
   public void mouseMoved(final MouseEvent event) {

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
                  new Route(GameVariables.playerToPlay)))
         this.catanMapJob.setShadow(summary.nearestLandCoord.get(), summary.nearestLandSide.get(),
               new Route(GameVariables.playerToPlay));
      else if (summary.nearestLandCorner.isPresent())
         if (GameVariables.map.get(summary.nearestLandCoord.get()).canSetBuilding(summary.nearestLandCorner.get(),
               new Colony(GameVariables.playerToPlay)))
            this.catanMapJob.setShadow(summary.nearestLandCoord.get(), summary.nearestLandCorner.get(),
                  new Colony(GameVariables.playerToPlay));
         else if (GameVariables.map.get(summary.nearestLandCoord.get()).canSetBuilding(summary.nearestLandCorner.get(),
               new City(GameVariables.playerToPlay)))
            this.catanMapJob.setShadow(summary.nearestLandCoord.get(), summary.nearestLandCorner.get(),
                  new City(GameVariables.playerToPlay));
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
      final MousePositionSummary summary = MouseControl.getMousePositionSummary(event.getPoint(),
            view.getLandSize(),
            view.getMapCenter());
      if (summary.nearestLandCoord.isEmpty() || (summary.nearestLandCorner.isEmpty()
            && summary.nearestLandSide.isEmpty()))
         return;
      if (summary.nearestLandSide.isPresent()) {
         event.consume();
         this.catanMapJob.removeShadow();
         try {
            GameVariables.map.get(summary.nearestLandCoord.get()).setRoute(summary.nearestLandSide.get(),
                  new Route(GameVariables.playerToPlay));
         } catch (final BUILD e) {
            return;
         }
         view.backgroundPainting.forceUpdatePainting().await();
         view.background.repaint();

      } else if (summary.nearestLandCorner.isPresent()) {
         event.consume();
         this.catanMapJob.removeShadow();
         try {
            GameVariables.map.get(summary.nearestLandCoord.get()).setBuilding(summary.nearestLandCorner.get(),
                  new City(GameVariables.playerToPlay));
         } catch (final CITY_WITHOUT_COLONY _e) {
            try {
               GameVariables.map.get(summary.nearestLandCoord.get()).setBuilding(summary.nearestLandCorner.get(),
                     new Colony(GameVariables.playerToPlay));
            } catch (final BUILD __e) {
               return;
            }
         } catch (final BUILD _e) {
            return;
         }

         view.backgroundPainting.forceUpdatePainting().await();
         view.background.repaint();
      }
   }
}
