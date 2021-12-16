package map;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.IntStream;

import config.Config;
import util_my.Box;
import util_my.Coord;
import util_my.HexagonalGrids;

public class CataneMap extends HexagonalGrids<Land> {
   final Thief thief;

   public CataneMap() {
      super(Config.mapRadius);
      initRandomLand();
      this.thief = new Thief(
            this.getAll().stream().filter((land) -> {
               return (land instanceof Desert);
            }).findFirst().orElse(this.get(new Coord(0, 0))));
   }

   private void initRandomLand() {
      Queue<Land> lands = CataneMap.getRandomLands(this.numberOfCase());
      this.forEachCoordinate((Coord c) -> {
         Land land = lands.remove();
         land.coord = c;
         this.set(c, land);
      });
      this.linkAllLand();
   }

   static private LinkedList<Land> getRandomLands(int numberOfLands) {
      // this : number = 19
      if (numberOfLands != 19)
         throw new Error("NI");
      LinkedList<Land> res = new LinkedList<Land>();

      res.add(new Desert());
      IntStream.range(0, 3).forEach((__) -> res.add(new Hill()));
      IntStream.range(0, 3).forEach((__) -> res.add(new Mountain()));
      IntStream.range(0, 4).forEach((__) -> res.add(new Forest()));
      IntStream.range(0, 4).forEach((__) -> res.add(new Field()));
      IntStream.range(0, 4).forEach((__) -> res.add(new Pasture()));

      Collections.shuffle(res);

      Box<Integer> altIndex = new Box<Integer>(0);
      int[] altOrders = new int[] { 0, 3, 7, 12, 16, 17, 18, 15, 11, 6, 2, 1, 4, 8, 13, 14, 10, 5, 9 };
      int[] altMove = new int[] { 5, 2, 6, 3, 8, 10, 9, 12, 11, 4, 8, 10, 9, 4, 5, 6, 3, 11 };
      IntStream.of(altOrders).boxed().forEach((Integer altOrder) -> {
         if (res.get(altOrder) instanceof Desert)
            return;
         res.get(altOrder).setNumber(altMove[altIndex.data]);
         altIndex.data++;
      });

      return res;
   }

   private void linkAllLand() {
      this.forEachCoordinate((Coord c) -> {
         this.forEachAdjacent(c, (neighbor, side) -> {
            this.get(c).setNeighbor(side, neighbor);
         });
      });
      this.forEach(land -> land.addMissingBorderAndCorner());
      this.forEach(land -> land.linkAllBorderAndCorner());
   }

   @Override
   public String toString() {
      Box<String> res = new Box<String>("");
      this.forEachLine((List<Land> line) -> {
         line.forEach((Land land) -> {
            res.data += land + " ";
         });
         res.data += "\n";
      });
      return res.data;
   }
}
