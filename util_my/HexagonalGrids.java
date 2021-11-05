package util_my;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import util_my.directions.LandSide;

public class HexagonalGrids<T> {
   public final int radius;
   private final List<List<T>> datas;
   static public final Error InvalidCoordinate = new Error("Coordinate is outside of the map");

   public HexagonalGrids(int radius) {
      this.radius = radius;
      this.datas = new ArrayList<List<T>>();
      IntStream.range(0, 2 * radius + 1).forEach((__) -> this.datas.add(new ArrayList<T>()));
      this.datas.forEach((row) -> IntStream.range(0, 2 * radius + 1).forEach((__) -> row.add(null)));
   }

   public boolean isValidCoordinate(Coord c) {
      return !(Math.abs(c.x) > this.radius || Math.abs(c.y) > this.radius || Math.abs(c.x + c.y) > this.radius);
   }

   /**
    * @throws InvalidCoordinate
    */
   private void verifyCoordinate(Coord c) {
      if (!this.isValidCoordinate(c))
         throw InvalidCoordinate;
   }

   /**
    * @throws InvalidCoordinate
    */
   public void set(Coord c, T element) {
      this.verifyCoordinate(c);
      if (element == null)
         throw new Error("Do you really want to set a null object ?");
      this.datas.get(c.x + this.radius).set(c.y + this.radius, element);
   }

   /**
    * @throws InvalidCoordinate
    */
   public T get(Coord c) {
      this.verifyCoordinate(c);
      return this.datas.get(c.x + this.radius).get(c.y + this.radius);
   }

   /**
    * @return list of not null elements of the grids
    */
   public List<T> getAllNotNull() {
      return this.datas.stream().flatMap(List::stream).filter((e) -> e != null).collect(Collectors.toList());
   }

   public List<T> getAll() {
      List<T> res = new ArrayList<T>();
      this.forEachCoordinate((Coord c) -> res.add(this.get(c)));
      return res;
   }

   public List<List<T>> getAllLine() {
      List<List<T>> res = new ArrayList<List<T>>();
      this.forEachLine((line) -> res.add(line));
      return res;
   }

   /**
    * @return list of not null adjacent elements of [x][y]
    * @throws InvalidCoordinate
    */
   public List<T> getAdjacents(Coord c) {
      this.verifyCoordinate(c);
      List<T> res = new ArrayList<T>();
      T adjacent;

      for (LandSide side : LandSide.values()) {
         try {
            adjacent = this.get(side.offsetCoord(c));
            if (adjacent != null)
               res.add(adjacent);
         } catch (Exception InvalidCoordinate) {
            continue;
         }
      }

      return res;
   }

   public void forEachCoordinate(Consumer<Coord> callback) {
      for (int y = -1 * this.radius; y <= this.radius; y++)
         for (int x = -1 * this.radius; x <= this.radius; x++)
            if (this.isValidCoordinate(new Coord(x, y)))
               callback.accept(new Coord(x, y));
   }

   public void forEachLine(Consumer<List<T>> callback) {
      List<T> acceptList = new ArrayList<T>();
      for (int y = -1 * this.radius; y <= this.radius; y++) {
         for (int x = -1 * this.radius; x <= this.radius; x++)
            if (this.isValidCoordinate(new Coord(x, y)))
               acceptList.add(this.get(new Coord(x, y)));
         callback.accept(acceptList);
         acceptList.removeIf((__) -> true);
      }
   }

   public int numberOfCase() {
      int res = 0;
      for (int x = -1 * this.radius; x <= this.radius; x++)
         for (int y = -1 * this.radius; y <= this.radius; y++)
            if (this.isValidCoordinate(new Coord(x, y)))
               res++;

      return res;
   }
}
