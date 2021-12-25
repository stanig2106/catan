package util_my;

public class Box<T> {
   public T value;

   private Box(T data) {
      this.value = data;
   }

   public static <T> Box<T> of(T data) {
      return new Box<T>(data);
   }

   public static <T> Box<T> of() {
      return new Box<T>(null);
   }
}
