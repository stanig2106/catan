package view.scenes.GameScene;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import globalVariables.GameVariables;

import java.awt.Image;
import java.awt.*;
import util_my.Button;
import util_my.Pair;
import util_my.Promise;
import view.Scene;
import view.View;
import view.inputs.BuildInputController;
import view.inputs.CatanMapInputController;
import view.inputs.GameInputController;
import view.painting.jobs.AndJob;
import view.painting.jobs.CatanMapJob;
import view.painting.jobs.gameInterface.GameInterfaceJob;

public class GameScene extends Scene {
   final CatanMapJob catanMapJob = new CatanMapJob(this.view);
   public final GameInterfaceJob gameInterfaceJob = new GameInterfaceJob(this);

   public final BuildScene buildScene = new BuildScene(this.view, this, this.catanMapJob);
   public final DicesScene dicesScene = new DicesScene(this.view);

   public GameScene(
         View view) {
      super(view);
   }

   @Override
   public void enable() {
      this.preEnable();
      this.newTurn();
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
      dicesButton.disabled = this.gameInterfaceJob.isAllDisabled() || this.getDicesLunched() || GameVariables.turn < 0;
      final Button buildButton = new Button("BUILD", 65, 65, Button.Position.end, Button.Position.middle,
            -10, 0, dim,
            "Build");
      buildButton.disabled = this.gameInterfaceJob.isAllDisabled() || !this.getDicesLunched() || GameVariables.turn < 0;
      final Button doneButton = new Button("DONE", 65, 65, Button.Position.end, Button.Position.middle,
            -10, 80, dim,
            "Done");
      doneButton.disabled = this.gameInterfaceJob.isAllDisabled() || !this.getDicesLunched()
            || (GameVariables.turn == -2 && GameVariables.playerToPlay.routes.size() < 1)
            || (GameVariables.turn == -1 && GameVariables.playerToPlay.routes.size() < 2);
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
      GameVariables.nextPlayer();
      this.buildScene.disable();
      GameVariables.playerToPlay.updateCards();
      if (GameVariables.turn < 0) {
         GameVariables.playerToPlay.freeColony++;
         GameVariables.playerToPlay.freeRoute++;
         this.setDicesLunched(true);
         this.buildScene.inputController.modes = Set.of(BuildInputController.Modes.colony);
         this.buildScene.enable();
      } else {
         this.setDicesLunched(false);
      }
   }
}
