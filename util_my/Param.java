package util_my;

public class Param extends Pair<String, String> {
   public Param(String key, String value) {
      super(key, value);
   }

   public Param(String key) {
      super(key, "");
   }
}