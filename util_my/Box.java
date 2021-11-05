package util_my;

public class Box<T> {
   public T data;

   public Box(T data) {
      this.data = data;
   }

   public Box() {
      this.data = null;
   }
}
