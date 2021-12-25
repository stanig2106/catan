package view.inputs;

import java.awt.Cursor;
import java.awt.*;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import globalVariables.GameVariables;
import player.developmentCards.Card;
import player.plays.PlayCard;
import util_my.Box;
import util_my.Button;
import util_my.Pair;
import util_my.Promise;
import util_my.Timeout;
import view.View;
import view.View.ListenerSave;
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
      final var buttons = gameScene.getButtons(this.view.getContentSize());

      this.gameInterfaceJob.overedButton = buttons.stream()
            .filter(pair -> !pair.getKey().disabled && pair.getKey().shape.contains(event.getPoint()))
            .findFirst().map(pair -> pair.getKey());

      if (this.gameInterfaceJob.overedButton.isPresent())
         this.view.content.setCursor(new Cursor(Cursor.HAND_CURSOR));
      else
         this.view.content.setCursor(Cursor.getDefaultCursor());

      if (!this.gameInterfaceJob.getCardDragged())
         this.gameInterfaceJob.setIndexOfOveredCard(indexOfOveredCard(event.getPoint()));

      if (this.view.backgroundPainting.updatePainting().await())
         this.view.background.repaint();

   }

   Point oldPosition = null;
   private boolean dragDisponible = true;
   Optional<ListenerSave> save = Optional.empty();

   @Override
   public void mousePressed(MouseEvent event) {
      final int index;
      if ((index = this.gameInterfaceJob.getIndexOfOveredCard()) == -1)
         return;
      if (GameVariables.playerToPlay.inventory.cards.get(index).getValue()) {
         this.gameInterfaceJob.setCardDragged(true);
         if (view.backgroundPainting.updatePainting().await())
            view.background.repaint();
         System.out.println("save");
         this.save = Optional.of(view.new ListenerSave());
         view.removeAllListener();
         view.content.addMouseListener(this);
         view.content.addMouseMotionListener(this);
      }
   }

   @Override
   public void mouseDragged(MouseEvent event) {
      if (this.gameInterfaceJob.getCardDragged()) {
         view.foreground.setBounds(event.getPoint().x, event.getPoint().y, 50, 50);
      }
   }

   @Override
   public void mouseReleased(MouseEvent event) {
      this.save.ifPresent(save -> save.restore());
      this.save = Optional.empty();
      System.out.println("restore");
      final int index;
      if ((index = this.gameInterfaceJob.getIndexOfOveredCard()) == -1)
         return;
      if (this.gameInterfaceJob.getCardDragged()) {
         this.gameInterfaceJob.setIndexOfOveredCard(-1);
         this.gameInterfaceJob.setCardDragged(false);
         if (event.getPoint().y < view.getContentSize().height - 250)
            new PlayCard(GameVariables.playerToPlay, index).execute();
      }

   }

   @Override
   public void mouseClicked(MouseEvent event) {
      final var buttons = gameScene.getButtons(this.view.getContentSize());

      Optional<Button> clickedButton = buttons.stream()
            .filter(pair -> !pair.getKey().disabled && pair.getKey().shape.contains(event.getPoint()))
            .findFirst().map(pair -> pair.getKey());

      switch (clickedButton.map(button -> button.id).orElse("")) {
         case "BUILD":
            if (this.gameScene.buildScene.enabled)
               this.gameScene.buildScene.disable();
            else
               this.gameScene.buildScene.enable();

            view.backgroundPainting.forceUpdatePainting().await();
            view.background.repaint();
            break;
         case "DICES":
            new Timeout(() -> {
               this.gameInterfaceJob.overedButton = Optional.empty();
               this.gameScene.setDicesLunched(true);
               this.gameInterfaceJob.setAllDisabled(true);

               this.gameScene.dicesScene.enable();

               this.gameInterfaceJob.setAllDisabled(false);
               if (view.backgroundPainting.updatePainting().await())
                  view.background.repaint();
            });
            break;
         case "DONE":
            this.gameScene.newTurn();
            if (view.backgroundPainting.updatePainting().await())
               view.background.repaint();
            break;
         default:
            break;
      }
   }

   int indexOfOveredCard(Point position) {
      final int cardsSize = GameVariables.playerToPlay.inventory.cards.size();
      if (cardsSize == 0)
         return -1;
      if (position.y < view.getContentSize().height - 70 - 40
            - (this.gameInterfaceJob.getIndexOfOveredCard() != -1 ? 50 : 0)
            || position.y > view.getContentSize().height - 70) {
         return -1;
      }
      Box<Double> xOffset = Box.of((cardsSize % 2) / 2.);
      xOffset.value -= (cardsSize - (cardsSize % 2 == 0 ? 1 : 0)) / 2.;

      return IntStream.range(0, cardsSize).filter(i -> {
         xOffset.value++;
         return position.x > (view.getContentSize().width / 2. + (xOffset.value - 1) * 155) - 75
               && position.x < (view.getContentSize().width / 2. + (xOffset.value - 1) * 155) + 75;
      }).findFirst().orElse(-1);
   }
}
