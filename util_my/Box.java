package util_my;

public class Box<T> {
   public T value;

   private Box(T value) {
      this.value = value;
   }

   public static <T> Box<T> of(T value) {
      return new Box<T>(value);
   }

   public static <T> Box<T> of() {
      return new Box<T>(null);
   }

}
