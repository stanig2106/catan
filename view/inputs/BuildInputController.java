package view.inputs;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Optional;
import java.util.function.Function;

import Jama.Matrix;
import globalVariables.GameVariables;
import globalVariables.ViewVariables;
import map.Land.BUILD;
import map.Land.BUILD.CITY_WITHOUT_COLONY;
import map.constructions.Building;
import map.constructions.City​​;
import map.constructions.Colony;
import map.constructions.Route;
import player.Player;
import util_my.Coord;
import util_my.Timeout;
import util_my.directions.LandCorner;
import util_my.directions.LandSide;
import view.View;
import view.inputCalculs.MouseControl;
import view.inputCalculs.MouseControl.MousePositionSummary;
import view.painting.Painting;
import view.painting.Painting.PaintingJob;
import view.painting.jobs.OneBuildingJob;
import view.painting.jobs.OneRouteJob;

public class BuildInputController extends InputController {
   final BuildInputController me = this;

   public BuildInputController(final View view) {
      super(view);
   }

   private void displayRoute(final Coord coord, final LandSide landSide) {
      final int size = view.getLandSize();
      final PaintingJob job = new OneRouteJob(new Route(me.player.get()), size, landSide);
      final int routeHeight = size;
      final int routeWidth = (int) (size / 3.);
      final Dimension paintingDim;
      switch (landSide) {
         case topLeft:
         case bottomRight:
         case topRight:
         case bottomLeft:
            paintingDim = new Dimension(
                  (int) (routeHeight * Math.sin(Math.toRadians(60)) +
                        routeWidth * Math.cos(Math.toRadians(60))),
                  (int) (routeHeight * Math.cos(Math.toRadians(60)) +
                        routeWidth * Math.sin(Math.toRadians(60))));
            break;
         case left:
         case right:
            paintingDim = new Dimension(routeWidth, routeHeight);
            break;
         default:
            throw new Error("Unknown side");
      }
      final Point mapCenter = view.getMapCenter();

      view.foregroundPainting.data = Painting.newPainting(paintingDim, job).await();
      final Matrix positionMatrix = ViewVariables.hexToPixelMatrix.times(coord.toMatrix()).times(size);

      final Point position = new Point((int) positionMatrix.get(0, 0), (int) positionMatrix.get(1, 0));
      position.translate((int) mapCenter.getX(), (int) mapCenter.getY());
      switch (landSide) {
         case topLeft:
            position.translate((int) -(Math.sqrt(3) * size / 2. + routeWidth / 4.),
                  -(int) (size + routeWidth / 4.));
            break;
         case topRight:
            position.translate((int) -(routeWidth / 2.50),
                  -(int) (size + routeWidth / 3.5));
            break;
         case right:
            position.translate((int) (Math.sqrt(3) * size / 2. - routeWidth / 2.),
                  (int) -(routeHeight / 2.));
            break;
         case left:
            position.translate((int) -(Math.sqrt(3) * size / 2. + routeWidth / 2.),
                  (int) -(routeHeight / 2.));
            break;
         case bottomLeft:
            position.translate((int) -(Math.sqrt(3) * size / 2. + routeWidth / 2.5),
                  (int) (routeWidth * 1.2));
            break;
         case bottomRight:
            position.translate((int) -(routeWidth / 3.5),
                  (int) (routeWidth * 1.2));
            break;
         default:
            throw new Error("Unknown side");
      }
      view.foreground.setBounds(new Rectangle(position, paintingDim));
   }

   private void displayColony(final Coord coord, final LandCorner landCorner) {
      displayBuilding(coord, landCorner, Colony::new);
   }

   private void displayCity(final Coord coord, final LandCorner landCorner) {
      displayBuilding(coord, landCorner, City​​::new);
   }

   private void displayBuilding(final Coord coord, final LandCorner landCorner,
         Function<Player, Building> buildingNew) {
      final int size = view.getLandSize();
      final PaintingJob job = new OneBuildingJob(buildingNew.apply(this.player.get()), size, landCorner);

      final int buildingHeight = (int) (size / 1.7);
      final int buildingWidth = (int) (size / 1.7);
      final Dimension paintingDim = new Dimension(buildingWidth, buildingHeight);

      final Point mapCenter = view.getMapCenter();

      view.foregroundPainting.data = Painting.newPainting(buildingWidth, buildingHeight, job).await();
      final Matrix positionMatrix = ViewVariables.hexToPixelMatrix.times(coord.toMatrix()).times(size);

      final Point position = new Point((int) positionMatrix.get(0, 0), (int) positionMatrix.get(1, 0));
      position.translate((int) mapCenter.getX(), (int) mapCenter.getY());
      switch (landCorner) {
         case topLeft:
            position.translate((int) (-buildingWidth / 2. - size * 0.87),
                  (int) (-buildingHeight / 2. - size / 1.9));
            break;
         case top:
            position.translate(-(int) (buildingWidth / 2.),
                  (int) -(buildingHeight / 2. + size));
            break;
         case topRight:
            position.translate((int) (-buildingWidth / 2. - size * 1.15) + size * 2,
                  (int) (-buildingHeight / 2. - size / 1.9));
            break;
         case bottomLeft:
            position.translate((int) (-buildingWidth / 2. - size * 0.87),
                  (int) (-buildingHeight / 2. - size / 1.9 + size));
            break;
         case bottom:
            position.translate(-(int) (buildingWidth / 2.),
                  (int) -(buildingWidth / 2. - size));
            break;
         case bottomRight:
            position.translate((int) (-buildingWidth / 2. - size * 1.15) + size * 2,
                  (int) (-buildingHeight / 2. - size / 1.9 + size));
            break;
         default:
            throw new Error("Unknown side");
      }
      view.foreground.setBounds(new Rectangle(position, paintingDim));
   }

   Optional<MousePositionSummary> oldSummary = Optional.empty();

   @Override
   public void mouseDragged(MouseEvent event) {
      view.foreground.setBounds(0, 0, 0, 0);
   }

   @Override
   public void mouseReleased(final MouseEvent event) {
      oldSummary = Optional.empty();
      this.mouseMoved(event);
   }

   int oldLandSize = view.getLandSize();

   @Override
   public void mouseWheelMoved(final MouseWheelEvent event) {
      if (this.oldLandSize == view.getLandSize())
         return;
      this.oldLandSize = view.getLandSize();

      final MousePositionSummary summary = oldSummary.orElse(MouseControl.getMousePositionSummary(event.getPoint(),
            view.getLandSize(),
            view.getMapCenter()));
      oldSummary = Optional.of(summary);
      if (summary.nearestLandCoord.isEmpty() || (summary.nearestLandCorner.isEmpty()
            && summary.nearestLandSide.isEmpty())) {
         view.foreground.setBounds(0, 0, 0, 0);
         return;
      }

      if (summary.nearestLandSide.isPresent()
            && GameVariables.map.get(summary.nearestLandCoord.get()).canSetRoute(summary.nearestLandSide.get(),
                  new Route(this.player.get())))
         this.displayRoute(summary.nearestLandCoord.get(), summary.nearestLandSide.get());
      else if (summary.nearestLandCorner.isPresent())
         if (GameVariables.map.get(summary.nearestLandCoord.get()).canSetBuilding(summary.nearestLandCorner.get(),
               new Colony(this.player.get())))
            this.displayColony(summary.nearestLandCoord.get(), summary.nearestLandCorner.get());
         else if (GameVariables.map.get(summary.nearestLandCoord.get()).canSetBuilding(summary.nearestLandCorner.get(),
               new City​​(this.player.get())))
            this.displayCity(summary.nearestLandCoord.get(), summary.nearestLandCorner.get());
         else
            view.foreground.setBounds(0, 0, 0, 0);
      else
         view.foreground.setBounds(0, 0, 0, 0);
   }

   @Override
   public void mouseMoved(final MouseEvent event) {
      final MousePositionSummary summary = MouseControl.getMousePositionSummary(event.getPoint(),
            view.getLandSize(),
            view.getMapCenter());
      if (oldSummary.map(oldSummary -> oldSummary.same(summary)).orElse(false))
         return;
      oldSummary = Optional.of(summary);
      if (summary.nearestLandCoord.isEmpty() || (summary.nearestLandCorner.isEmpty()
            && summary.nearestLandSide.isEmpty())) {
         view.foreground.setBounds(0, 0, 0, 0);
         return;
      }

      if (summary.nearestLandSide.isPresent()
            && GameVariables.map.get(summary.nearestLandCoord.get()).canSetRoute(summary.nearestLandSide.get(),
                  new Route(this.player.get())))
         this.displayRoute(summary.nearestLandCoord.get(), summary.nearestLandSide.get());
      else if (summary.nearestLandCorner.isPresent())
         if (GameVariables.map.get(summary.nearestLandCoord.get()).canSetBuilding(summary.nearestLandCorner.get(),
               new Colony(this.player.get())))
            this.displayColony(summary.nearestLandCoord.get(), summary.nearestLandCorner.get());
         else if (GameVariables.map.get(summary.nearestLandCoord.get()).canSetBuilding(summary.nearestLandCorner.get(),
               new City​​(this.player.get())))
            this.displayCity(summary.nearestLandCoord.get(), summary.nearestLandCorner.get());
         else
            view.foreground.setBounds(0, 0, 0, 0);
      else
         view.foreground.setBounds(0, 0, 0, 0);
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
         new Timeout(() -> view.foreground.setBounds(0, 0, 0, 0), 150);
         try {
            GameVariables.map.get(summary.nearestLandCoord.get()).setRoute(summary.nearestLandSide.get(),
                  new Route(this.player.get()));
         } catch (final BUILD e) {
            return;
         }

         view.backgroundPainting.data.forceUpdatePainting().await();
         view.background.repaint();

      } else if (summary.nearestLandCorner.isPresent()) {
         event.consume();
         new Timeout(() -> view.foreground.setBounds(0, 0, 0, 0), 150);
         try {
            GameVariables.map.get(summary.nearestLandCoord.get()).setBuilding(summary.nearestLandCorner.get(),
                  new City​​(this.player.get()));
         } catch (final CITY_WITHOUT_COLONY _e) {
            try {
               GameVariables.map.get(summary.nearestLandCoord.get()).setBuilding(summary.nearestLandCorner.get(),
                     new Colony(this.player.get()));
            } catch (final BUILD __e) {
               return;
            }
         } catch (final BUILD _e) {
            return;
         }

         view.backgroundPainting.data.forceUpdatePainting().await();
         view.background.repaint();
      }
   }
}
