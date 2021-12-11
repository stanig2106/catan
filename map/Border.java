package map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import map.constructions.Route;

public class Border {
   Optional<Route> route = Optional.empty();
   List<Land> adjacentLands = new ArrayList<Land>(); 
}
