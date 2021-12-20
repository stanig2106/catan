package util_my;

public class Box<T> {
   public T data;

   private Box(T data) {
      this.data = data;
   }

   public static <T> Box<T> of(T data) {
      return new Box<T>(data);
   }

   public static <T> Box<T> of() {
      return new Box<T>(null);
   }
}
