package view;

import javax.swing.*;

import util_my.Timeout;
import view.jobs.CataneMapJob;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class View extends JFrame {
   // this.background.getGraphics();
   // DONT USE THE getGraphics method !!

   final Painting foregroundPainting;
   final Painting backgroundPainting;
   final Canvas foreground;
   final JPanel background;

   public View() {
      super("Catane");
      System.setProperty("sun.awt.noerasebackground", "true");
      super.setSize(1200, 1600);
      super.setPreferredSize(new Dimension(1200, 1600));
      super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      this.foregroundPainting = Painting.newPainting(this.getSize(), new TestJob()).await();
      this.backgroundPainting = Painting.newPainting(this.getSize(), new CataneMapJob()).await();

      View me = this;
      this.foreground = new Canvas() {
         @Override
         public void paint(Graphics g) {
            // System.out.println("try to paint front");
            me.foregroundPainting.paintTo(this).await();
         }
      };
      this.foreground.setSize(super.getWidth() / 2, super.getHeight());
      this.background = new BackgroundPanel(this.backgroundPainting);

      super.getLayeredPane().add(this.foreground, 2);
      super.getLayeredPane().add(this.background, 1);

      super.pack();
      super.setLocationRelativeTo(null);

      super.getLayeredPane().setVisible(true);
      super.setVisible(true);

      super.addComponentListener(this.defaultComponentListener);
      this.foreground.setVisible(false);

      // new Timeout(() -> {
      // System.out.println();
      // System.out.println();
      // System.out.println("test : ");
      // System.out.println("------------------");
      // System.out.println("------------------");
      // System.out.println("move");
      // this.foreground.setBounds(50, 50, 50, 50);
      // this.background.repaint();
      // new Timeout(() -> {
      // System.out.println("move");
      // this.foreground.setBounds(100, 100, 100, 100);
      // new Timeout(() -> {
      // System.out.println("hide");
      // this.foreground.setVisible(false);
      // }, 5000);
      // }, 5000);
      // }, 5000);
   }

   void resizeCallback(ComponentEvent e) {
      this.background.setSize(super.getSize());
      this.backgroundPainting.updatePainting(super.getSize()).await();
      this.backgroundPainting.destroyBackup();

      // this.foreground.setSize(super.getWidth() / 2, super.getHeight());
      // this.foregroundPainting.updatePainting(super.getSize()).awaitOrError();
      // this.foregroundPainting.destroyBackup();
      // this.foregroundPainting.paintTo(this.foreground);
   }

   //
   //
   //
   //
   //

   final ComponentListener defaultComponentListener = new ComponentListener() {
      @Override
      public void componentHidden(ComponentEvent e) {
      }

      @Override
      public void componentMoved(ComponentEvent e) {
      }

      @Override
      public void componentResized(ComponentEvent e) {
         resizeCallback(e);
      }

      @Override
      public void componentShown(ComponentEvent e) {
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
      // System.out.println("try to paint back");
      painting.paintTo(this, (Graphics2D) g).await();
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