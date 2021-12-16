package map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import map.constructions.Building;

public class Corner {
   public Optional<Building> building = Optional.empty();
   public final List<Land> adjacentLands = new ArrayList<Land>();
   public final List<Border> adjacentBorders = new ArrayList<Border>();
   public final List<Corner> adjacentCorners = new ArrayList<Corner>();
}
