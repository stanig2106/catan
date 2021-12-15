package view;

import javax.swing.*;

import util_my.Box;
import util_my.Promise;
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
      super.setSize(800, 600);
      super.setPreferredSize(new Dimension(800, 600));
      super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      View me = this;
      this.foreground = new Canvas() {
         @Override
         public void paint(Graphics g) {
            System.out.println("try to paint front");
            me.foregroundPainting.paintTo(me.foreground).await();
         }

         @Override
         public Graphics getGraphics() {
            return super.getGraphics();
         }
      };
      this.foreground.setSize(super.getWidth() / 2, super.getHeight());
      this.background = new JPanel() {
         @Override
         public void paint(Graphics g) {
            System.out.println("try to paint back");
            me.backgroundPainting.paintTo(me.background, (Graphics2D) g).await();
         }

         @Override
         public Graphics getGraphics() {
            throw new Error("I don't know why, but don't call getGraphics on background...");
         }
      };

      this.foregroundPainting = Painting.newPainting(this.getSize(), new TestJob()).await();
      this.backgroundPainting = Painting.newPainting(this.getSize(), new CataneMapJob()).await();

      super.getLayeredPane().add(this.foreground, 2);
      super.getLayeredPane().add(this.background, 1);

      super.pack();
      super.setLocationRelativeTo(null);

      super.getLayeredPane().setVisible(true);
      super.setVisible(true);

      super.addComponentListener(this.defaultComponentListener);

      new Timeout(() -> {
         System.out.println("test : ");
         System.out.println("------------------");
         System.out.println("------------------");
         System.out.println("move");
         this.foreground.setBounds(50, 50, 50, 50);
         new Timeout(() -> {
            System.out.println("move");
            // this.background.repaint();
            this.foreground.setBounds(100, 100, 100, 100);
            new Timeout(() -> {
               System.out.println("hide");
               this.foreground.setVisible(false);
            }, 5000);
         }, 5000);
      }, 5000);
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
