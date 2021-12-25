package util_my;

public class Coord {
   public int x;
   public int y;

   public Coord(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public Matrix toMatrix() {
      return new Matrix(new double[][] { { this.x }, { this.y } });
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof Coord))
         return false;
      return ((Coord) (obj)).x == this.x && ((Coord) (obj)).y == this.y;
   }

   @Override
   public String toString() {
      return "Coord[" + x + "," + y + "]";
   }

   public String toWeb() {
      return x + "E" + y;
   }

   public static Coord fromWeb(String s) {
      String[] coord = s.split("E");

      return new Coord(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]));
   }
}
