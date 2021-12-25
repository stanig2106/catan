package view.painting.jobs.gameInterface;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.ImageObserver;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import globalVariables.GameVariables;
import globalVariables.ViewVariables;
import map.ressources.Ressources;
import player.Player;
import player.developmentCards.Card;
import util_my.Box;
import util_my.Button;
import util_my.DrawUtils;
import util_my.Pair;
import util_my.Promise;
import util_my.StreamUtils;
import view.painting.Painting.PaintingJob;
import view.scenes.GameScene.GameScene;

public class GameInterfaceJob extends PaintingJob {
   public static final Promise<Image> buildImage = ViewVariables.importImage("assets/menu/icons/Build.png", 2000);
   public static final Promise<Image> dicesImage = ViewVariables.importImage("assets/menu/icons/Dices.png", 2000);
   public static final Promise<Image> cancelImage = ViewVariables.importImage("assets/menu/icons/Cancel.png", 2000);

   public Optional<Button> overedButton = Optional.empty();
   private Optional<Button> saveOveredButton = Optional.empty();
   final GameScene gameScene;

   protected int indexOfOveredCard = -1;// -1 for no overedCard
   protected boolean cardDragged = false;

   boolean allDisabled = false;

   public boolean manualReload = false;

   public Optional<Pair<Integer, Integer>> lastDice = Optional.empty();

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

      new CardInterfaceJob(indexOfOveredCard, cardDragged).paint(g, dim, imageObserver);
      new RessourcesInterfaceJob().paint(g, dim, imageObserver);

      new PlayersInterfaceJob().paint(g, dim, imageObserver);

      this.lastDice.ifPresent(dices -> dices.map((dice1, dice2) -> {
         g.drawImage(DicesJob.facesImage.get(dice1).await(), dim.width - 46, 10, 36, 36, imageObserver);
         g.drawImage(DicesJob.facesImage.get(dice2).await(), dim.width - 46 * 2, 10, 36, 36, imageObserver);
      }));
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

   public void setIndexOfOveredCard(int index) {
      if (this.indexOfOveredCard != index)
         this.manualReload = true;
      this.indexOfOveredCard = index;
   }

   public int getIndexOfOveredCard() {
      return indexOfOveredCard;
   }

   public void setCardDragged(boolean cardDragged) {
      this.manualReload = true;
      this.cardDragged = cardDragged;
   }

   public boolean isCardDragged() {
      return this.cardDragged;
   }

}

class PlayersInterfaceJob extends PaintingJob {
   public static final Promise<Image> playImage = ViewVariables.importImage("assets/menu/icons/Play.png", 2000);
   public static final Promise<Image> botImage = ViewVariables.importImage("assets/menu/icons/Bot.png", 2000);
   public static final Promise<Image> onlineImage = ViewVariables.importImage("assets/menu/icons/Bot.png", 2000);
   public static final Promise<Image> swordImage = ViewVariables.importImage("assets/menu/icons/Sword.png", 2000);
   public static final Promise<Image> pathImage = ViewVariables.importImage("assets/menu/icons/Path.png", 2000);
   public static final Promise<Image> ressourceCardImage = ViewVariables
         .importImage("assets/menu/icons/RessourceCard.png", 2000);
   public static final Promise<Image> ressourceCardRedImage = ViewVariables
         .importImage("assets/menu/icons/RessourceCardRed.png", 2000);
   public static final Promise<Image> developmentCardImage = ViewVariables
         .importImage("assets/menu/icons/DevelopmentCard.png", 2000);
   public static final Promise<Image> trophyImage = ViewVariables.importImage("assets/menu/icons/Trophy.png", 2000);

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      Box<Integer> i = Box.of(0);
      Stream.of(GameVariables.players).forEach(player -> {
         paintPlayer(player, new Point(10, 10 + i.value++ * 130), g, dim, imageObserver);
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
         DrawUtils.drawCenteredImage(g, PlayersInterfaceJob.botImage.await(), 24, 27,
               new Rectangle(position.x, position.y + 2, 40, 40), imageObserver);
      if (player instanceof Player.Online)
         DrawUtils.drawCenteredImage(g, PlayersInterfaceJob.onlineImage.await(), 24, 27,
               new Rectangle(position.x, position.y + 2, 40, 40), imageObserver);

      DrawUtils.drawVerticalCenteredString(g, player.getName(),
            new Rectangle(position.x + 4 + (player instanceof Player.IA || player instanceof Player.Online ? 35 : 0),
                  position.y + 10, 0, 40));
      if (player.id == GameVariables.playerToPlay.id)
         DrawUtils.drawCenteredImage(g, PlayersInterfaceJob.playImage.await(), 35, 24,
               new Rectangle(position.x + 170, position.y + 2, 40, 40), imageObserver);

      g.setColor(new Color(215, 152, 47));
      g.setStroke(new BasicStroke(2));
      g.drawRect(position.x, position.y, 215, 2);
      g.drawRect(position.x, position.y + 42, 215, 2);
      g.setStroke(defaultStroke);

      class Property {
         final int xOffset;
         final Image image;
         final int width;
         final String value;

         public Property(int xOffset, Image image, int width, String value) {
            this.xOffset = xOffset;
            this.image = image;
            this.width = width;
            this.value = value;
         }

         public Property(int xOffset, Image image, int width, int value) {
            this(xOffset, image, width, "" + value);
         }
      }

      g.setFont(ViewVariables.GameFont.deriveFont(24f));
      g.setColor(Color.white);
      int i = 0;
      Stream.of(
            new Property(40 * i++, PlayersInterfaceJob.swordImage.await(), 12, 0),
            new Property(40 * i++, PlayersInterfaceJob.pathImage.await(), 17, "-"),
            new Property(40 * i++, PlayersInterfaceJob.ressourceCardImage.await(), 17, player.inventory.getTotal()),
            new Property(40 * i++, PlayersInterfaceJob.developmentCardImage.await(), 17,
                  player.inventory.getCardsCount()),
            new Property(40 * i++, PlayersInterfaceJob.trophyImage.await(), 25, player.getPublicScore())).forEach(p -> {
               DrawUtils.drawCenteredString(g, p.value,
                     new Rectangle(position.x + 5 + p.xOffset, position.y + 40, 40, 40));
               DrawUtils.drawCenteredImage(g, p.image, p.width, 24,
                     new Rectangle(position.x + 5 + p.xOffset, position.y + 65, 40, 40), imageObserver);
            });
   }

   @Override
   public boolean needReload() {
      return false;
   }

}

class CardInterfaceJob extends PaintingJob {
   final int indexOfOveredCard; // -1 for no overedCard
   final boolean cardDragged;

   CardInterfaceJob(int indexOfOveredCard, boolean cardDragged) {
      this.indexOfOveredCard = indexOfOveredCard;
      this.cardDragged = cardDragged;
   }

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      final var cards = new LinkedList<>(GameVariables.getMe().inventory.cards);
      Box<Double> xOffset = Box.of((cards.size() % 2) / 2.);
      xOffset.value -= (cards.size() - (cards.size() % 2 == 0 ? 1 : 0)) / 2.;
      Box<Double> xOffsetOfOveredCard = Box.of(-1.0);
      StreamUtils.StreamIndexed(cards).forEach(pair -> pair.map((i, card) -> {
         if (i == this.indexOfOveredCard) {
            xOffsetOfOveredCard.value = xOffset.value;
            paintCard(Optional.empty(), xOffset.value++, g, dim, imageObserver, false);
         } else
            paintCard(Optional.of(card.getKey()), xOffset.value++, g, dim, imageObserver, card.getValue());
      }));

      if (indexOfOveredCard != -1 && !cardDragged)
         paintFullCard(cards.get(indexOfOveredCard).getKey(), xOffsetOfOveredCard.value, g, dim, imageObserver,
               cards.get(indexOfOveredCard).getValue());
   }

   private void paintCard(Optional<Card> card, double xOffset, Graphics2D g, Dimension dim,
         ImageObserver imageObserver, boolean playable) {
      final Composite defaultComposite = g.getComposite();
      if (card.isEmpty()) {
         g.setColor(Color.gray);
         g.setComposite(AlphaComposite.SrcOver.derive(0.8f));
         g.fillRect((int) (dim.width / 2. + xOffset * 155) - 75, dim.height - 70 - 40, 150, 40);
         g.setComposite(defaultComposite);
      } else
         g.drawImage(MenuJob.ParchemineTexture.await(), (int) (dim.width / 2. + xOffset * 155) - 75,
               dim.height - 70 - 40, 150, 40, imageObserver);

      if (playable) {
         final Stroke defaultStroke = g.getStroke();
         g.setStroke(new BasicStroke(4));
         g.setComposite(AlphaComposite.SrcOver.derive(0.5f));
         g.setColor(Color.green);
         g.drawRect((int) (dim.width / 2. + xOffset * 155) - 75, dim.height - 70 - 40, 150, 50);
         g.setStroke(defaultStroke);
         g.setComposite(defaultComposite);
      }

      if (card.isEmpty())
         return;

      g.setFont(ViewVariables.GameFont.deriveFont(20f));
      g.setColor(Color.black);
      DrawUtils.drawCenteredString(g, card.get().getTitle(),
            new Rectangle((int) (dim.width / 2. + xOffset * 155 - 75), dim.height - 70 - 40 + 2, 150, 40));

   }

   private void paintFullCard(Card card, double xOffset, Graphics2D g, Dimension dim,
         ImageObserver imageObserver, boolean playable) {
      g.drawImage(MenuJob.ParchemineTexture.await(), (int) (dim.width / 2. + xOffset * 155) - 120,
            dim.height - 70 - 190, 240, 190, imageObserver);
      if (playable) {
         final Composite defaultComposite = g.getComposite();
         final Stroke defaultStroke = g.getStroke();
         g.setStroke(new BasicStroke(4));
         g.setComposite(AlphaComposite.SrcOver.derive(0.5f));
         g.setColor(Color.green);
         g.drawRect((int) (int) (dim.width / 2. + xOffset * 155) - 120,
               dim.height - 70 - 190, 240, 190);
         g.setStroke(defaultStroke);
         g.setComposite(defaultComposite);
      }
      g.setFont(ViewVariables.GameFont.deriveFont(26f));
      g.setColor(Color.black);
      DrawUtils.drawCenteredString(g, card.getTitle(),
            new Rectangle((int) (dim.width / 2. + xOffset * 155 - 120), dim.height - 70 - 190 + 2, 240, 40));
      g.setFont(ViewVariables.SerialFont.deriveFont(20f));
      StreamUtils.StreamIndexed(card.getDescription()).forEach(pair -> pair.map((i, line) -> {
         DrawUtils.drawCenteredString(g, line,
               new Rectangle((int) (dim.width / 2. + xOffset * 155 - 120), dim.height - 70 - 190 + 2 + i * 25 + 50, 240,
                     20));
      }));
   }

}

class RessourcesInterfaceJob extends PaintingJob {

   @Override
   public void paint(Graphics2D g, Dimension dim, ImageObserver imageObserver) {
      final GradientPaint gp = new GradientPaint(0f, (float) (dim.getHeight() - 80), new Color(22, 27, 34),
            0,
            (float) dim.getHeight(),
            /* new Color(142, 17, 3) */ GameVariables.getMe().color.getColor().darker().darker());
      g.setPaint(gp);
      g.fillRect(0, (int) (dim.getHeight() - 65), (int) dim.getWidth(), 65);

      g.setColor(new Color(22, 27, 34));
      g.fillRect(0, (int) (dim.getHeight() - 70), (int) dim.getWidth(), 5);
      int i = -2;
      final int offset = 100;
      Stream.of(
            Pair.of(i++ * offset, Ressources.Lumber),
            Pair.of(i++ * offset, Ressources.Brick),
            Pair.of(i++ * offset, Ressources.Wool),
            Pair.of(i++ * offset, Ressources.Wheat),
            Pair.of(i++ * offset, Ressources.Ore)).forEach(pair -> pair.map((xOffset, ressource) -> {
               g.setFont(ViewVariables.GameFont.deriveFont(30f));
               DrawUtils.drawCenteredString(g, "" + GameVariables.getMe().inventory.getCount(ressource),
                     new Rectangle(new Point((int) (dim.getWidth() / 2. + xOffset - 40), (int) dim.getHeight() - 60),
                           new Dimension(40, 60)));
               DrawUtils.drawCenteredImage(g, ressource.getImage().await(), 40, 40,
                     new Rectangle(new Point((int) (dim.getWidth() / 2. + xOffset), (int) dim.getHeight() - 60),
                           new Dimension(40, 60)),
                     imageObserver);
            }));
   }

}