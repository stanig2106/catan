package util_my;

import java.awt.Point;

public class Line {
   final Point p1;
   final Point p2;

   public Line(Point p1, Point p2) {
      this.p1 = p1;
      this.p2 = p2;
   }

   public double angleV1(Line line) {
      double angle1 = Math.atan2(this.p1.getY() - this.p2.getY(),
            this.p1.getX() - this.p2.getX());
      double angle2 = Math.atan2(line.p1.getY() - line.p2.getY(),
            line.p1.getX() - line.p2.getX());
      return Math.toDegrees(angle1 - angle2 + 1.5 * Math.PI) % 360;
   }

   public double distance() {
      return p1.distance(p2);
   }

   public static Line abscise() {
      return new Line(new Point(0, 0), new Point(0, 1));
   }
}
