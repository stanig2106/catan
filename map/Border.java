package map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import map.constructions.Route;

public class Border {
   public Optional<Route> route = Optional.empty();
   public final List<Border> adjacentBorders = new ArrayList<Border>();

   // juste note that [start-end]AdjacentBorders have a border in commun
   public final List<Border> startAdjacentBorders = new ArrayList<Border>();
   public final List<Border> endAdjacentBorders = new ArrayList<Border>();

   public final List<Corner> adjacentCorners = new ArrayList<Corner>();
}
