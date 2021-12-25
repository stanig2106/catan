package view.scenes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.stream.Stream;

import globalVariables.ViewVariables;
import util_my.Button;
import util_my.DrawUtils;
import view.Scene;
import view.View;
import view.inputs.InputController;
import view.inputs.StartMenuInputController;
import view.painting.Painting.PaintingJob;
import view.painting.jobs.gameInterface.MenuJob;

public class StartMenuScene extends Scene {

   public StartMenuScene(View view) {
      super(view);
   }

   private Button[] getButton(Dimension dim) {
      return Stream.of(
            new Button("PLAY", 200, 100, Button.Position.end, Button.Position.end, -50, -50, dim, "Play"))
            .toArray(Button[]::new);
   }

   public void enable() {
      this.preEnable();

      MenuJob menuJob = new MenuJob() {
         public void paintContent(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
            Stream.of(getButton(dim)).forEach(button -> {
               g.drawImage(woodTexture.await(), (int) button.shape.getX(), (int) button.shape.getY(),
                     (int) button.shape.getWidth(),
                     (int) button.shape.getHeight(), imageObserver);
               g.setFont(ViewVariables.GameFont.deriveFont(36f));
               g.setColor(new Color(250, 233, 232));
               DrawUtils.drawCenteredString(g, button.content, button.shape);
            });
         };
      };
      this.view.backgroundPainting.updatePainting(this.view.getContentSize(), menuJob).await();
      this.view.background.repaint();
      InputController inputController = new StartMenuInputController(this.view, this::getButton);
      this.view.addComponentListener(inputController);
      this.view.content.addMouseListener(inputController);
      this.view.content.addMouseMotionListener(inputController);
   }

}
