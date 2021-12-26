package view.inputs;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

import util_my.Button;
import util_my.Pair;
import util_my.Promise;
import util_my.Timeout;
import view.View;
import view.painting.jobs.gameInterface.GameInterfaceJob;
import view.scenes.GameScene.GameScene;

public class GameInputController extends InputController {
   final GameScene gameScene;
   final View view;
   final GameInterfaceJob gameInterfaceJob;

   public GameInputController(View view, GameScene gameScene, GameInterfaceJob gameInterfaceJob) {
      this.view = view;
      this.gameScene = gameScene;
      this.gameInterfaceJob = gameInterfaceJob;
   }

   @Override
   public void mouseMoved(MouseEvent event) {
      final List<Pair<Button, Promise<Image>>> buttons = gameScene.getButtons(this.view.getContentSize());

      this.gameInterfaceJob.overedButton = buttons.stream()
            .filter(pair -> !pair.getKey().disabled && pair.getKey().shape.contains(event.getPoint()))
            .findFirst().map(pair -> pair.getKey());

      if (this.gameInterfaceJob.overedButton.isPresent())
         this.view.content.setCursor(new Cursor(Cursor.HAND_CURSOR));
      else
         this.view.content.setCursor(Cursor.getDefaultCursor());

      if (this.view.backgroundPainting.updatePainting().await())
         this.view.background.repaint();
   }

   @Override
   public void mouseClicked(MouseEvent event) {
      final List<Pair<Button, Promise<Image>>> buttons = gameScene.getButtons(this.view.getContentSize());

      Optional<Button> clickedButton = buttons.stream()
            .filter(pair -> !pair.getKey().disabled && pair.getKey().shape.contains(event.getPoint()))
            .findFirst().map(pair -> pair.getKey());

      switch (clickedButton.map(button -> button.id).orElse("")) {
         case "BUILD":
            if (this.gameScene.buildScene.enabled)
               this.gameScene.buildScene.disable();
            else
               this.gameScene.buildScene.enable();
            break;
         case "DICES":
            new Timeout(() -> {
               this.gameInterfaceJob.overedButton = Optional.empty();
               this.gameScene.dicesScene.enable();
            });
         default:
            break;
      }

   }
}
