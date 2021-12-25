package view.scenes.GameScene;

import java.util.stream.IntStream;

import globalVariables.GameVariables;
import player.Player;
import player.plays.LunchDices;
import util_my.Timeout;
import view.Scene;
import view.View;
import view.View.ListenerSave;
import view.painting.Painting.PaintingJob;
import view.painting.jobs.gameInterface.DicesJob;

public class DicesScene extends Scene {

   protected DicesScene(View view) {
      super(view);
   }

   public void enable(int dice1, int dice2) {
      ListenerSave backupListener = this.view.new ListenerSave();
      this.preEnable();

      final DicesJob dicesJob = new DicesJob();
      PaintingJob backupJobs = view.backgroundPainting.getJobs();

      view.backgroundPainting.addJobs(dicesJob).await();
      view.background.repaint();
      new Timeout(70).join();

      IntStream.range(0, 5).forEach(i -> {
         dicesJob.nextRandomDices();
         view.backgroundPainting.forceUpdatePainting().await();
         view.background.repaint();
         new Timeout(70).join();
      });

      dicesJob.showResult(dice1, dice2);
      if (view.backgroundPainting.updatePainting().await())
         view.background.repaint();

      new Timeout(1000).join();
      while (view.jobSave.isPresent())
         new Timeout(500).join();

      if (view.backgroundPainting.updatePainting(backupJobs).await())
         view.background.repaint();
      backupListener.restore();
   }

}
