// hills produce brick,
// forests produce lumber,
// mountains produce ore,
// fields produce grain,
// and pastures produce wool

package map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import globalVariables.GameVariables;
import map.constructions.Building;
import map.constructions.City​​;
import map.constructions.Colony;
import map.constructions.Route;
import map.ressources.Ressources;
import util_my.Coord;
import util_my.Promise;
import util_my.directions.LandCorner;
import util_my.directions.LandSide;
import globalVariables.ViewVariables;

import java.awt.*;

public abstract class Land {
   protected int number = -1;
   private final Optional<Ressources> produce;
   public final Map<LandSide, Optional<Land>> neighbors = new HashMap<LandSide, Optional<Land>>() {
      {
         Stream.of(LandSide.values()).forEach((landSide) -> this.put(landSide, Optional.empty()));
      }
   };
   public final Map<LandSide, Border> borders = new HashMap<LandSide, Border>() {
      {
         Stream.of(LandSide.values()).forEach((landSide) -> this.put(landSide, null));

      }
   };
   public final Map<LandCorner, Corner> corners = new HashMap<LandCorner, Corner>() {
      {
         Stream.of(LandCorner.values()).forEach((landCorner) -> this.put(landCorner, null));
      }
   };

   public final Promise<Image> image;
   public Coord coord;

   Land(Optional<Ressources> produce, Promise<Image> image) {
      this.produce = produce;
      this.image = image;
   }

   public void setNeighbor(LandSide side, Land newNeighbor) {
      this.neighbors.replace(side, Optional.of(newNeighbor));
      newNeighbor.neighbors.replace(side.getOpposite(), Optional.of(this));

      Border border;
      if ((border = newNeighbor.borders.get(side.getOpposite())) == null)
         this.borders.replace(side, new Border());
      else
         this.borders.replace(side, border);

      Corner corner;

      Corner cornerNewNeighbor = newNeighbor.corners.get(side.getOpposite().getCornerCounterClockwise());
      Corner cornerOtherNeighbor = this.neighbors.get(side.getSideClockwise()).map((otherNeighbor) -> {
         return otherNeighbor.corners.get(side.getSideClockwise().getOpposite().getCornerClockwise());
      }).orElse(null);
      if (cornerNewNeighbor == null && cornerOtherNeighbor == null)
         corner = new Corner();
      else if (cornerNewNeighbor != null)
         corner = cornerNewNeighbor;
      else
         corner = cornerOtherNeighbor;
      this.corners.replace(side.getCornerClockwise(), corner);
      corner.adjacentLands.add(this);

      cornerNewNeighbor = newNeighbor.corners.get(side.getOpposite().getCornerClockwise());
      cornerOtherNeighbor = this.neighbors.get(side.getSideCounterClockwise()).map((otherNeighbor) -> {
         return otherNeighbor.corners.get(side.getSideCounterClockwise().getOpposite().getCornerCounterClockwise());
      }).orElse(null);
      if (cornerNewNeighbor == null && cornerOtherNeighbor == null)
         corner = new Corner();
      else if (cornerNewNeighbor != null)
         corner = cornerNewNeighbor;
      else
         corner = cornerOtherNeighbor;

      this.corners.replace(side.getCornerCounterClockwise(), corner);
      corner.adjacentLands.add(this);
   }

   public void addMissingBorderAndCorner() {
      this.borders.entrySet().stream().forEach(entry -> {
         if (this.borders.get(entry.getKey()) == null)
            this.borders.replace(entry.getKey(), new Border());
      });
      this.corners.entrySet().stream().forEach(entry -> {
         if (this.corners.get(entry.getKey()) == null)
            this.corners.replace(entry.getKey(), new Corner());
      });
   }

   public void linkAllBorderAndCorner() {
      this.borders.entrySet().forEach(entry -> {
         final LandSide landSide = entry.getKey();
         final Border border = entry.getValue();
         // link border - border
         Stream.of(landSide.getAdjacentsSides()).map(adjacentsLandSide -> this.borders.get(adjacentsLandSide))
               .forEach(adjacentBorder -> border.adjacentBorders.add(adjacentBorder));

         // link [start-end] border
         Border clockwiseBorder = this.borders.get(landSide.getSideClockwise());
         Border CounterClockwiseBorder = this.borders.get(landSide.getSideCounterClockwise());
         Set<LandSide> arbitrarySplit = Set.of(LandSide.topRight, LandSide.right, LandSide.bottomRight);

         border.startAdjacentBorders.add(arbitrarySplit.contains(landSide) ? clockwiseBorder : CounterClockwiseBorder);
         border.endAdjacentBorders.add(arbitrarySplit.contains(landSide) ? CounterClockwiseBorder : clockwiseBorder);

         // border - corner // corner - border
         Stream.of(landSide.getAdjacentsCorners())
               .map(adjacentLandCorner -> this.corners.get(adjacentLandCorner))
               .forEach(adjacentCorner -> {
                  if (!border.adjacentCorners.contains(adjacentCorner))
                     border.adjacentCorners.add(adjacentCorner);
                  if (!adjacentCorner.adjacentBorders.contains(border))
                     adjacentCorner.adjacentBorders.add(border);
               });
      });

      // link corner - corner
      this.corners.entrySet().forEach(entry -> {
         Stream.of(entry.getKey().getAdjacentsCorners()).map(adjacentLandCorner -> this.corners.get(adjacentLandCorner))
               .forEach(adjacentCorner -> {
                  if (!entry.getValue().adjacentCorners.contains(adjacentCorner))
                     entry.getValue().adjacentCorners.add(adjacentCorner);
               });
      });

   }

   public Optional<Land> getNeighbor(LandSide side) {
      return this.neighbors.get(side);
   }

   public void setNumber(int value) {
      if (value < 2 || value > 12)
         throw new Error("value of land can only be between 2 and 12");
      this.number = value;
   }

   public int getNumber() {
      return this.number;
   }

   public boolean canSetBuilding(LandCorner corner, Building building) {
      try {
         this.trowIfIllegalBuild(this.corners.get(corner).building, building, this.corners.get(corner));
      } catch (BUILD e) {
         return false;
      }
      return true;
   }

   public void throwIfCanNotSetBuilding(LandCorner corner, Building building) throws BUILD {
      this.trowIfIllegalBuild(this.corners.get(corner).building, building, this.corners.get(corner));
   }

   public void setBuilding(LandCorner corner, Building building) throws BUILD {
      this.trowIfIllegalBuild(this.corners.get(corner).building, building, this.corners.get(corner));

      this.corners.get(corner).building.ifPresent(oldBuilding -> oldBuilding.owner.buildings.remove(oldBuilding));
      this.corners.get(corner).building = Optional.of(building);
      building.addToPlayer();
   }

   public Stream<Building> buildings() {
      return this.corners.values().stream().map((border) -> {
         return border.building;
      }).filter((building) -> {
         return building.isPresent();
      }).map((building) -> {
         return building.get();
      });
   }

   public Optional<Building> getBuilding(LandCorner corner) {
      return this.corners.get(corner).building;
   }

   private void trowIfIllegalBuild(Optional<Building> oldBuilding, Building newBuilding, Corner cornerPosition)
         throws BUILD {
      if (cornerPosition.adjacentCorners.stream().anyMatch(adjacentCorner -> adjacentCorner.building.isPresent()))
         throw new BUILD.BUILDING_NEAR_BUILDING();
      if (oldBuilding.isEmpty()) {
         if (newBuilding instanceof City​​)
            throw new BUILD.CITY_WITHOUT_COLONY();
         return;
      }
      if (oldBuilding.get().owner != newBuilding.owner)
         throw new BUILD.BUILDING_ON_OPPONENT_BUILDING();
      if (oldBuilding.get() instanceof City​​)
         throw new BUILD.BUILDING_ON_CITY();
      if (newBuilding instanceof Colony)
         throw new BUILD.COLONY_ON_COLONY();
   }

   public boolean canSetRoute(LandSide side, Route route) { // FIXME: check color player
      if (this.borders.get(side).adjacentBorders.stream().noneMatch(adjacentBorder -> adjacentBorder.route.isPresent())
            && this.borders.get(side).adjacentCorners.stream()
                  .noneMatch(adjacentCorner -> adjacentCorner.building.isPresent()))
         return false;
      return !this.borders.get(side).route.isPresent();
   }

   public void throwIfCanNotSetRoute(LandSide side, Route route) throws BUILD {
      System.out.println("TODO: Land::throwIfCanNotSetRoute");
      // TODO:
   }

   public void setRoute(LandSide side, Route route) throws BUILD {
      if (this.borders.get(side).adjacentBorders.stream().noneMatch(adjacentBorder -> adjacentBorder.route.isPresent())
            && this.borders.get(side).adjacentCorners.stream()
                  .noneMatch(adjacentCorner -> adjacentCorner.building.isPresent()))
         throw new BUILD.ORPHELIN_ROUTE(); // FIXME: sometime, throw but false (top of the map, check links between
                                           // lands)
      if (this.borders.get(side).route.isPresent())
         throw new BUILD.ROUTE_ON_ROUTE();
      this.borders.get(side).route = Optional.of(route);
      route.addToPlayer();
   }

   public Optional<Route> getRoute(LandSide side) {
      return this.borders.get(side).route;
   }

   public boolean isProducerLand() {
      return this.produce.isPresent();
   }

   public Optional<Ressources> getRessource() {
      return this.produce;
   }

   @Override
   public String toString() {
      return "" + this.getClass().getName().charAt(4) + this.number;
   }

   // class Exception
   public abstract static class BUILD extends Exception {

      public static class BUILDING_NEAR_BUILDING extends BUILD {

      }

      public static class BUILDING_ON_OPPONENT_BUILDING extends BUILD {

      }

      public static class CITY_WITHOUT_COLONY extends BUILD {

      }

      public static class ORPHELIN_ROUTE extends BUILD {

      }

      public static class ROUTE_ON_ROUTE extends BUILD {

      }

      public static class BUILDING_ON_CITY extends BUILD {

      }

      public static class COLONY_ON_COLONY extends BUILD {

      }
   }
}

class Hill extends Land {
   static final Promise<Image> image = ViewVariables.importImage("assets/lands/Hill.png", 2000);

   Hill() {
      super(Optional.of(Ressources.Brick), Hill.image);
   }
}

class Forest extends Land {
   static final Promise<Image> image = ViewVariables.importImage("assets/lands/Forest.png", 2000);

   Forest() {
      super(Optional.of(Ressources.Lumber), Forest.image);
   }

}

class Mountain extends Land {
   static final Promise<Image> image = ViewVariables.importImage("assets/lands/Mountain.png", 2000);

   Mountain() {
      super(Optional.of(Ressources.Ore), Mountain.image);
   }
}

class Field extends Land {
   static final Promise<Image> image = ViewVariables.importImage("assets/lands/Field.png", 2000);

   Field() {
      super(Optional.of(Ressources.Wheat), Field.image);
   }
}

class Pasture extends Land {
   static final Promise<Image> image = ViewVariables.importImage("assets/lands/Pasture.png", 2000);

   Pasture() {
      super(Optional.of(Ressources.Wool), Pasture.image);
   }
}

class Desert extends Land {
   static final Promise<Image> image = ViewVariables.importImage("assets/lands/Desert.png", 2000);

   Desert() {
      super(Optional.empty(), Desert.image);
   }
}
