package map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import map.constructions.Route;

public class Border {
   public Optional<Route> route = Optional.empty();
   public final List<Border> adjacentBorders = new ArrayList<Border>();
   public final List<Corner> adjacentCorners = new ArrayList<Corner>();
}
