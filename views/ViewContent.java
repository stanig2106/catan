package views;

import java.awt.Color;
import java.awt.Canvas;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import Jama.Matrix;
import gameVariables.GameVariables;
import map.Border;
import map.Land;
import util_my.directions.LandSide;

public class ViewContent extends Canvas {

   public ViewContent() {
      super();
   }

   @Override
   public void paint(Graphics g) {
      System.out.println("redraw front");
      // g.setColor(new Color(22, 145, 198));
      // g.fillRect(0, 0, getWidth(), getHeight());

      int dim = (int) Math.min((super.getHeight() / 10.), super.getWidth() / (Math.sqrt(3) * 7.));
      int height = 2 * dim;
      int width = (int) (Math.sqrt(3) * dim);

      g.setColor(new Color(237, 211, 151));
      g.fillOval((int) (getWidth() / 2.) - (int) (width * 2.75), (int) (getHeight() / 2.) - (int) (height * 2.5),
            (int) (width * 5.5),
            height * 5);

      g.drawImage(ViewVariables.backgroundImage, (int) (getWidth() / 2) - (int) (11.56 * dim / 2.0),
            (int) (getHeight() / 2) - (int) (10.11 * dim / 2.0),
            (int) (11.56 * dim), (int) (10.11 * dim), this);

      GameVariables.map.forEachCoordinate(coord -> {
         Matrix position = ViewVariables.basisMatrix.times(coord.toMatrix()).times(dim);
         int x = (int) position.get(0, 0);
         int y = (int) position.get(1, 0);
         g.drawImage(GameVariables.map.get(coord).image, x + (int) (super.getWidth() / 2. - width / 2.),
               y + (int) (super.getHeight() / 2. - height / 2.), width,
               height,
               this);
      });

      List<Border> drawnBorders = new ArrayList<Border>();
      GameVariables.map.forEachCoordinate(coord -> {
         Land land = GameVariables.map.get(coord);
         LandSide.stream().forEach(landSide -> {
            Border border = land.borders.get(landSide);
            border.route.ifPresent(route -> {
               if (drawnBorders.indexOf(border) == -1) {
                  landSide.drawRouteOn(g, coord, dim, this, route);
                  drawnBorders.add(border);
               }
            });

         });
      });
   }

}