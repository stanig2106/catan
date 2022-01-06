package util_my;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

public final class StreamUtils {
   private StreamUtils() {
   };

   public static <T> Stream<Pair<Integer, T>> StreamIndexed(Collection<T> of) {
      Box<Integer> i = Box.of(0);
      return of.stream().map(e -> Pair.of(i.value++, e));
   }

   public static <T> Stream<Pair<Integer, T>> StreamIndexed(Stream<T> of) {
      Box<Integer> i = Box.of(0);
      return of.map(e -> Pair.of(i.value++, e));
   }

   public static <T> Stream<Pair<Integer, T>> StreamIndexed(T[] of) {
      return StreamIndexed(Stream.of(of));
   }

   public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
      Set<Object> seen = ConcurrentHashMap.newKeySet();
      return t -> seen.add(keyExtractor.apply(t));
   }

   // return the max of a stream, empty if the max is not uniq or if the stream is
   // empty
   public static <T> Collector<T, ?, Optional<T>> maxSupCollector(Comparator<? super T> comp) {
      return new Collector<T, Box<Optional<T>>, Optional<T>>() {

         @Override
         public BiConsumer<Box<Optional<T>>, T> accumulator() {
            return (box, e) -> {
               final int compared = box.value.map(value -> comp.compare(e, value)).orElse(1);
               if (compared == 0)
                  box.value = Optional.empty();
               if (compared == 1)
                  box.value = Optional.of(e);
            };
         }

         @Override
         public Set<Characteristics> characteristics() {
            return Set.of();
         }

         @Override
         public BinaryOperator<Box<Optional<T>>> combiner() {
            return (box1, box2) -> {
               if (box1.value.isEmpty() && box2.value.isEmpty())
                  return box1;
               if (box1.value.isEmpty())
                  return box2;
               if (box2.value.isEmpty())
                  return box1;

               final int compared = comp.compare(box1.value.get(), box2.value.get());
               if (compared == 0)
                  return Box.of(Optional.empty());
               if (compared > 0)
                  return box1;
               return box2;
            };
         }

         @Override
         public Function<Box<Optional<T>>, Optional<T>> finisher() {
            return (box) -> box.value;
         }

         @Override
         public Supplier<Box<Optional<T>>> supplier() {
            return () -> Box.of(Optional.empty());
         }

      };
   }

}
