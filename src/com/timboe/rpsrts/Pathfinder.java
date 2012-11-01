package com.timboe.rpsrts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class Pathfinder implements Runnable {
	private final Utility utility = Utility.GetUtility();
	private final SpriteManager theSpriteManager = SpriteManager.GetSpriteManager();
	private final PathfinderGrid thePathfinderGrid = PathfinderGrid.GetPathfinderGrid();
	
	private Vector<WorldPoint> result = null; //TODO make me something better
	private final Sprite fromSprite;
	private final Sprite toSprite;
	private Boolean killMe = false;
	
    Pathfinder(Sprite _from, Sprite _to) {
    	fromSprite = _from;
    	toSprite = _to;
    }

    public Vector<WorldPoint> GetResult() {
    	return result;
    }

    public void Kill() {
    	killMe = true;
    }

    @Override
	public void run() {
    	if (killMe == true) return;
//System.out.println("GOING FROM ("+fromSprite.GetX()+","+fromSprite.GetY()+") TO ("+toSprite.GetX()+","+toSprite.GetY()+")!");

    	if (fromSprite == null || toSprite == null) {
    		Kill();
    		return;
    	}

    	final HashMap<WorldPoint, PTWeight> _closed_set = new HashMap<WorldPoint, PTWeight>();
    	final HashMap<WorldPoint, PTWeight> _open_set = new HashMap<WorldPoint, PTWeight>();
    	
		final float DistToDest = utility.Seperation(fromSprite.GetLoc(), toSprite.GetLoc());
		//get nearest accessible
		WeightedPoint closed_set_starter_node = theSpriteManager.ClipToGrid(fromSprite.GetLoc());
		
		//couldn't find a start node
		if (closed_set_starter_node == null) {
			Kill();
			return;
		} else if (closed_set_starter_node.GetBad() == true) { //this one no good, look around
			boolean breakout = false; //look around two deep
			for (WeightedPoint b : closed_set_starter_node.GetNieghbours()) {
				if (b.GetBad() == true) {
					for (WeightedPoint b_2 : b.GetNieghbours()) {
						if (b_2.GetBad() == false) { 
							closed_set_starter_node = b_2;
							breakout = true;
						}
						if (breakout == true) break;
					}
				} else {
					closed_set_starter_node = b;
					breakout = true;
				}
				if (breakout == true) break;
			}
		}
		
		WorldPoint closed_set_starter_point = closed_set_starter_node.GetLoc();
		PTWeight start = new PTWeight(0,  DistToDest, DistToDest);
		start.SetLoc(closed_set_starter_point.x,closed_set_starter_point.y);
		_open_set.put(closed_set_starter_point, start);
		
		PTWeight soloution = null;
		int loop = 0;
		while (_open_set.size() != 0) {
			//While there are points in the Open Set
			PTWeight A = null;//_sorted_set.first();
			WorldPoint A_loc = null;//A.GetLoc();
			float _min_f = utility.minimiser_start;
			//Find Open set point with smallest f
			for (WorldPoint _w : _open_set.keySet()) {
				PTWeight _p = _open_set.get(_w);
				if (_p.f_score < _min_f) {
					_min_f = _p.f_score;
					A = _p;
					A_loc = _w;
				}
			}
			
			//Is this the soloution?
			if ( (++loop > utility.pathfinding_max_depth)
					|| utility.Seperation(A_loc, toSprite.GetLoc()) < (utility.tiles_size + fromSprite.GetR() + toSprite.GetR())) {
					//|| (Math.abs(A.GetX() - _to.GetX()) <= pathfinding_accuracy && Math.abs(A.GetY() - _to.GetY()) <= pathfinding_accuracy) ) {
				soloution = A;
				break;
			}

			_closed_set.put(A_loc,A);
			_open_set.remove(A_loc);

			WeightedPoint a = thePathfinderGrid.point_collection_map.get(A_loc);
			
//System.out.println("LOK ARND ("+A_loc.x+","+A_loc.y+") W F_MIN:"+_min_f+", G:"+A.g_score+" H:"+A.h_score+" AND NGNBRS "+a.GetNieghbours().size()+", OPEN SET SIZE:"+_open_set.size()+" C.S SIZE:"+_closed_set.size() );
			
			HashSet<WeightedPoint> myNeighbours = a.GetNieghbours();
			for (WeightedPoint b : myNeighbours) {
				//Bad tile?
				if (b.GetBad() == true) { // System.out.println("BAD");
					continue;
				}
		
				WorldPoint B_loc = b.GetLoc();
				//Is it in the closed set?
				if (_closed_set.containsKey(B_loc) == true) continue;
				
				final float DistAtoB = utility.Seperation(A_loc, B_loc);
				final float tentative_g_score = A.g_score + DistAtoB;

				//Now we know the weight of this node: add to open set if not already in
				//is it better?
				PTWeight B = _open_set.get(B_loc);
				if (B == null) {
					B = new PTWeight(-1, -1, -1);
					_open_set.put(B_loc, B);
					//_min_f.put(B, B_loc);
				}
				
				boolean tentative_is_better = false;
				if (B.g_score < 0 || tentative_g_score < B.g_score) {
					tentative_is_better = true;
				}

				if (tentative_is_better) {
					final float DistBtoGoal = utility.Seperation( toSprite.GetLoc(), B_loc);
					B.g_score = tentative_g_score;
					B.h_score = DistBtoGoal;
					B.f_score = tentative_g_score + DistBtoGoal;
					B.SetLoc(B_loc.x, B_loc.y);
					B.came_from = A;
//System.out.println("GOT CLOSER - Dist:"+DistBtoGoal);
				}
			}
		}

		if (soloution != null) {
			result = new Vector<WorldPoint>();
			result.add(soloution.GetLoc());
			PTWeight p = soloution.came_from;
			while (true) {
				if (p == null) {
					break;
				}
				p = p.came_from;
				if (p == null) {
					break;
				}
				result.add(p.GetLoc());
			}
//System.out.println("INFO Took "+loop+" alg loops: PATH FINDING MINIMISED, WE CAN GET TO ("+soloution.X+","+soloution.Y+") in "+result.size()+" steps! ");
		} else {
			System.out.println("FATAL Took "+loop+" alg loops : PATH FINDING EPIC FAIL!");
			result = null;
		}
		_closed_set.clear();
		_open_set.clear();
		Kill();
    }
}