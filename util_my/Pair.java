package util_my;

public class Pair<K, V> implements java.util.Map.Entry<K, V> {
   K key;
   V value;

   public Pair(K key, V value) {
      this.key = key;
      this.value = value;
   }

   @Override
   public K getKey() {
      return this.key;
   }

   @Override
   public V getValue() {
      return this.value;
   }

   @Override
   public V setValue(V value) {
      V oldValue = this.value;
      this.value = value;
      return oldValue;
   }

   public K setKey(K key) {
      K oldValue = this.key;
      this.key = key;
      return oldValue;
   }
}
