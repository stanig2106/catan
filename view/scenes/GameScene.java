package view.scenes;

import util_my.Promise;
import view.Scene;
import view.View;
import view.inputs.CatanMapInputController;
import view.painting.Painting.PaintingJob;
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

      view.backgroundPainting.updatePainting(new CatanMapJob(this.view)).await();
      view.backgroundPainting.addJobs(new GameInterfaceJob()).await();
      view.background.repaint();
      CatanMapInputController catanMapInputController = new CatanMapInputController(view);
      view.content.addMouseMotionListener(catanMapInputController);
      view.content.addMouseListener(catanMapInputController);
      view.content.addMouseWheelListener(catanMapInputController);
   }

   @Override
   public void disable() {

   }

}
