package util_my;

import java.awt.*;

public class Button {
   public final Rectangle shape;
   public final String content;

   Button(Rectangle shape, String content) {
      this.shape = shape;
      this.content = content;
   }

   Button(Point position, Dimension dimension, String content) {
      this(new Rectangle(position, dimension), content);
   }

   public Button(int x, int y, int width, int height, String content) {
      this(new Rectangle(x, y, width, height), content);
   }
}
