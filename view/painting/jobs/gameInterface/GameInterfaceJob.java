package view.painting.jobs.gameInterface;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import globalVariables.GameVariables;
import globalVariables.ViewVariables;
import map.ressources.Ressources;
import player.Player;
import util_my.Button;
import util_my.DrawUtils;
import util_my.Pair;
import util_my.Promise;
import view.painting.Painting.PaintingJob;
import view.scenes.GameScene.GameScene;

public class GameInterfaceJob extends PaintingJob {
   public static final Promise<Image> buildImage = ViewVariables.importImage("assets/menu/icons/Build.png", 2000);
   public static final Promise<Image> dicesImage = ViewVariables.importImage("assets/menu/icons/Dices.png", 2000);
   public static final Promise<Image> cancelImage = ViewVariables.importImage("assets/menu/icons/Cancel.png", 2000);
   public static final Promise<Image> playImage = ViewVariables.importImage("assets/menu/icons/Play.png", 2000);
   public static final Promise<Image> botImage = ViewVariables.importImage("assets/menu/icons/Bot.png", 2000);

   public Optional<Button> overedButton = Optional.empty();
   private Optional<Button> saveOveredButton = Optional.empty();
   final GameScene gameScene;

   boolean allDisabled = false;

   public boolean manualReload = false;

   public GameInterfaceJob(GameScene gameScene) {
      this.gameScene = gameScene;
   }

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      this.saveOveredButton = this.overedButton;
      this.manualReload = false;

      final Composite defaultComposite = g.getComposite();
      gameScene.getButtons(dim).forEach(pair -> pair.map((button, image) -> {
         final Image background = button.disabled ? MenuJob.woodTextureDark.await()
               : button.equals(overedButton.orElse(null)) ? MenuJob.woodTextureLight.await()
                     : MenuJob.woodTexture.await();
         final Composite composite = button.disabled ? AlphaComposite.SrcOver.derive(0.3f)
               : defaultComposite;

         g.drawImage(background, (int) button.shape.getX(),
               (int) button.shape.getY(),
               (int) button.shape.getWidth(),
               (int) button.shape.getHeight(), imageObserver);
         g.setComposite(composite);
         image.ifPresentOrElse(image_ -> {
            DrawUtils.drawCenteredImage(g, image_.await(), 50, 50, button.shape, imageObserver);
         }, () -> {
            g.setFont(ViewVariables.GameFont.deriveFont(24f));
            DrawUtils.drawCenteredString(g, button.content, button.shape);
         });
         g.setComposite(defaultComposite);
      }));

      new GameRessourcesInterfaceJob().paint(g, dim, imageObserver);
      new GamePlayerInterfaceJob().paint(g, dim, imageObserver);
   }

   @Override
   public boolean needReload() {
      return this.manualReload || !this.overedButton.equals(this.saveOveredButton);
   }

   public boolean isAllDisabled() {
      return allDisabled;
   }

   public void setAllDisabled(boolean value) {
      if (this.allDisabled != value)
         this.manualReload = true;
      this.allDisabled = value;
   }

}

class GamePlayerInterfaceJob extends PaintingJob {

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      IntStream.rangeClosed(0, 3).forEach(i -> {
         paintPlayer(GameVariables.playerToPlay, new Point(10, 10 + i * 130), g, dim, imageObserver);
      });
   }

   void paintPlayer(Player player, Point position, Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      Stroke defaultStroke = g.getStroke();

      g.setColor(player.color.getColor().darker());
      IntStream.rangeClosed(0, 4).forEach(i -> {
         g.fillRoundRect(position.x + 5 + i * 40, position.y + 26, 40, 80, 40, 40);
      });

      g.setColor(player.color.getColor().darker().darker());
      g.setStroke(new BasicStroke(2));
      IntStream.rangeClosed(0, 4).forEach(i -> {
         g.drawRoundRect(position.x + 5 + i * 40, position.y + 26, 40, 80, 40, 40);
      });
      g.setStroke(defaultStroke);

      g.setColor(player.color.getColor());
      g.fillRect(position.x - 2, position.y + 2, 215 + 2, 40);

      g.setColor(Color.WHITE);
      g.setFont(ViewVariables.SerialFont.deriveFont(24f));
      if (player instanceof Player.IA)
         DrawUtils.drawCenteredImage(g, GameInterfaceJob.botImage.await(), 24, 27,
               new Rectangle(position.x, position.y + 2, 40, 40), imageObserver);
      DrawUtils.drawVerticalCenteredString(g, player.name,
            new Rectangle(position.x + 4 + (player instanceof Player.IA ? 35 : 0), position.y + 10, 0, 40));
      if (player == GameVariables.playerToPlay)
         DrawUtils.drawCenteredImage(g, GameInterfaceJob.playImage.await(), 35, 24,
               new Rectangle(position.x + 170, position.y + 2, 40, 40), imageObserver);

      g.setColor(new Color(215, 152, 47));
      g.setStroke(new BasicStroke(2));
      g.drawRect(position.x, position.y, 215, 2);
      g.drawRect(position.x, position.y + 42, 215, 2);
      g.setStroke(defaultStroke);

   }

   @Override
   public boolean needReload() {
      return false;
   }

}

class GameRessourcesInterfaceJob extends PaintingJob {

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      final GradientPaint gp = new GradientPaint(0f, (float) (dim.getHeight() - 80), new Color(22, 27, 34),
            0,
            (float) dim.getHeight(), new Color(142, 17, 3));
      g.setPaint(gp);
      g.fillRect(0, (int) (dim.getHeight() - 60), (int) dim.getWidth(), 60);

      g.setColor(new Color(22, 27, 34));
      g.fillRect(0, (int) (dim.getHeight() - 70), (int) dim.getWidth(), 10);
      int i = -2;
      final int offset = 100;
      Stream.of(
            Pair.of(i++ * offset, Ressources.Lumber),
            Pair.of(i++ * offset, Ressources.Brick),
            Pair.of(i++ * offset, Ressources.Wool),
            Pair.of(i++ * offset, Ressources.Wheat),
            Pair.of(i++ * offset, Ressources.Ore)).forEach(pair -> pair.map((xOffset, ressource) -> {
               g.setFont(ViewVariables.GameFont.deriveFont(30f));
               DrawUtils.drawCenteredString(g, "" + GameVariables.playerToPlay.ressources.getCount(ressource),
                     new Rectangle(new Point((int) (dim.getWidth() / 2. + xOffset - 40), (int) dim.getHeight() - 60),
                           new Dimension(40, 60)));
               DrawUtils.drawCenteredImage(g, ressource.getImage().await(), 40, 40,
                     new Rectangle(new Point((int) (dim.getWidth() / 2. + xOffset), (int) dim.getHeight() - 60),
                           new Dimension(40, 60)),
                     imageObserver);
            }));
   }

   @Override
   public boolean needReload() {
      return false;
   }

}