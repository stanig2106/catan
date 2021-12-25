package util_my;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class StreamUtils {
   private StreamUtils() {
   };

   public static <T> Stream<Pair<Integer, T>> StreamIndexed(Collection<T> of) {
      Box<Integer> i = Box.of(0);
      return of.stream().map(e -> Pair.of(i.value++, e));
   }

}
