package view.scenes;

import java.util.List;

import java.awt.Image;
import java.awt.*;
import util_my.Button;
import util_my.Pair;
import util_my.Promise;
import view.Scene;
import view.View;
import view.inputs.CatanMapInputController;
import view.inputs.GameInputController;
import view.painting.Painting.PaintingJob;
import view.painting.jobs.AndJob;
import view.painting.jobs.CatanMapJob;
import view.painting.jobs.LoadingJob;
import view.painting.jobs.gameInterface.GameInterfaceJob;

public class GameScene extends Scene {

   public GameScene(View view) {
      super(view);

   }

   @Override
   public void enable() {
      this.preEnable();
      final GameInterfaceJob gameInterfaceJob = new GameInterfaceJob(this);
      view.backgroundPainting.updatePainting(new AndJob(new CatanMapJob(this.view), gameInterfaceJob))
            .await();
      view.background.repaint();
      final CatanMapInputController catanMapInputController = new CatanMapInputController(view);
      view.content.addMouseMotionListener(catanMapInputController);
      view.content.addMouseListener(catanMapInputController);
      view.content.addMouseWheelListener(catanMapInputController);
      final GameInputController gameInputController = new GameInputController(view, this);
      view.content.addMouseMotionListener(gameInputController);
      view.content.addMouseListener(gameInputController);
   }

   @Override
   public void disable() {

   }

   public List<Pair<Button, Promise<Image>>> getButtons(Dimension dim) {
      final Button dicesButton = new Button("DICES", 65, 65, Button.Position.start, Button.Position.middle,
            10, -40, dim,
            "Lunch dices");
      dicesButton.disabled = true;
      final Button buildButton = new Button("BUILD", 65, 65, Button.Position.start, Button.Position.middle,
            10, 40, dim,
            "Build");
      return List.of(
            Pair.of(dicesButton, GameInterfaceJob.dicesImage),
            Pair.of(buildButton, GameInterfaceJob.buildImage));
   }
}
