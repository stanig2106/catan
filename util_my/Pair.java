package util_my;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class Pair<K, V> implements java.util.Map.Entry<K, V> {
   K key;
   V value;

   private Pair(K key, V value) {
      this.key = key;
      this.value = value;
   }

   public static <K, V> Pair<K, V> of(K key, V value) {
      return new Pair<K, V>(key, value);
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

   public <NK> Pair<NK, V> mapKey(BiFunction<K, V, NK> mapFunction) {
      return Pair.of(mapFunction.apply(this.key, this.value), this.value);
   }

   public <NV> Pair<K, NV> mapValue(BiFunction<K, V, NV> mapFunction) {
      return Pair.of(this.key, mapFunction.apply(this.key, this.value));
   }

   public <R> R map(BiFunction<K, V, R> mapFunction) {
      return mapFunction.apply(this.key, this.value);
   }

   public void map(BiConsumer<K, V> mapFunction) {
      mapFunction.accept(this.key, this.value);
   }

}
