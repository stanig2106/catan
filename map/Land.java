// hills produce brick,
// forests produce lumber,
// mountains produce ore,
// fields produce grain,
// and pastures produce wool

package map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import util_my.directions.LandCorner;
import util_my.directions.LandSide;

abstract class Land {
   protected int number;

   public final Land[] neighbors = new Land[LandSide.values().length];

   public final Route[] routes = new Route[LandSide.values().length];
   public final Building[] buildings = new Building[LandCorner.values().length];

   public void setNeighbor(LandSide side, Land newNeighbor) {
      this.neighbors[side.ordinal()] = newNeighbor;
      newNeighbor.neighbors[side.getOpposite().ordinal()] = this;
   }

   public Land getNeighbor(LandSide side) {
      return this.neighbors[side.ordinal()];
   }

   public void setNumber(int value) {
      if (value < 2 || value > 12)
         throw new Error("value of land can only be between 2 and 12");
      this.number = value;
   }

   public void setBuilding(LandCorner corner, Building building) {
      this.buildings[corner.ordinal()] = building;
      Building.getNewLandLinks(corner).entrySet().stream().forEach((Entry<LandSide, LandCorner> landEntry) -> {
         Land neighbor = this.neighbors[landEntry.getKey().ordinal()];
         if (neighbor == null)
            return;
         neighbor.buildings[landEntry.getValue().ordinal()] = building;
      });
   }

   public void setRoute(LandSide side, Route route) {
      this.routes[side.ordinal()] = route;
      this.neighbors[side.ordinal()].routes[side.getOpposite().ordinal()] = route;
   }

   @Override
   public String toString() {
      return "" + this.getClass().getName().charAt(4) + this.number;
   }
}

abstract class ProducingLand<T extends Ressource> extends Land {
   protected Supplier<T> T_new;

   ProducingLand(Supplier<T> RessourceGen) {
      this.T_new = RessourceGen;
   }

   public List<T> getRessource() {
      List<T> res = new ArrayList<T>();
      IntStream.range(0, this.number).forEach((__) -> res.add((T) this.T_new.get()));
      return res;
   }
}

class Hill extends ProducingLand<Brick> {
   Hill() {
      super(Brick::new);
   }
}

class Forest extends ProducingLand<Lumber> {
   Forest() {
      super(Lumber::new);
   }

}

class Mountain extends ProducingLand<Ore> {
   Mountain() {
      super(Ore::new);
   }
}

class Field extends ProducingLand<Grain> {
   Field() {
      super(Grain::new);
   }
}

class Pasture extends ProducingLand<Wool> {
   Pasture() {
      super(Wool::new);
   }
}

class Desert extends Land {

}
