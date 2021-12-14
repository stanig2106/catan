package views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Canvas;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import util_my.Timeout;

public class View extends JFrame {
   public View() {
      super("Catane");
      // System.setProperty("sun.awt.noerasebackground", "true");
      super.setSize(800, 600);
      super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      ViewContent canvas = new ViewContent();
      ViewVariables.waitAllImageLoaded();
      System.out.println("now");

      JPanel bgTest = new JPanel() {
         @Override
         public void paint(Graphics g) {
            System.out.println("redraw back");
            g.setColor(new Color(22, 145, 198));
            g.fillRect(0, 0, super.getWidth(), super.getHeight());
         }
      };
      Canvas fgTest = new Canvas() {
         @Override
         public void paint(Graphics g) {
            System.out.println("redraw front green");
            g.setColor(new Color(0, 255, 0));
            g.fillRect(0, 0, super.getWidth(), super.getHeight());
         }
      };

      // fgTest.setVisible(true);
      bgTest.setVisible(true);
      fgTest.setSize(new Dimension(25, 25));
      canvas.setSize(new Dimension(50, 50));
      JLayeredPane layeredPane = super.getLayeredPane();

      // fgTest.setSize(new Dimension(25, 25));
      // layeredPane.setDoubleBuffered(true);
      // layeredPane.setOpaque(true);
      layeredPane.add(fgTest, 3);
      layeredPane.add(canvas, 2);
      layeredPane.add(bgTest, 1);
      new Timeout(() -> {
         System.out.println("alors ?");
         canvas.setVisible(false);
      }, 10000);

      super.addComponentListener(new ComponentListener() {
         @Override
         public void componentHidden(ComponentEvent e) {
            // TODO Auto-generated method stub

         }

         @Override
         public void componentMoved(ComponentEvent e) {
            // TODO Auto-generated method stub

         }

         @Override
         public void componentResized(ComponentEvent e) {
            bgTest.setSize(getSize());
         }

         @Override
         public void componentShown(ComponentEvent e) {
            // TODO Auto-generated method stub

         }
      });

      canvas.setSize(super.getSize());
      layeredPane.setVisible(true);

      super.setVisible(true);
   }

}
