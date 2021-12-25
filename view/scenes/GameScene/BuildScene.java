package view.scenes.GameScene;

import java.awt.Dimension;
import java.awt.*;
import java.awt.image.ImageObserver;

import map.CatanMap;
import player.Player;
import util_my.DrawUtils;
import view.Scene;
import view.View;
import view.inputs.BuildInputController;
import view.painting.Painting.PaintingJob;
import view.painting.jobs.CatanMapJob;
import view.painting.jobs.gameInterface.GameInterfaceJob;
import view.painting.jobs.gameInterface.MenuJob;

public class BuildScene extends Scene {
   final GameScene gameScene;
   public final BuildInputController inputController;
   public boolean enabled = false;

   protected BuildScene(View view, GameScene gameScene, CatanMapJob catanMapJob) {
      super(view);
      this.gameScene = gameScene;

      inputController = new BuildInputController(view, catanMapJob, this);
   }

   public void enable() {
      this.view.addComponentListener(inputController);
      this.view.content.addMouseListener(inputController);
      this.view.content.addMouseMotionListener(inputController);
      this.view.content.addMouseWheelListener(inputController);

      this.view.foregroundPainting.updatePainting(30, 30, new PaintingJob() {
         @Override
         public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
            g.drawImage(MenuJob.woodTexture.await(), 0, 0, (int) dim.getWidth(), (int) dim.getHeight(), imageObserver);
            DrawUtils.drawCenteredImage(g, GameInterfaceJob.buildImage.await(), 20, 20,
                  new Rectangle(new Point(0, 0), dim), imageObserver);
         }
      });

      enabled = true;
   }

   public void disable() {
      this.view.removeComponentListener(inputController);
      this.view.content.removeMouseListener(inputController);
      this.view.content.removeMouseMotionListener(inputController);
      this.view.content.removeMouseWheelListener(inputController);
      this.view.foreground.setBounds(0, 0, 0, 0);
      enabled = false;
   }
}
