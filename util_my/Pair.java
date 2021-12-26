package util_my;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class Pair<K, V> implements java.util.Map.Entry<K, V> {
   K key;
   V value;

   protected Pair(K key, V value) {
      this.key = key;
      this.value = value;
   }

   public static <K, V> Pair<K, V> of(K key, V value) {
      return new Pair<K, V>(key, value);
   }

   public static <A, B, C> Triple<A, B, C> tripleOf(A A_Value, B B_Value, C C_Value) {
      return new Triple<A, B, C>(A_Value, B_Value, C_Value);
   }

   public static <A, B, C> Triple<A, B, C> tripleOf(Pair<A, B> A_B_Values, C C_Value) {
      return Pair.tripleOf(A_B_Values.getKey(), A_B_Values.getValue(), C_Value);
   }

   public static <A, B, C> Triple<A, B, C> tripleOf(A A_Value, Pair<B, C> B_C_Values) {
      return Pair.tripleOf(A_Value, B_C_Values.getKey(), B_C_Values.getValue());
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

   public static class Triple<A, B, C> extends Pair<Pair<A, B>, C> {

      private Triple(A A_Value, B B_Value, C C_Value) {
         super(Pair.of(A_Value, B_Value), C_Value);
      }

      public A getA() {
         return this.getKey().getKey();
      }

      public B getB() {
         return this.getKey().getValue();
      }

      public C getC() {
         return this.getValue();
      }

   }
}
