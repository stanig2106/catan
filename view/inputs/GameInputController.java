package view.inputs;

import java.awt.event.MouseEvent;
import java.awt.Image;
import java.awt.Cursor;
import java.util.List;
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

import globalVariables.ViewVariables;
import util_my.Button;
import util_my.DrawUtils;
import util_my.Pair;
import util_my.Promise;
import view.View;
import view.painting.Painting.PaintingJob;
import view.painting.jobs.gameInterface.GameInterfaceJob;
import view.painting.jobs.gameInterface.MenuJob;
import view.scenes.GameScene;

public class GameInputController extends InputController {
   final GameScene gameScene;
   final View view;

   public GameInputController(View view, GameScene gameScene) {
      this.view = view;
      this.gameScene = gameScene;
   }

   @Override
   public void mouseMoved(MouseEvent event) {
      final List<Pair<Button, Promise<Image>>> buttons = gameScene.getButtons(this.view.getContentSize());

      buttons.stream().filter(pair -> !pair.getKey().disabled && pair.getKey().shape.contains(event.getPoint()))
            .findFirst()
            .ifPresentOrElse(pair -> pair.map((button, image) -> {
               this.view.content.setCursor(new Cursor(Cursor.HAND_CURSOR));
               view.foregroundPainting.updatePainting(button.shape.getSize(), new PaintingJob() {
                  @Override
                  public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
                     g.drawImage(MenuJob.woodTextureLight.await(), 0, 0,
                           (int) dim.getWidth(),
                           (int) dim.getHeight(), imageObserver);
                     DrawUtils.drawCenteredImage(g, image.await(), 50, 50,
                           new Rectangle(new Point(0, 0), button.shape.getSize()), imageObserver);
                  }
               }).await();
               view.foreground.setBounds(button.shape);
            }), () -> {
               this.view.foreground.setBounds(0, 0, 0, 0);
               this.view.content.setCursor(Cursor.getDefaultCursor());
            });

   }
}
