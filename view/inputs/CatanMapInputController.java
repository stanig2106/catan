package view.inputs;

import java.awt.*;
import view.View;
import view.painting.jobs.CatanMapJob;
import view.painting.jobs.gameInterface.GameInterfaceJob;

import java.awt.event.*;
import java.util.Optional;

import util_my.Timeout;

public class CatanMapInputController extends InputController {
   final View view;

   public CatanMapInputController(View view) {
      this.view = view;
   }

   public void zoomCallback(boolean zoomUp, Point origine) {
      if ((view.zoomLevel == 0.75 && !zoomUp) || (view.zoomLevel == 3 && zoomUp))
         return;

      double old_xDistanceToCenter = (origine.getX() - view.getMapCenter().getX());
      double old_yDistanceToCenter = (origine.getY() - view.getMapCenter().getY());
      double oldZoomLevel = view.zoomLevel;

      view.zoomLevel += zoomUp ? 0.25 : -0.25;
      view.landSizeCalculator.readjustZoomLevel();

      double xDistanceToCenter = old_xDistanceToCenter * view.zoomLevel / oldZoomLevel;
      double yDistanceToCenter = old_yDistanceToCenter * view.zoomLevel / oldZoomLevel;

      view.mapOffset.translate((int) Math.round(old_xDistanceToCenter - xDistanceToCenter),
            (int) Math.round(old_yDistanceToCenter - yDistanceToCenter));

      view.mapCenterCalculator.needRecalculate = true;
      view.landSizeCalculator.needRecalculate = true;

      view.backgroundPainting.updatePainting().await();
      view.background.repaint();
   }

   private boolean wheelDisponible = true;

   @Override
   public void mouseWheelMoved(MouseWheelEvent event) {
      int notches = event.getWheelRotation();

      if (!wheelDisponible)
         return;

      this.zoomCallback(notches < 0, event.getPoint());

      this.wheelDisponible = false;

      new Timeout(() -> {
         this.wheelDisponible = true;
      }, 50);
   }

   public void moveCallback(int xOffset, int yOffset) {
      view.mapOffset.translate(xOffset, yOffset);

      view.mapCenterCalculator.needRecalculate = true;
      view.backgroundPainting.updatePainting().await();
      view.background.repaint();
   }

   @Override
   public void mouseReleased(MouseEvent event) {
      this.oldPosition = null;
   }

   Point oldPosition = null;
   private boolean dragDisponible = true;

   @Override
   public void mouseDragged(MouseEvent event) {
      if (!dragDisponible)
         return;
      if (oldPosition == null) {
         this.oldPosition = event.getPoint();
         return;
      }

      moveCallback(event.getX() - (int) this.oldPosition.getX(),
            event.getY() - (int) this.oldPosition.getY());
      this.oldPosition = event.getPoint();
      new Timeout(() -> {
         this.dragDisponible = true;
      }, 2);
   }

}
