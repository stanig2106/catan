package util_my;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
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

}
