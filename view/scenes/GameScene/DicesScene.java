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

   @Override
   public void enable() {
      ListenerSave backupListener = this.view.new ListenerSave();
      this.preEnable();

      LunchDices play = new LunchDices(GameVariables.playerToPlay);
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

      dicesJob.showResult(play.firstDice, play.secondDice);
      if (view.backgroundPainting.updatePainting().await())
         view.background.repaint();
      new Timeout(1000).join();
      play.execute();
      if (view.backgroundPainting.updatePainting(backupJobs).await())
         view.background.repaint();

      backupListener.restore();
   }

   @Override
   public void disable() {
   }

}
