package view.scenes.GameScene;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.awt.*;
import java.util.Optional;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import globalVariables.GameVariables;
import globalVariables.ViewVariables;
import online.Online;
import player.Player;
import util_my.Box;
import util_my.Button;
import util_my.DrawUtils;
import util_my.StreamUtils;
import view.Scene;
import view.View.ListenerSave;
import view.inputCalculs.MouseControl;
import view.inputCalculs.MouseControl.MousePositionSummary;
import view.inputs.InputController;
import view.painting.Painting.PaintingJob;
import view.painting.jobs.gameInterface.MenuJob;

public class RobberScene extends Scene {
   Optional<ListenerSave> listenerSave;
   Optional<PaintingJob> paintingSave;

   public RobberScene() {
      super(GameVariables.view);
   }

   public void enable() {
      GameVariables.scenes.gameScene.gameInterfaceJob.setAllDisabled(true);
      this.listenerSave = Optional.of(view.new ListenerSave());
      this.paintingSave = Optional.of(view.backgroundPainting.getJobs());

      GameVariables.scenes.gameScene.buildScene.disable();
      GameVariables.scenes.gameScene.gameInputController.sudoDisable = true;

      InputController robberInputController = new view.inputs.InputController() {
         Optional<MousePositionSummary> oldSummary = Optional.empty();

         @Override
         public void mouseMoved(MouseEvent event) {
            final MousePositionSummary summary = MouseControl.getMousePositionSummary(event.getPoint(),
                  view.getLandSize(),
                  view.getMapCenter());
            if (oldSummary.map(oldSummary -> oldSummary.same(summary)).orElse(false))
               return;
            oldSummary = Optional.of(summary);
            if (summary.nearestLandCoord.isEmpty())
               GameVariables.scenes.gameScene.catanMapJob.removeShadow();
            else
               GameVariables.scenes.gameScene.catanMapJob.setShadowRobber(summary.nearestLandCoord.get());
         }

         @Override
         public void mouseClicked(MouseEvent event) {
            final MousePositionSummary summary = MouseControl.getMousePositionSummary(event.getPoint(),
                  view.getLandSize(),
                  view.getMapCenter());
            if (summary.nearestLandCoord.isEmpty())
               return;

            if (summary.nearestLandCoord.get().equals(GameVariables.map.robber.position.coord))
               return;
            else {
               GameVariables.map.robber.position = GameVariables.map.get(summary.nearestLandCoord.get());
               Online.placeRobber(summary.nearestLandCoord.get());
            }
            GameVariables.scenes.gameScene.catanMapJob.removeShadow();

            enableStealScene();
         }
      };
      view.content.addMouseListener(robberInputController);
      view.content.addMouseMotionListener(robberInputController);
   }

   public void disable() {
      GameVariables.scenes.gameScene.gameInterfaceJob.setAllDisabled(false);
      GameVariables.scenes.gameScene.gameInputController.sudoDisable = false;

      this.listenerSave.ifPresent(save -> save.restore());
      this.listenerSave = Optional.empty();

      this.paintingSave.ifPresent(save -> view.backgroundPainting.updatePainting(save).await());
      this.paintingSave = Optional.empty();
      view.background.repaint();
   }

   public void enableStealScene() {
      Set<Player> canSteal = GameVariables.map.robber.position.corners.values().stream()
            .filter(corner -> corner.building.isPresent() && !(corner.building.get().owner instanceof Player.Me)
                  && !corner.building.get().owner.inventory.isEmpty())
            .map(corner -> corner.building.get().owner).collect(Collectors.toSet());

      if (canSteal.size() == 0) {
         disable();
         return;
      }

      view.removeAllListener();

      final Function<Dimension, Button[]> buttonGetter = (dim) -> StreamUtils.StreamIndexed(GameVariables.players)
            .map(pair -> pair.map((index, player) -> {
               Button playerButton = new Button("STEAL" + player.id, 150, 100, Button.Position.middle,
                     Button.Position.middle,
                     (index - (GameVariables.players.length - 1) / 2.) * 200, 0, dim, player.getName());
               playerButton.disabled = !canSteal.contains(player);
               return playerButton;
            })).toArray(Button[]::new);
      Box<Optional<Button>> overedButton = Box.of(Optional.empty());

      final PaintingJob stealJob = new PaintingJob() {
         @Override
         public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
            g.setFont(ViewVariables.GameFont.deriveFont(36f));
            g.setColor(Color.black);

            DrawUtils.drawCenteredString(g, "Which player do you want to steal ?",
                  new Rectangle(0, 0, dim.width, 100), (bgRect) -> {
                     g.drawImage(MenuJob.ParchemineTexture.await(), bgRect.x, bgRect.y, bgRect.width, bgRect.height,
                           imageObserver);
                  });

            g.setFont(ViewVariables.GameFont.deriveFont(24f));
            Stream.of(buttonGetter.apply(dim)).forEach(button -> {
               final Composite defaultComposite = g.getComposite();

               final Image background = button.disabled ? MenuJob.woodTextureDark.await()
                     : button.equals(overedButton.value.orElse(null)) ? MenuJob.woodTextureLight.await()
                           : MenuJob.woodTexture.await();

               final Composite composite = button.disabled ? AlphaComposite.SrcOver.derive(0.3f)
                     : defaultComposite;

               g.drawImage(background, (int) button.shape.getX(),
                     (int) button.shape.getY(),
                     (int) button.shape.getWidth(),
                     (int) button.shape.getHeight(), imageObserver);
               g.setComposite(composite);

               try {
                  int id = Integer.parseInt(button.id.split("STEAL")[1]);
                  g.setColor(GameVariables.players[id].color.getColor().brighter().brighter());
               } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                  return;
               }
               DrawUtils.drawCenteredString(g, button.content, button.shape);

               g.setComposite(defaultComposite);
            });
         }

      };

      final InputController stealController = new InputController() {
         public void mouseMoved(MouseEvent event) {
            final Button[] buttons = buttonGetter.apply(GameVariables.view.getContentSize());

            overedButton.value = Stream.of(buttons)
                  .filter(button -> !button.disabled && button.shape.contains(event.getPoint()))
                  .findFirst();

            if (overedButton.value.isPresent())
               view.content.setCursor(new Cursor(Cursor.HAND_CURSOR));
            else
               view.content.setCursor(Cursor.getDefaultCursor());

            view.backgroundPainting.forceUpdatePainting().await();
            view.background.repaint();
         };

         public void mouseClicked(MouseEvent event) {
            final Button[] buttons = buttonGetter.apply(GameVariables.view.getContentSize());

            Optional<Button> clickedButton = Stream.of(buttons)
                  .filter(button -> !button.disabled && button.shape.contains(event.getPoint()))
                  .findFirst();

            try {
               Optional<Integer> idToSteal = clickedButton
                     .map(button -> Integer.parseInt(button.id.split("STEAL")[1]));
               idToSteal.ifPresent(id -> {
                  Online.steal(id);
                  disable();
               });
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
               return;
            }
         }
      };

      view.backgroundPainting.addJobs(stealJob);
      view.content.addMouseListener(stealController);
      view.content.addMouseMotionListener(stealController);
   }
}