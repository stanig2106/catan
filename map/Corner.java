package map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import map.constructions.Building;

public class Corner {
   public Optional<Building> building = Optional.empty();
   public List<Land> adjacentLands = new ArrayList<Land>(); 
}
