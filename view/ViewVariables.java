package view;

import java.awt.Image;
import java.awt.Toolkit;

import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;

public class ViewVariables {
   public final static Matrix basisMatrix = new Matrix(
         new double[][] { { Math.sqrt(3), Math.sqrt(3) / 2. }, { 0, 3. / 2. } });

   public static final Image backgroundImage = importImage("assets/Background.png");

   private static List<Image> importedImages;

   public static Image importImage(String path) {
      return importImage(path, true);
   }

   public static Image importImage(String path, boolean important) {
      Image res = Toolkit.getDefaultToolkit().getImage(path);

      if (importedImages == null)
         importedImages = new ArrayList<Image>();
      if (important)
         ViewVariables.importedImages.add(res);
      return res;
   }

   public static void waitAllImageLoaded() {
      while (ViewVariables.importedImages.stream().anyMatch(image -> image.getHeight(null) == -1)) {
      }
   }
}
