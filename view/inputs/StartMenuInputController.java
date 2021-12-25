package view.inputs;

import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import globalVariables.GameVariables;
import globalVariables.ViewVariables;
import util_my.Button;
import util_my.DrawUtils;
import view.View;
import view.painting.Painting.PaintingJob;
import view.painting.jobs.gameInterface.MenuJob;

public class StartMenuInputController extends InputController {
   private final Function<Dimension, Button[]> buttonsGetter;
   Button[] buttons;

   final View view;

   public StartMenuInputController(View view, Function<Dimension, Button[]> buttonsGetter) {
      this.view = view;
      this.buttonsGetter = buttonsGetter;
      this.buttons = buttonsGetter.apply(view.getContentSize());
   }

   private void displayOveredButton(Button button) {
      this.view.content.setCursor(new Cursor(Cursor.HAND_CURSOR));
      view.foregroundPainting.updatePainting(button.shape.getSize(), new PaintingJob() {
         @Override
         public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
            g.drawImage(MenuJob.woodTextureLight.await(), 0, 0,
                  (int) dim.getWidth(),
                  (int) dim.getHeight(), imageObserver);
            g.setFont(ViewVariables.GameFont.deriveFont(36f));
            g.setColor(new Color(250, 233, 232));
            DrawUtils.drawCenteredString(g, button.content, new Rectangle(new Point(0, 0), button.shape.getSize()));
         }
      }).await();
      view.foreground.setBounds(button.shape);

   }

   @Override
   public void componentResized(ComponentEvent e) {
      this.buttons = buttonsGetter.apply(view.getContentSize());
   };

   Optional<Button> oldOveredButton = Optional.empty();

   @Override
   public void mouseMoved(MouseEvent event) {
      Optional<Button> overedButton = Stream.of(this.buttons).filter(button -> button.shape.contains(event.getPoint()))
            .findFirst();
      if (overedButton.equals(oldOveredButton))
         return;
      oldOveredButton = overedButton;

      overedButton.ifPresentOrElse(this::displayOveredButton, () -> {
         this.view.foreground.setBounds(0, 0, 0, 0);
         this.view.content.setCursor(Cursor.getDefaultCursor());
      });
   }

   @Override
   public void mouseClicked(MouseEvent event) {
      Optional<Button> button = Stream.of(this.buttons).filter(button_ -> button_.shape.contains(event.getPoint()))
            .findFirst();
      button.map(b -> b.id).ifPresent(content -> {
         switch (content) {
            case "PLAY":

               GameVariables.scenes.gameScene.enable();
               break;

            default:
               break;
         }
      });
   }
}
