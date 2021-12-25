package view.scenes.GameScene;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import globalVariables.GameVariables;
import online.Online;
import player.Player;

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

   public GameScene(View view) {
      super(view);
   }

   public void enable() {
      this.preEnable();
      Online.watchPlays().await();
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

   private boolean dicesLunched = false;

   public List<Pair<Button, Optional<Promise<Image>>>> getButtons(Dimension dim) {
      final Button dicesButton = new Button("DICES", 65, 65, Button.Position.end, Button.Position.middle,
            -10, -160, dim,
            "Lunch dices");
      dicesButton.disabled = this.gameInterfaceJob.isAllDisabled() || this.getDicesLunched() || GameVariables.turn < 0
            || !(GameVariables.playerToPlay instanceof Player.Me);

      //

      final Button buildButton = new Button("BUILD", 65, 65, Button.Position.end, Button.Position.middle,
            -10, -80, dim,
            "Build");
      buildButton.disabled = this.gameInterfaceJob.isAllDisabled() || !this.getDicesLunched() || GameVariables.turn < 0
            || !(GameVariables.playerToPlay instanceof Player.Me);

      //

      final Button cardButton = new Button("CARD", 65, 65, Button.Position.end, Button.Position.middle,
            -10, 0, dim,
            "Card");
      cardButton.disabled = this.gameInterfaceJob.isAllDisabled() || !this.getDicesLunched() || GameVariables.turn < 0
      // || !GameVariables.playerToPlay.canBuyCard()
      // TODO: uncomment
      ;

      //

      final Button tradeButton = new Button("TRADE", 65, 65, Button.Position.end, Button.Position.middle,
            -10, 80, dim,
            "Trade");
      tradeButton.disabled = this.gameInterfaceJob.isAllDisabled() || !this.getDicesLunched() || GameVariables.turn < 0;

      //

      final Button doneButton = new Button("DONE", 65, 65, Button.Position.end, Button.Position.middle,
            -10, 160, dim,
            "Done");
      doneButton.disabled = this.gameInterfaceJob.isAllDisabled() || !this.getDicesLunched()
            || (GameVariables.turn == -2 && GameVariables.playerToPlay.routes.size() < 1)
            || (GameVariables.turn == -1 && GameVariables.playerToPlay.routes.size() < 2)
            || !(GameVariables.playerToPlay instanceof Player.Me);

      //

      return List.of(
            Pair.of(dicesButton, Optional.of(GameInterfaceJob.dicesImage)),
            Pair.of(buildButton,
                  Optional.of(buildScene.enabled ? GameInterfaceJob.cancelImage : GameInterfaceJob.buildImage)),
            Pair.of(cardButton, Optional.empty()),
            Pair.of(tradeButton, Optional.empty()),
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
      this.buildScene.disable();

      if (!(GameVariables.playerToPlay instanceof Player.Me)) {
         Online.watchPlays();
         return;
      }

      if (GameVariables.turn < 0) {
         this.setDicesLunched(true);
         this.buildScene.inputController.modes = Set.of(BuildInputController.Modes.colony);
         this.buildScene.enable();
      } else {
         this.setDicesLunched(false);
      }

   }

   private boolean roadBuildingMode = false;

   public void enableRoadBuildingMode() {
      this.roadBuildingMode = true;
      this.gameInterfaceJob.setAllDisabled(true);
      this.buildScene.inputController.modes = Set.of(BuildInputController.Modes.route);
      this.buildScene.enable();
   }

   public void disableRoadBuildingMode() {
      this.roadBuildingMode = false;
      this.gameInterfaceJob.setAllDisabled(false);
      this.buildScene.disable();
   }

   public boolean isRoadBuildingMode() {
      return roadBuildingMode;
   }
}
