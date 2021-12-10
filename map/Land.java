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
import java.util.Map.Entry;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import map.Land.BUILD.ROUTE_ON_ROUTE;
import map.constructions.Building;
import map.constructions.City​​;
import map.constructions.Colony;
import map.constructions.Route;
import map.ressources.Ressources;
import util_my.directions.LandCorner;
import util_my.directions.LandSide;

public abstract class Land {
   protected int number;
   private final Optional<Ressources> produce;
   private final Map<LandSide, Optional<Land>> neighbors = new HashMap<LandSide, Optional<Land>>() {
      {
         Stream.of(LandSide.values()).forEach((landSide) -> this.put(landSide, Optional.empty()));
      
   }};
   private final Map<LandSide, Border> borders = new HashMap<LandSide, Border>() {
      {
         Stream.of(LandSide.values()).forEach((landSide) -> this.put(landSide, null));
      
   }};
   private final Map<LandCorner, Corner> corners = new HashMap<LandCorner, Corner>() {
      {
         Stream.of(LandCorner.values()).forEach((landCorner) -> this.put(landCorner, null));
   }};

   Land(Optional<Ressources> produce) {
      this.produce = produce;
   }

   public void setNeighbor(LandSide side, Land newNeighbor) {
      this.neighbors.replace(side, Optional.of(newNeighbor));
      newNeighbor.neighbors.replace(side.getOpposite(), Optional.of(this));

      Border border;
      if ((border = newNeighbor.borders.get(side.getOpposite())) == null)
         this.borders.replace(side, new Border());
      else
         this.borders.replace(side, border);

      Corner cornerNewNeighbor = newNeighbor.corners.get(side.getOpposite().getCornerCounterClockwise());
      Corner cornerOtherNeighbor = this.neighbors.get(side.getSideClockwise()).map((otherNeighbor) -> {
         return otherNeighbor.corners.get(side.getSideClockwise().getOpposite().getCornerClockwise());
      }).orElse(null);
      if (cornerNewNeighbor == null && cornerOtherNeighbor == null)
         this.corners.replace(side.getCornerClockwise(), new Corner());
      else if (cornerNewNeighbor != null)
         this.corners.replace(side.getCornerClockwise(), cornerNewNeighbor);
      else 
         this.corners.replace(side.getCornerClockwise(), cornerOtherNeighbor);
         
      cornerNewNeighbor = newNeighbor.corners.get(side.getOpposite().getCornerClockwise());
      cornerOtherNeighbor = this.neighbors.get(side.getSideCounterClockwise()).map((otherNeighbor) -> {
         return otherNeighbor.corners.get(side.getSideCounterClockwise().getOpposite().getCornerCounterClockwise());
      }).orElse(null);
      if (cornerNewNeighbor == null && cornerOtherNeighbor == null)
         this.corners.replace(side.getCornerCounterClockwise(), new Corner());
      else if (cornerNewNeighbor != null)
         this.corners.replace(side.getCornerCounterClockwise(), cornerNewNeighbor);
      else 
         this.corners.replace(side.getCornerCounterClockwise(), cornerOtherNeighbor);
   }
   
   public void debug() {
      System.out.println(this.corners.get(LandCorner.bottomRight));
   }
   
   public void debug2() {
      System.out.println(this.corners.get(LandCorner.topLeft));
   }

   public void debug3() {
      System.out.println(this.corners.get(LandCorner.topRight));
   }

   public Optional<Land> getNeighbor(LandSide side) {
      return this.neighbors.get(side);
   }

   public void setNumber(int value) {
      if (value < 2 || value > 12)
         throw new Error("value of land can only be between 2 and 12");
      this.number = value;
   }

   public void setBuilding(LandCorner corner, Building building) throws BUILD {
      this.trowIfIllegalBuild(this.corners.get(corner).building, building);
      this.corners.get(corner).building = Optional.of(building);
   }

   public Optional<Building> getBuilding(LandCorner corner) {
      return this.corners.get(corner).building;
   }

   private void trowIfIllegalBuild(Optional<Building> oldBuilding, Building newBuilding) throws BUILD {
      if (oldBuilding.isEmpty()) {
         if (newBuilding instanceof City​​)
            throw new BUILD.CITY_WITHOUT_COLONY();
         return;
      }
      if (newBuilding instanceof City​​)
         throw new BUILD.BUILDING_ON_CITY();    
      if (oldBuilding.get() instanceof Colony && newBuilding instanceof Colony)
            throw new BUILD.COLONY_ON_COLONY();
   }

   public void setRoute(LandSide side, Route route) throws ROUTE_ON_ROUTE {
      if (this.borders.get(side).route.isPresent())
         throw new BUILD.ROUTE_ON_ROUTE();
      this.borders.get(side).route = Optional.of(route);
   }

   public Optional<Route> getRoute(LandSide side) {
      return this.borders.get(side).route;
   }


   public boolean isProducerLand() {
      return this.produce.isPresent();
   }

   public List<Ressources> getRessource() throws LAND_DONT_PRODUCE {
      if (this.produce.isEmpty())
         throw new LAND_DONT_PRODUCE();
      List<Ressources> res = new ArrayList<Ressources>();
      IntStream.range(0, this.number).forEach((__) -> res.add(this.produce.get()));
      return res;
   }


   @Override
   public String toString() {
      return "" + this.getClass().getName().charAt(4) + this.number;
   }

   // class Exception
   public static class LAND_DONT_PRODUCE extends Exception {
      LAND_DONT_PRODUCE() {
         super();
      }
   }

   public static class BUILD extends Exception {
      BUILD() {
         super();
      }

      public static class CITY_WITHOUT_COLONY extends BUILD {
         CITY_WITHOUT_COLONY() {
            super();
         }
      }

      public static class ROUTE_ON_ROUTE extends BUILD {
         ROUTE_ON_ROUTE() {
            super();
         }
      }

      public static class BUILDING_ON_CITY extends BUILD {
         BUILDING_ON_CITY() {
            super();
         }
      }
   
      public static class COLONY_ON_COLONY extends BUILD {
         COLONY_ON_COLONY() {
            super();
         }
      }
   }
}

class Hill extends Land {
   Hill() {
      super(Optional.of(Ressources.Brick));
   }
}

class Forest extends Land {
   Forest() {
      super(Optional.of(Ressources.Lumber));
   }

}

class Mountain extends Land {
   Mountain() {
      super(Optional.of(Ressources.Ore));
   }
}

class Field extends Land {
   Field() {
      super(Optional.of(Ressources.Grain));
   }
}

class Pasture extends Land {
   Pasture() {
      super(Optional.of(Ressources.Wool));
   }
}

class Desert extends Land {
   Desert() {
      super(Optional.empty());
   }
}
