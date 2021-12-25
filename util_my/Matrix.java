package util_my;

public class Matrix {
   double[][] m2x2;
   double[][] m1x2;

   public Matrix(double[][] e) {
      if (e.length != 2)
         throw new Error("NI");
      if (e[0].length != 1 && e[0].length != 2)
         throw new Error("NI");
      if (e[0].length == 1)
         m1x2 = e;
      if (e[0].length == 2)
         m2x2 = e;
   }

   public double get(int i, int j) {
      if (m1x2 != null)
         return m1x2[i][j];
      else
         return m2x2[i][j];
   }

   public Matrix times(Matrix m) {
      if (m.m1x2 == null)
         throw new Error("NI");
      if (m2x2 == null)
         throw new Error("NI");
      return new Matrix(
            new double[][] { { get(0, 0) * m.get(0, 0) + get(0, 1) * m.get(1, 0) },
                  { get(1, 0) * m.get(0, 0) + get(1, 1) * m.get(1, 0) } });
   }

   public Matrix times(double k) {
      if (m1x2 != null)
         return new Matrix(new double[][] { { m1x2[0][0] * k }, { m1x2[1][0] * k } });
      else
         return new Matrix(new double[][] { { m2x2[0][0] * k, m2x2[0][1] * k, }, { m2x2[1][0] * k, m2x2[1][1] * k } });
   }

}
