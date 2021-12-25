package map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.awt.*;
import config.Config;
import globalVariables.ViewVariables;
import map.constructions.Route;
import player.Player;
import util_my.Box;
import util_my.Coord;
import util_my.HexagonalGrids;
import util_my.Pair;
import util_my.Promise;
import view.painting.jobs.CatanMapJob;

public class CatanMap extends HexagonalGrids<Land> {
   public final static Promise<Image> backgroundImage = ViewVariables.importImage("assets/Background.png");

   public final Robber robber;

   public CatanMap() {
      super(Config.mapRadius);
      initRandomLand();
      this.robber = new Robber(
            this.getAll().stream().filter((land) -> {
               return (land instanceof Desert);
            }).findFirst().orElse(this.get(new Coord(0, 0))));
      CatanMapJob.init(this);
   }

   public CatanMap(Land[] lands) {
      super(Config.mapRadius);

      Box<Integer> i = Box.of(0);
      this.forEachCoordinate(c -> {
         lands[i.value].coord = c;
         this.set(c, lands[i.value++]);
      });
      this.linkAllLand();

      this.robber = new Robber(
            this.getAll().stream().filter((land) -> {
               return (land instanceof Desert);
            }).findFirst().orElse(this.get(new Coord(0, 0))));
      CatanMapJob.init(this);

   }

   private void initRandomLand() {
      Queue<Land> lands = CatanMap.getRandomLands(this.numberOfCase());
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

      Box<Integer> altIndex = Box.of(0);
      int[] altOrders = new int[] { 0, 3, 7, 12, 16, 17, 18, 15, 11, 6, 2, 1, 4, 8, 13, 14, 10, 5, 9 };
      int[] altMove = new int[] { 5, 2, 6, 3, 8, 10, 9, 12, 11, 4, 8, 10, 9, 4, 5, 6, 3, 11 };
      IntStream.of(altOrders).boxed().forEach((Integer altOrder) -> {
         if (res.get(altOrder) instanceof Desert)
            return;
         res.get(altOrder).setNumber(altMove[altIndex.value]);
         altIndex.value++;
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

   public List<Pair<Integer, Player>> getRouteLength() {
      // TODO: calculate path length
      if (null == null)
         throw new Error("NI");
      List<List<Route>> pathsSummary = new ArrayList<List<Route>>();
      this.forEach(land -> {
         land.borders.values().stream().filter(border -> border.route.isPresent())
               .filter(border /* border with route */ -> pathsSummary.stream()
                     .noneMatch(path -> path.contains(border.route.get())))
               .forEach(border /* border with route and not in pathsSummary */ -> {

                  border.adjacentBorders.stream()
                        .filter(adjacentBorder -> adjacentBorder.route.isPresent()
                              && adjacentBorder.route.get().owner.equals(border.route.get().owner))
                        .map(adjacentBorder /* adjacentBorder with route of same owner as border's route */ -> border.route
                              .get())
                        .forEach(adjacentRoute /* adjacentRoute with same owner */ -> {
                           if (pathsSummary.stream()
                                 .filter(path -> path.contains(border.route.get())).peek(path -> {
                                    path.add(border.route.get());
                                 }).count() == 0)
                              pathsSummary.add(new ArrayList<Route>() {
                                 {
                                    this.add(border.route.get());
                                 }
                              });
                        });

               });

      });

      return pathsSummary.stream().sorted(Comparator.comparingInt(List::size))
            .collect(Collectors.groupingBy(path -> {
               return path.get(0).owner;
            })).entrySet().stream().map(entry -> Pair.of(entry.getKey(),
                  entry.getValue().stream().max(Comparator.comparingInt(List::size)).get().size()))
            .map(Pair::inverse)
            .collect(Collectors.toList());
   }

   @Override
   public String toString() {
      Box<String> res = Box.of("");
      this.forEachLine((List<Land> line) -> {
         line.forEach((Land land) -> {
            res.value += land + " ";
         });
         res.value += "\n";
      });
      return res.value;
   }

   public String toWeb() {
      return this.getAll().stream().map(Land::toWeb).collect(Collectors.joining("&"));
   }

   public static CatanMap fromWeb(String s) {
      return new CatanMap(Stream.of(s.split("&")).map(Land::fromWeb).toArray(Land[]::new));
   }
}
