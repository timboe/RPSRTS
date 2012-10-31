package com.timboe.rpsrts;

import java.util.HashMap;
import java.util.HashSet;

public class PathfinderGrid {
	private final Utility utility = Utility.GetUtility();
	private final GameWorld theWorld = GameWorld.GetGameWorld();
	
	private static PathfinderGrid singleton = new PathfinderGrid();
	public static PathfinderGrid GetPathfinderGrid() {
		return singleton;
	}
	
	public HashSet<WeightedPoint> point_collection = new HashSet<WeightedPoint>();
	public HashMap<WorldPoint,WeightedPoint> point_collection_map = new HashMap<WorldPoint,WeightedPoint>();
	
	private PathfinderGrid() {
		System.out.println("--- PathfinderGrid Manager spawned (depends on Util,World) : "+this);
	}
	
	public void Init() {
		point_collection.clear();
		point_collection_map.clear();

		int ID = 0;
		for (int x = -utility.world_size2; x < utility.world_size2; x = x + utility.tiles_size) {
			for (int y = -utility.world_size2; y < utility.world_size2; y = y + utility.tiles_size) {
				if (theWorld.tiles[ID++].GetWalkable() == false) continue; //unreachable
				WeightedPoint WP = new WeightedPoint(x+(utility.tiles_size/2), y+(utility.tiles_size/2));
				point_collection.add(WP);
				point_collection_map.put(new WorldPoint(x+(utility.tiles_size/2),y+(utility.tiles_size/2)), WP);
			}
		}
		
		//Set accessible, and set neighbours
		for (WeightedPoint _P : point_collection) {
			for (int neighbour = 0; neighbour < 8; ++neighbour) { //TODO check nn vs nnn
				int neighbour_x = 0;
				int neighbour_y = 0;
				switch (neighbour) {
					case 4: neighbour_x = _P.GetX() + utility.tiles_size; neighbour_y = _P.GetY() + utility.tiles_size; break;
					case 5: neighbour_x = _P.GetX() - utility.tiles_size; neighbour_y = _P.GetY() + utility.tiles_size; break;
					case 6: neighbour_x = _P.GetX() + utility.tiles_size; neighbour_y = _P.GetY() - utility.tiles_size; break;
					case 7: neighbour_x = _P.GetX() - utility.tiles_size; neighbour_y = _P.GetY() - utility.tiles_size; break;
					
					case 0: neighbour_x = _P.GetX() + utility.tiles_size; neighbour_y = _P.GetY(); break;
					case 1: neighbour_x = _P.GetX() - utility.tiles_size; neighbour_y = _P.GetY(); break;
					case 2: neighbour_x = _P.GetX(); neighbour_y = _P.GetY() + utility.tiles_size; break;
					case 3: neighbour_x = _P.GetX(); neighbour_y = _P.GetY() - utility.tiles_size; break;
				}

				WeightedPoint _n = point_collection_map.get(new WorldPoint(neighbour_x, neighbour_y));
			
				if (_n != null) {
					_P.AddNieghbour(_n);
				}
			}
		}
	}
}
