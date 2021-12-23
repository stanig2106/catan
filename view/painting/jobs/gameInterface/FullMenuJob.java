package view.painting.jobs.gameInterface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

import globalVariables.ViewVariables;
import util_my.Button;
import util_my.Promise;
import view.painting.Painting.PaintingJob;

public class FullMenuJob extends PaintingJob {
   static final Promise<Image> ParchemineTexture = ViewVariables.importImage("assets/menu/ParchemineTexture.png");
   static final Promise<Image> woodTexture = ViewVariables.importImage("assets/menu/WoodTexture.jpg");

   final List<Button> buttons = new ArrayList<Button>();

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      g.setColor(new Color(248, 227, 193));
      g.fillRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());
      final Image fullMenuImage = FullMenuJob.ParchemineTexture.await();
      double scale = dim.getWidth() / (double) fullMenuImage.getWidth(imageObserver);
      g.drawImage(fullMenuImage, 0, 0, (int) (dim.getWidth()),
            (int) (fullMenuImage.getHeight(imageObserver) * scale),
            imageObserver);

      this.addButton(new Button(0, 0, 100, 100, "test"));

      this.buttons.forEach(button -> {
         g.drawImage(woodTexture.await(), (int) button.shape.getX(), (int) button.shape.getY(),
               (int) button.shape.getWidth(),
               (int) button.shape.getHeight(), imageObserver);
      });
   }

   public void addButton(Button button) {
      this.buttons.add(button);
   }

}
