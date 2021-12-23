package view.painting.jobs;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;

import map.constructions.Building;
import util_my.directions.LandCorner;
import view.painting.Painting.PaintingJob;

public class OneBuildingJob extends PaintingJob {
   final Building building;
   final LandCorner corner;
   final int size;

   public OneBuildingJob(Building building, int size, LandCorner corner) {
      this.building = building;
      this.corner = corner;
      this.size = size;
   }

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      Image image = this.building.image.await();
      int height = (int) (size / 1.7);
      int width = (int) (size / 1.7);
      g.setComposite(AlphaComposite.SrcOver.derive(0.7f));
      g.drawImage(image, 0, 0, width, height, imageObserver);
   }
}
