package views;

import java.util.Vector;

import Jama.Matrix;

public class ViewVariables {
   public final static Matrix basisMatrix = new Matrix(
         new double[][] { { Math.sqrt(3), Math.sqrt(3) / 2. }, { 0, 3. / 2. } });
}
