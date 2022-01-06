package view.scenes.GameScene;

import globalVariables.GameVariables;
import view.Scene;
import view.View;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.awt.*;
import java.util.Optional;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import globalVariables.GameVariables;
import globalVariables.ViewVariables;
import map.ressources.Ressources;
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

public class ChoseRessourceScene extends Scene {
   Optional<ListenerSave> listenerSave;
   Optional<PaintingJob> paintingSave;

   public ChoseRessourceScene() {
      super(GameVariables.view);
   }

   public void enable(String text, BiConsumer<Ressources, Runnable> callback) {
      GameVariables.scenes.gameScene.gameInterfaceJob.setAllDisabled(true);
      this.listenerSave = Optional.of(view.new ListenerSave());
      this.paintingSave = Optional.of(view.backgroundPainting.getJobs());
      final ChoseRessourceScene me = this;
      GameVariables.scenes.gameScene.buildScene.disable();

      view.removeAllListener();

      final Function<Dimension, Button[]> buttonGetter = (dim) -> StreamUtils.StreamIndexed(Ressources.values())
            .map(pair -> pair.map(
                  (Integer index, Ressources ressource) -> new Button("RESSOURCE" + ressource.ordinal(), 150, 100,
                        Button.Position.middle,
                        Button.Position.middle,
                        (index - (Ressources.values().length - 1) / 2.) * 200, 0, dim, ressource.toString())))
            .toArray(Button[]::new);
      Box<Optional<Button>> overedButton = Box.of(Optional.empty());

      final PaintingJob chooseJob = new PaintingJob() {
         @Override
         public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
            g.setFont(ViewVariables.GameFont.deriveFont(36f));
            g.setColor(Color.black);

            DrawUtils.drawCenteredString(g, text,
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
                  final Ressources ressource = Ressources.values()[Integer.parseInt(button.id.split("RESSOURCE")[1])];
                  DrawUtils.drawCenteredImage(g, ressource.getImage().await(), 48, 48, button.shape,
                        imageObserver);
               } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                  DrawUtils.drawCenteredString(g, button.content, button.shape);
               }

               g.setComposite(defaultComposite);
            });
         }

      };

      final InputController chooseController = new InputController() {
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
               final Optional<Ressources> ressource = clickedButton
                     .map(button -> Ressources.values()[Integer.parseInt(button.id.split("RESSOURCE")[1])]);
               ressource.ifPresent(ressource_ -> {
                  callback.accept(ressource_, me::disable);

               });
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
               return;
            }
         }
      };

      view.backgroundPainting.addJobs(chooseJob);
      view.content.addMouseListener(chooseController);
      view.content.addMouseMotionListener(chooseController);
   }

   public void disable() {
      GameVariables.scenes.gameScene.gameInterfaceJob.setAllDisabled(false);

      this.listenerSave.ifPresent(save -> save.restore());
      this.listenerSave = Optional.empty();

      this.paintingSave.ifPresent(save -> view.backgroundPainting.updatePainting(save).await());
      this.paintingSave = Optional.empty();
      view.background.repaint();
   }

}
