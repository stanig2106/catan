import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

class FlickerAWT extends Canvas {

   public static void main(String[] args) {
      System.setProperty("sun.awt.noerasebackground", "true");

      Frame f = new Frame(str);
      // this line change nothing
      // JFrame f = new JFrame(str);
      f.add(new FlickerAWT());
      f.pack();

      int frameWidth = f.getWidth();
      int frameHeight = f.getHeight();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      f.setLocation(screenSize.width / 2 - frameWidth / 2, screenSize.height / 2 - frameHeight / 2);
      f.setVisible(true);
   }

   private Color bgColor;
   private Color contentColor;
   Font f = new Font("Georgia", Font.BOLD, 16);
   static String str = "AWT Canvas Resize Flickering";

   public FlickerAWT() {
      Random r = new Random();
      bgColor = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
      contentColor = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
   }

   public Dimension getPreferredSize() {
      FontMetrics fm = getFontMetrics(f);
      return new Dimension(fm.stringWidth(str) + 20, fm.getHeight() + 10);
   }

   public void paint(java.awt.Graphics g) {
      g.setColor(bgColor);
      g.fillRect(0, 0, getWidth(), getHeight());
      g.setColor(contentColor);
      g.setFont(f);
      FontMetrics fm = g.getFontMetrics(f);
      int dx = getWidth() / 2 - (fm.stringWidth(str) / 2);
      int dy = getHeight() / 2 + (fm.getHeight() / 2);
      g.drawString(str, dx, dy);
   }
}
