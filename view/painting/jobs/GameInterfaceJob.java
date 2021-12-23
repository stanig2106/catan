package view.painting.jobs;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import globalVariables.GameVariables;
import view.painting.Painting.PaintingJob;

public class GameInterfaceJob extends PaintingJob {

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      GameVariables.players.forEach(player -> {

      });
   }

}
