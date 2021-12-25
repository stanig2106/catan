package view.painting.jobs.gameInterface;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.*;
import java.util.List;
import java.util.Optional;

import globalVariables.ViewVariables;
import util_my.Button;
import util_my.DrawUtils;
import util_my.Pair;
import util_my.Promise;
import view.painting.Painting.PaintingJob;
import view.scenes.GameScene.GameScene;

public class GameInterfaceJob extends PaintingJob {
   public static final Promise<Image> buildImage = ViewVariables.importImage("assets/menu/icons/Build.png");
   public static final Promise<Image> dicesImage = ViewVariables.importImage("assets/menu/icons/Dices.png");
   public static final Promise<Image> cancelImage = ViewVariables.importImage("assets/menu/icons/Cancel.png");
   public Optional<Button> overedButton = Optional.empty();
   private Optional<Button> saveOveredButton = Optional.empty();
   final GameScene gameScene;

   public GameInterfaceJob(GameScene gameScene) {
      this.gameScene = gameScene;
   }

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      this.saveOveredButton = this.overedButton;

      final Composite defaultComposite = g.getComposite();
      gameScene.getButtons(dim).forEach(pair -> pair.map((button, image) -> {
         final Image background = button.disabled ? MenuJob.woodTextureDark.await()
               : button.equals(overedButton.orElse(null)) ? MenuJob.woodTextureLight.await()
                     : MenuJob.woodTexture.await();
         final Composite composite = button.disabled ? AlphaComposite.SrcOver.derive(0.3f)
               : defaultComposite;

         g.setComposite(defaultComposite);
         g.drawImage(background, (int) button.shape.getX(),
               (int) button.shape.getY(),
               (int) button.shape.getWidth(),
               (int) button.shape.getHeight(), imageObserver);
         g.setComposite(composite);
         DrawUtils.drawCenteredImage(g, image.await(), 50, 50, button.shape, imageObserver);
      }));
   }

   @Override
   public boolean needReload() {
      return !this.overedButton.equals(this.saveOveredButton);

   }

}
