package com.timboe.rpsrts;

import java.util.HashMap;
import java.util.HashSet;

public class PathfinderGrid implements Runnable {
	private final Utility utility = Utility.GetUtility();

	public HashSet<WeightedPoint> point_collection = new HashSet<WeightedPoint>();
	public HashMap<WorldPoint,WeightedPoint> point_collection_map = new HashMap<WorldPoint,WeightedPoint>();
	SpriteManager theSpriteManager;	
	int step;
	
	PathfinderGrid(int _tile_size, int _world_tiles, SpriteManager _sm, GameWorld _gw) {
		theSpriteManager = _sm;
		step = utility.pathfinding_accuracy;
		
		int world_size = (_world_tiles*_tile_size);

		int ID = 0;
		for (int x = -(world_size/2); x < (world_size/2); x = x + _tile_size) {
			for (int y = -(world_size/2); y < (world_size/2); y = y + _tile_size) {
				if (_gw.tiles[ID++].GetWalkable() == false) continue; //unreachable
				WeightedPoint WP = new WeightedPoint(x+(_tile_size/2), y+(_tile_size/2));
				point_collection.add(WP);
				point_collection_map.put(new WorldPoint(x+(_tile_size/2),y+(_tile_size/2)), WP);
			}
		}
		
		//Set accessible, and set neighbours
		for (WeightedPoint _P : point_collection) {
			for (int neighbour = 0; neighbour < 8; ++neighbour) { //TODO check nn vs nnn
				int neighbour_x = 0;
				int neighbour_y = 0;
				switch (neighbour) {
					case 4: neighbour_x = _P.GetX() + _tile_size; neighbour_y = _P.GetY() + _tile_size; break;
					case 5: neighbour_x = _P.GetX() - _tile_size; neighbour_y = _P.GetY() + _tile_size; break;
					case 6: neighbour_x = _P.GetX() + _tile_size; neighbour_y = _P.GetY() - _tile_size; break;
					case 7: neighbour_x = _P.GetX() - _tile_size; neighbour_y = _P.GetY() - _tile_size; break;
					
					case 0: neighbour_x = _P.GetX() + _tile_size; neighbour_y = _P.GetY(); break;
					case 1: neighbour_x = _P.GetX() - _tile_size; neighbour_y = _P.GetY(); break;
					case 2: neighbour_x = _P.GetX(); neighbour_y = _P.GetY() + _tile_size; break;
					case 3: neighbour_x = _P.GetX(); neighbour_y = _P.GetY() - _tile_size; break;
				}

				WeightedPoint _n = point_collection_map.get(new WorldPoint(neighbour_x, neighbour_y));
			
				if (_n != null) {
					_P.AddNieghbour(_n);
				}
			}
		}
	}

	@Override
	public void run() {
//		while (theSpriteManager.thePathfinderGrid != null) { //While I'm not dead
//			int pruned = 0;
//			for (WeightedPoint _p : point_collection) {
//				pruned += _p.PruneBelow(theSpriteManager.GlobalSpriteCounter - 100); //keep last 20's pathfinding efforts
//			}
//			System.out.println("PRUNED "+pruned+" PTWeights, sprite PT counter :"+theSpriteManager.GlobalSpriteCounter);
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
	}


}
