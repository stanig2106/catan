package views;

import javax.swing.*;

import Jama.Matrix;
import gameVariables.GameVariables;

import java.awt.*;

public class View extends JFrame {
   private final Container content;

   public View() {
      super("Catane");
      System.setProperty("sun.awt.noerasebackground", "true");
      super.setSize(800, 600);
      super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.content = super.getContentPane();
      ViewContent canvas = new ViewContent();
      super.add(canvas);
      super.setVisible(true);
   }
}

class ViewContent extends Canvas {

   public ViewContent() {
      super();
   }

   @Override
   public void paint(Graphics g) {
      g.setColor(new Color(237, 211, 151));
      g.fillRect(0, 0, getWidth(), getHeight());

      int dim = (int) Math.min((super.getHeight() / 8.), super.getWidth() / (Math.sqrt(3) * 5.));
      int height = 2 * dim;
      int width = (int) (Math.sqrt(3) * dim);
      GameVariables.map.forEachCoordinate(coord -> {
         Matrix position = ViewVariables.basisMatrix.times(coord.toMatrix()).times(dim);
         int x = (int) position.get(0, 0);
         int y = (int) position.get(1, 0);
         g.drawImage(GameVariables.map.get(coord).image, x + (int) (super.getWidth() / 2. - width / 2),
               y + (int) (super.getHeight() / 2. - height / 2), width,
               height,
               this);
      });
   }

}