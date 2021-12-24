package view.painting.jobs.gameInterface;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import globalVariables.ViewVariables;
import util_my.Button;
import util_my.DrawUtils;
import view.painting.Painting.PaintingJob;

public class GameInterfaceJob extends PaintingJob {

      @Override
      public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
            Button buildButton = new Button("BUILD", 50, 50, Button.Position.start, Button.Position.middle,
                        10, 0, dim,
                        "Build");
            g.drawImage(MenuJob.woodTexture.await(), (int) buildButton.shape.getX(), (int) buildButton.shape.getY(),
                        (int) buildButton.shape.getWidth(),
                        (int) buildButton.shape.getHeight(), imageObserver);
            g.setFont(ViewVariables.GameFont.deriveFont(16f));
            DrawUtils.drawCenteredString(g, buildButton.content, buildButton.shape);
      }

}
