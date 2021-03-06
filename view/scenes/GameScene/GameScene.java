package view.scenes.GameScene;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import globalVariables.GameVariables;

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
   final CatanMapJob catanMapJob = new CatanMapJob(this.view);
   final GameInterfaceJob gameInterfaceJob = new GameInterfaceJob(this);
   public final BuildScene buildScene = new BuildScene(this.view, this, this.catanMapJob);
   public final DicesScene dicesScene = new DicesScene(this.view);

   public GameScene(
         View view) {
      super(view);
   }

   @Override
   public void enable() {
      this.preEnable();

      view.backgroundPainting.updatePainting(new AndJob(this.catanMapJob, gameInterfaceJob))
            .await();
      view.background.repaint();

      final CatanMapInputController catanMapInputController = new CatanMapInputController(view);
      view.content.addMouseMotionListener(catanMapInputController);
      view.content.addMouseListener(catanMapInputController);
      view.content.addMouseWheelListener(catanMapInputController);

      final GameInputController gameInputController = new GameInputController(view, this, gameInterfaceJob);
      view.content.addMouseMotionListener(gameInputController);
      view.content.addMouseListener(gameInputController);
   }

   @Override
   public void disable() {

   }

   private boolean dicesLunched = false;

   public List<Pair<Button, Optional<Promise<Image>>>> getButtons(Dimension dim) {
      final Button dicesButton = new Button("DICES", 65, 65, Button.Position.end, Button.Position.middle,
            -10, -80, dim,
            "Lunch dices");
      dicesButton.disabled = this.gameInterfaceJob.isAllDisabled() || this.getDicesLunched();
      final Button buildButton = new Button("BUILD", 65, 65, Button.Position.end, Button.Position.middle,
            -10, 0, dim,
            "Build");
      buildButton.disabled = this.gameInterfaceJob.isAllDisabled() || !this.getDicesLunched();
      final Button doneButton = new Button("DONE", 65, 65, Button.Position.end, Button.Position.middle,
            -10, 80, dim,
            "Done");
      doneButton.disabled = this.gameInterfaceJob.isAllDisabled() || !this.getDicesLunched();
      return List.of(
            Pair.of(dicesButton, Optional.of(GameInterfaceJob.dicesImage)),
            Pair.of(buildButton,
                  Optional.of(buildScene.enabled ? GameInterfaceJob.cancelImage : GameInterfaceJob.buildImage)),
            Pair.of(doneButton, Optional.empty()));
   }

   public boolean getDicesLunched() {
      return dicesLunched;
   }

   public void setDicesLunched(boolean value) {
      if (this.dicesLunched != value)
         this.gameInterfaceJob.manualReload = true;
      this.dicesLunched = value;
   }

   public void newTurn() {
      this.setDicesLunched(false);
      this.buildScene.disable();
      GameVariables.playerToPlay = Stream.of(GameVariables.players)
            .dropWhile(Predicate.not(GameVariables.playerToPlay::equals)).skip(1)
            .findFirst().orElse(GameVariables.players[0]);
   }
}
