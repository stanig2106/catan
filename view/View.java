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
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import util_my.Timeout;
import view.inputs.InputController;
import view.painting.Painting;
import view.painting.jobs.LoadingJob;
import view.painting.jobs.NullJob;

public class View extends JFrame {
   // this.background.getGraphics();
   // DONT USE THE getGraphics method !!

   public final Painting foregroundPainting;
   public final Painting backgroundPainting;
   public final Canvas foreground;
   public final JPanel background;
   private final View me = this;

   public final JLayeredPane content;

   public View() {
      super("Catan");
      System.setProperty("sun.awt.noerasebackground", "true");
      super.setSize(1200, 800);
      super.setPreferredSize(new Dimension(1200, 800));
      super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      this.backgroundPainting = Painting.newPainting(1200, 800, new LoadingJob()).await();

      super.setVisible(true);
      this.content = this.getLayeredPane();
      this.content.setVisible(true);
      this.foregroundPainting = Painting.newPainting(this.getContentSize(), new NullJob()).await();
      this.foreground = new Canvas() {
         @Override
         public void paint(Graphics g) {
            me.backgroundPainting.paintSubImageTo(this, this.getBounds()).await();
            me.foregroundPainting.paintTo(this).await();
         }
      };

      this.foreground.setSize(0, 0);

      this.background = new BackgroundPanel(this.backgroundPainting);
      this.background.setSize(this.getContentSize());

      this.content.add(this.foreground, 2);
      this.content.add(this.background, 1);
      super.pack();
      super.setLocationRelativeTo(null);

      super.addComponentListener(this.defaultComponentListener);

      this.foreground.addMouseListener(this.redirectMouseInputListener);
      this.foreground.addMouseMotionListener(this.redirectMouseInputListener);
      this.foreground.addMouseWheelListener(this.redirectMouseWheelListener);

      this.repaintLoop();

   }

   public void removeAllListener(InputController... listener) {
      Stream.of(super.getComponentListeners()).filter(Predicate.not(this.defaultComponentListener::equals))
            .forEach(super::removeComponentListener);

      removeListener(this.content::getMouseListeners,
            this.content::removeMouseListener);
      removeListener(this.content::getMouseMotionListeners,
            this.content::removeMouseMotionListener);
      removeListener(this.content::getMouseWheelListeners,
            this.content::removeMouseWheelListener);
      removeListener(this.content::getKeyListeners,
            this.content::removeKeyListener);
   }

   private static <T> void removeListener(Supplier<T[]> getter, Consumer<T> remover) {
      Stream.of(getter.get()).forEach(remover);
   }

   public Dimension getContentSize() {
      return this.content.getSize();
   }

   private final void repaintLoop() {
      this.backgroundPainting.forceUpdatePainting().await();
      this.background.repaint();
      new Timeout(this::repaintLoop, 1000);
   }

   public final class LandSizeCalculator implements Supplier<Integer> {
      public boolean needRecalculate = true;
      int cachedValue;

      @Override
      public Integer get() {
         if (this.needRecalculate) {
            readjustZoomLevel();
            this.cachedValue = (int) Math.round(me.content.getHeight() / 10.
                  * me.zoomLevel);
            this.needRecalculate = false;
         }
         return this.cachedValue;
      }

      public void readjustZoomLevel() {
         zoomLevel = Math.max(0.75, zoomLevel);
         zoomLevel = Math.min(3, zoomLevel);
      }
   }

   public final LandSizeCalculator landSizeCalculator = new LandSizeCalculator();

   public int getLandSize() {
      return landSizeCalculator.get();
   }

   public final class MapCenterCalculator implements Supplier<Point> {
      public boolean needRecalculate = true;
      Point cachedValue;

      @Override
      public Point get() {
         if (this.needRecalculate) {
            readjustMapOffset();
            this.cachedValue = new Point((int) (me.content.getWidth() / 2. + me.mapOffset.getX()),
                  (int) (me.content.getHeight() / 2. + me.mapOffset.getY()));
            this.needRecalculate = false;
         }
         return this.cachedValue;
      }

      public void readjustMapOffset() {
         if (Math.abs(mapOffset.getX()) > me.content.getWidth() / 2. * zoomLevel * 0.70)
            mapOffset = new Point(
                  (int) Math.min(me.content.getWidth() / 2. * zoomLevel * 0.70,
                        Math.max(me.content.getWidth() / -2. * zoomLevel * 0.70, mapOffset.getX())),
                  (int) mapOffset.getY());
         if (Math.abs(mapOffset.getY()) > me.content.getHeight() / 2. * zoomLevel * 0.70)
            mapOffset = new Point(
                  (int) mapOffset.getX(),
                  (int) Math.min(me.content.getHeight() / 2. * zoomLevel * 0.70,
                        Math.max(me.content.getHeight() / -2. * zoomLevel * 0.70, mapOffset.getY())));
      }
   }

   public final MapCenterCalculator mapCenterCalculator = new MapCenterCalculator();

   public Point getMapCenter() {
      return mapCenterCalculator.get();
   }

   //
   // Callback
   //

   public void resizeCallback() {
      landSizeCalculator.needRecalculate = true;
      mapCenterCalculator.needRecalculate = true;
      this.background.setSize(this.getContentSize());
      this.backgroundPainting
            .updatePainting(this.getContentSize()).await();
   }

   public double zoomLevel = 1;

   public Point mapOffset = new Point(0, 0);

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

   final MouseWheelListener redirectMouseWheelListener = new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent event) {
         event.setSource(me.content);
         event.translatePoint(me.foreground.getX(), me.foreground.getY());
         me.content.dispatchEvent(event);
      }

   };

   final MouseInputListener redirectMouseInputListener = new MouseInputListener() {

      @Override
      public void mouseClicked(MouseEvent event) {
         event.setSource(me.content);
         event.translatePoint(me.foreground.getX(), me.foreground.getY());
         me.content.dispatchEvent(event);
      }

      @Override
      public void mouseEntered(MouseEvent event) {
      }

      @Override
      public void mouseExited(MouseEvent event) {
      }

      @Override
      public void mousePressed(MouseEvent event) {
         event.setSource(me.content);
         event.translatePoint(me.foreground.getX(), me.foreground.getY());
         me.content.dispatchEvent(event);
      }

      @Override
      public void mouseReleased(MouseEvent event) {
         event.setSource(me.content);
         event.translatePoint(me.foreground.getX(), me.foreground.getY());
         me.content.dispatchEvent(event);
      }

      @Override
      public void mouseDragged(MouseEvent event) {
         event.setSource(me.content);
         event.translatePoint(me.foreground.getX(), me.foreground.getY());
         me.content.dispatchEvent(event);
      }

      @Override
      public void mouseMoved(MouseEvent event) {
         event.setSource(me.content);
         event.translatePoint(me.foreground.getX(), me.foreground.getY());
         me.content.dispatchEvent(event);
      }
   };
}

class BackgroundPanel extends JPanel {
   private final Painting painting;

   BackgroundPanel(Painting painting) {
      super();
      this.painting = painting;
   }

   @Override
   public void paint(Graphics g) {
      painting.paintTo(this, (Graphics2D) g).await();
   }

   @Override
   public Graphics getGraphics() {
      StackTraceElement[] traces = Thread.currentThread().getStackTrace();
      boolean check = false;
      for (StackTraceElement trace : traces) {
         if (check)
            if (trace.getMethodName().equals("safelyGetGraphics") || trace.getClassName().startsWith("javax.swing")
                  || trace.getClassName().startsWith("java.awt"))
               return super.getGraphics();
            else
               break;
         check = trace.getMethodName().equals("getGraphics");
      }
      throw new UnsupportedOperationException("Don't call getGraphics on background...");
   }
};