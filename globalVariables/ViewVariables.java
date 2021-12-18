package globalVariables;

import util_my.Promise;

import java.awt.Image;
import java.awt.Toolkit;

import Jama.Matrix;

import java.awt.MediaTracker;
import java.awt.Component;

public class ViewVariables extends Component {
   static final private ViewVariables instance = new ViewVariables();
   public final static Matrix basisMatrix = new Matrix(
         new double[][] { { Math.sqrt(3), Math.sqrt(3) / 2. }, { 0, 3. / 2. } });

   public static Promise<Image> importImage(String path) {
      return instance._importImage(path);
   }

   private Promise<Image> _importImage(String path) {
      return new Promise<Image>((resolve, reject) -> {
         System.out.println("I load " + path);
         final Image res = Toolkit.getDefaultToolkit().getImage(path);
         final MediaTracker m = new MediaTracker(this);
         m.addImage(res, 1);
         try {
            m.waitForAll();
         } catch (final InterruptedException e) {
            e.printStackTrace();
            throw new Error(e);
         }
         if (res == null)
            reject.accept(new IMAGE_NOT_FOUND());
         else
            resolve.accept(res);
      }, -1);
   }

   // class Exception
   public static class IMAGE_NOT_FOUND extends Exception {
   }
}
