package util_my;

import java.awt.*;

public class Button {
   public final Rectangle shape;
   public final String content;
   public final String id;
   public boolean disabled = false;

   public Button(String id, Rectangle shape, String content) {
      this.shape = shape;
      this.content = content;
      this.id = id;
   }

   public Button(String id, Point position, Dimension dimension, String content) {
      this(id, new Rectangle(position, dimension), content);
   }

   public Button(String id, double x, double y, double width, double height, String content) {
      this(id, new Rectangle((int) x, (int) y, (int) width, (int) height), content);
   }

   public Button(String id, double width, double height, Position xPosition, Position yPosition, double xOffset,
         double yOffset, Dimension dim,
         String content) {
      this(id, xPosition.offsetCalculator(dim.getWidth(), xOffset, width),
            yPosition.offsetCalculator(dim.getHeight(), yOffset, height), width, height, content);
   }

   public enum Position {
      start, middle, end;

      public double offsetCalculator(double axeLength, double offset, double axeSize) {
         switch (this) {
            case start:
               return offset;
            case middle:
               return (axeLength - axeSize) / 2. + offset;
            case end:
               return axeLength - axeSize + offset;
            default:
               throw new EnumConstantNotPresentException(this.getClass(), this.name());
         }
      }
   }

}
