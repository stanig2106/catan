package view;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Optional;
import java.util.function.Supplier;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import globalVariables.GameVariables;
import map.Land;
import util_my.Box;
import util_my.Coord;
import util_my.Promise;
import util_my.Timeout;
import view.input.MouseControl;
import view.painting.Painting;
import view.painting.jobs.CataneMapJob;
import view.painting.jobs.LoadingJob;
import view.painting.jobs.NullJob;
import view.painting.jobs.TestJob;

public class View extends JFrame {
   // this.background.getGraphics();
   // DONT USE THE getGraphics method !!

   public final Box<Painting> foregroundPainting = new Box<Painting>();
   public final Box<Painting> backgroundPainting = new Box<Painting>();
   final Canvas foreground;
   public final JPanel background;
   private final View me = this;

   private final JLayeredPane content;

   public View() {
      super("Catane");
      System.setProperty("sun.awt.noerasebackground", "true");
      super.setSize(1200, 800);
      super.setPreferredSize(new Dimension(1200, 800));
      super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      this.content = this.getLayeredPane();
      this.content.setVisible(true);
      super.setVisible(true);

      Promise<Painting> cataneMapPainting = Painting.newPainting(this.content.getSize(),
            new CataneMapJob(this.getLandSize(), this.getMapCenter()));
      this.backgroundPainting.data = Painting.newPainting(this.content.getSize(), new LoadingJob()).await();
      this.foregroundPainting.data = Painting.newPainting(this.content.getSize(), new NullJob()).await();
      this.foreground = new Canvas() {
         @Override
         public void paint(Graphics g) {
            me.foregroundPainting.data.paintTo(this).await();
         }
      };
      this.foreground.setSize(0, 0);

      this.background = new BackgroundPanel(this.backgroundPainting);
      this.background.setSize(this.content.getSize());

      this.content.add(this.foreground, 2);
      this.content.add(this.background, 1);

      super.pack();
      super.setLocationRelativeTo(null);

      super.addComponentListener(this.defaultComponentListener);
      this.content.addMouseListener(this.defaultMouseInputListener);
      this.content.addMouseMotionListener(this.defaultMouseInputListener);
      this.content.addMouseWheelListener(this.defaultMouseWheelListener);

      this.background.repaint();
      this.backgroundPainting.data = cataneMapPainting.await();
      this.foregroundPainting.data = Painting.newPainting(this.content.getSize(), new TestJob()).await();
      this.background.repaint();
   }

   final private class LandSizeCalculator implements Supplier<Integer> {
      public boolean needRecalculate = true;
      int cachedValue;

      @Override
      public Integer get() {
         if (this.needRecalculate) {
            this.cachedValue = (int) Math.round(me.content.getHeight() / 10.
                  * me.zoomLevel);
            this.needRecalculate = false;
         }
         return this.cachedValue;
      }
   }

   final LandSizeCalculator landSizeCalculator = new LandSizeCalculator();

   public int getLandSize() {
      return landSizeCalculator.get();
   }

   final private class MapCenterCalculator implements Supplier<Point> {
      public boolean needRecalculate = true;
      Point cachedValue;

      @Override
      public Point get() {
         if (this.needRecalculate) {
            this.cachedValue = new Point((int) (me.content.getWidth() / 2. + me.mapOffset.getX()),
                  (int) (me.content.getHeight() / 2. + me.mapOffset.getY()));
            this.needRecalculate = false;
         }
         return this.cachedValue;
      }
   }

   final MapCenterCalculator mapCenterCalculator = new MapCenterCalculator();

   public Point getMapCenter() {
      return mapCenterCalculator.get();
   }

   //
   // Callback
   //

   public void resizeCallback() {
      landSizeCalculator.needRecalculate = true;
      mapCenterCalculator.needRecalculate = true;
      this.background.setSize(this.content.getSize());
      this.backgroundPainting.data
            .updatePainting(this.content.getSize(), new CataneMapJob(this.getLandSize(), this.getMapCenter())).await();
      this.backgroundPainting.data.destroyBackup();

      // this.foreground.setSize(super.getWidth() / 2, super.getHeight());
      // this.foregroundPainting.updatePainting(super.getSize()).awaitOrError();
      // this.foregroundPainting.destroyBackup();
      // this.foregroundPainting.paintTo(this.foreground);
   }

   private double zoomLevel = 1;

   public void zoomCallback(boolean zoomUp, Point origine) {
      if ((zoomLevel == 0.75 && !zoomUp) || (zoomLevel == 3 && zoomUp))
         return;

      double old_xDistanceToCenter = (origine.getX() - getMapCenter().getX());
      double old_yDistanceToCenter = (origine.getY() - getMapCenter().getY());
      double oldZoomLevel = zoomLevel;

      zoomLevel += zoomUp ? 0.25 : -0.25;
      zoomLevel = Math.max(0.75, zoomLevel);
      zoomLevel = Math.min(3, zoomLevel);

      double xDistanceToCenter = old_xDistanceToCenter * zoomLevel / oldZoomLevel;
      double yDistanceToCenter = old_yDistanceToCenter * zoomLevel / oldZoomLevel;

      this.mapOffset.translate((int) Math.round(old_xDistanceToCenter - xDistanceToCenter),
            (int) Math.round(old_yDistanceToCenter - yDistanceToCenter));

      mapCenterCalculator.needRecalculate = true;
      landSizeCalculator.needRecalculate = true;

      this.backgroundPainting.data.updatePainting(new CataneMapJob(this.getLandSize(), this.getMapCenter())).await();
      this.background.repaint();
   }

   private Point mapOffset = new Point(0, 0);

   public void moveWithShiftCallback(int xOffset, int yOffset) {
      mapOffset.translate(xOffset, yOffset);

      this.backgroundPainting.data.updatePainting(new CataneMapJob(this.getLandSize(), this.getMapCenter())).await();
      this.background.repaint();
      mapCenterCalculator.needRecalculate = true;
   }

   //
   //
   //
   //
   //

   final ComponentListener defaultComponentListener = new ComponentListener() {
      @Override
      public void componentHidden(ComponentEvent event) {
      }

      @Override
      public void componentMoved(ComponentEvent event) {
      }

      @Override
      public void componentResized(ComponentEvent _event) {
         resizeCallback();
      }

      @Override
      public void componentShown(ComponentEvent event) {
      }
   };

   final MouseWheelListener defaultMouseWheelListener = new MouseWheelListener() {
      private boolean disponible = true;

      @Override
      public void mouseWheelMoved(MouseWheelEvent event) {
         int notches = event.getWheelRotation();

         if (!disponible)
            return;

         me.zoomCallback(notches < 0, event.getPoint());

         this.disponible = false;

         new Timeout(() -> {
            this.disponible = true;
         }, 50);

      }

   };

   final MouseInputListener defaultMouseInputListener = new MouseInputListener() {

      @Override
      public void mouseClicked(MouseEvent event) {

      }

      @Override
      public void mouseEntered(MouseEvent event) {
      }

      @Override
      public void mouseExited(MouseEvent event) {
      }

      @Override
      public void mousePressed(MouseEvent event) {
      }

      @Override
      public void mouseReleased(MouseEvent event) {
         this.oldPosition = null;
      }

      Point oldPosition = null;
      private boolean disponible = true;

      @Override
      public void mouseDragged(MouseEvent event) {
         if (!disponible)
            return;
         if (oldPosition == null) {
            this.oldPosition = event.getPoint();
            return;
         }

         if (event.isShiftDown())
            moveWithShiftCallback(event.getX() - (int) this.oldPosition.getX(),
                  event.getY() - (int) this.oldPosition.getY());
         this.oldPosition = event.getPoint();
         new Timeout(() -> {
            this.disponible = true;
         }, 2);
      }

      @Override
      public void mouseMoved(MouseEvent event) {
         Optional<Coord> coord = MouseControl.positionToLandCoord(event.getPoint(), me.getLandSize(),
               me.getMapCenter());
         if (coord.isEmpty())
            return;
         Land land = GameVariables.map.get(coord.get());
         System.out.println(land);
         Point center = CataneMapJob.landsPosition.get(coord.get());
         double distance = center.distance(event.getPoint());
         System.out.println(distance);
         System.out.println(0.70 * me.getLandSize());
         if (distance > 0.70 * me.getLandSize())
            System.out.println("bord");
         // System.out.println("moved");
         // System.out.println(event.getPoint());
      }
   };
}

class BackgroundPanel extends JPanel {
   private final Box<Painting> painting;

   BackgroundPanel(Box<Painting> painting) {
      super();
      this.painting = painting;
   }

   @Override
   public void paint(Graphics g) {
      // System.out.println("try to paint back");
      painting.data.paintTo(this, (Graphics2D) g).await();
   }

   @Override
   public Graphics getGraphics() {
      StackTraceElement[] traces = Thread.currentThread().getStackTrace();
      boolean check = false;
      for (StackTraceElement trace : traces) {
         if (check) {
            if (trace.getMethodName().equals("safelyGetGraphics") || trace.getClassName().startsWith("javax.swing")
                  || trace.getClassName().startsWith("java.awt"))
               return super.getGraphics();
            else
               throw new Error("I don't know why, but don't call getGraphics on background...");
         }
         if (trace.getMethodName().equals("getGraphics")) {
            check = true;
         }
      }
      throw new Error("I don't know why, but don't call getGraphics on background...");

      // // System.out.println("I don't know why, but don't call getGraphics on
      // // background...");
   }
};