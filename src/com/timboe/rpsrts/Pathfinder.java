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

//    	if (killMe == true) return;
//		//System.out.println("GOING AFTER ("+toSprite.GetX()+","+toSprite.GetY()+")!");
//		//Do path finding
//
//    	if (fromSprite == null || toSprite == null) {
//    		killMe = true;
//    		return;
//    	}
//    	
//    	final HashSet<WeightedPoint> closed_set = new HashSet<WeightedPoint>();
//    	final HashSet<WeightedPoint> open_set = new HashSet<WeightedPoint>();
//    	
//		//final Vector<WeightedPoint> closed_set = new Vector<WeightedPoint>();
//		//final Vector<WeightedPoint> open_set = new Vector<WeightedPoint>();
//
//		final float DistToDest = utility.Seperation(fromSprite.GetLoc(), toSprite.GetLoc());
//		final WeightedPoint start = new WeightedPoint(fromSprite.GetX(), fromSprite.GetY(), 0, DistToDest, DistToDest);
//		open_set.add(start);
//
//		WeightedPoint soloution = null;
//		int loop = 0;
//		while (open_set.size() != 0) {
//			//While there are points in the Open Set
//			WeightedPoint A = null;
//			float _min_f = utility.minimiser_start;
//			//Find Open set point with smallest f
//			for (final WeightedPoint wp_os : open_set) {
//				if (wp_os.GetF_Score() < _min_f) {
//					_min_f = wp_os.GetF_Score();
//					A = wp_os;
//				}
//			}
//			//Is this the soloution?
//			if ( (++loop > utility.pathfinding_max_depth)
//					|| utility.Seperation(A.GetPoint(), toSprite.GetLoc()) < (utility.pathfinding_accuracy + fromSprite.GetR() + toSprite.GetR())) {
//					//|| (Math.abs(A.GetX() - _to.GetX()) <= pathfinding_accuracy && Math.abs(A.GetY() - _to.GetY()) <= pathfinding_accuracy) ) {
//				//YIPPEEE (or bail out and get somewhat closer)
//				soloution = A;
//				break;
//			}
//			//Remove this point from open set and add to closed set
//			closed_set.add(A);
//			open_set.remove(A);
//
//			//System.out.println("LOOKING AROUND ("+A.GetX()+","+A.GetY()+") WITH F_MIN "+_min_f );
//
//			//loop over neighbour nodes
//			for (int neighbour = 0; neighbour < 8; ++neighbour) {
//				//Get coords
//				int neighbour_x = 0;
//				int neighbour_y = 0;
//				switch (neighbour) {
//					case 0: neighbour_x = A.GetX() + utility.pathfinding_accuracy; neighbour_y = A.GetY() + utility.pathfinding_accuracy; break;
//					case 1: neighbour_x = A.GetX() - utility.pathfinding_accuracy; neighbour_y = A.GetY() + utility.pathfinding_accuracy; break;
//					case 2: neighbour_x = A.GetX() + utility.pathfinding_accuracy; neighbour_y = A.GetY() - utility.pathfinding_accuracy; break;
//					case 3: neighbour_x = A.GetX() - utility.pathfinding_accuracy; neighbour_y = A.GetY() - utility.pathfinding_accuracy; break;
//
//					case 4: neighbour_x = A.GetX() + utility.pathfinding_accuracy; neighbour_y = A.GetY(); break;
//					case 5: neighbour_x = A.GetX() - utility.pathfinding_accuracy; neighbour_y = A.GetY(); break;
//					case 6: neighbour_x = A.GetX(); neighbour_y = A.GetY() + utility.pathfinding_accuracy; break;
//					case 7: neighbour_x = A.GetX(); neighbour_y = A.GetY() - utility.pathfinding_accuracy; break;
//				}
//				//Is this pixel legit? Let's check
//				if ( theSpriteManager.CheckSafe(neighbour_x, neighbour_y, utility.pathfinding_accuracy, toSprite.GetID(), fromSprite.GetID()) == false) {
//					continue; //Pixel is whack
//				}
//				
//				//OK - so the pixel is safe as pyjamas - is the pixel already in the closed set?
//				boolean inClosedSet = false;
//				for (final WeightedPoint wp_cs : closed_set) {
//					if (wp_cs.GetX() == neighbour_x && wp_cs.GetY() == neighbour_y) {
//						inClosedSet = true;
//					}
//				}
//				if (inClosedSet == true) {
//					continue; //It is? Right - not interested, next neighbour
//				}
//
//				//OK, so the neighbour is legit and is not is the closed set - let's get its score - see how it does
//				WeightedPoint B = new WeightedPoint(neighbour_x, neighbour_y, 0, 0, 0); //MAY NEED TO SET g, h or f
//
//				final float DistAtoB = utility.Seperation(A.GetX(), B.GetX(), A.GetY(), B.GetY()); //Math.sqrt( Math.pow(A.GetX() - B.GetX(),2) + Math.pow(A.GetY() + B.GetY(), 2) );
//				final float tentative_g_score = A.GetG_Score() + DistAtoB;
//
//				//Now we know the weight of this node: can we find it in the _open_ set.
//				//And if we can, is it better?
//				boolean inOpenSet = false;
//				boolean tentative_is_better = false;
//				for (final WeightedPoint wp_os : open_set) {
//					if (wp_os.GetX() == neighbour_x && wp_os.GetY() == neighbour_y) {
//						inOpenSet = true;
//						//overwrite current pointer
//						B = null;//GC
//						B = wp_os;
//						if (tentative_g_score <= B.GetG_Score()) {
//							tentative_is_better = true;
//						}
//					}
//				}
//				if (inOpenSet == false) {
//					tentative_is_better = true;
//				}
//
//				//Right, so we have have either plucked an existing object from the open set
//				//or we have a new B which we've added to the open set
//				if (tentative_is_better) {
//					//Node needs updating;
//					B.SetCameFrom(A);
//					B.SetG_Score(tentative_g_score);
//					final float DistBtoGoal = utility.Seperation( toSprite.GetX(), B.GetX(), toSprite.GetY(), B.GetY());// Math.sqrt( Math.pow(B.GetX() - _d.getX(), 2) + Math.pow(B.GetY() - _d.getY(), 2) );
//					B.SetH_Score(DistBtoGoal);
//					B.SetF_Score(tentative_g_score + DistBtoGoal);
//					//System.out.println("GOT CLOSER - Dist:"+DistBtoGoal);
//				}
//
//				if (inOpenSet == false) {
//					open_set.add(B);
//					tentative_is_better = true;
//				}
//			}
//		}
//		if (soloution != null) {
//			result = new Vector<WorldPoint>();
//			result.add(soloution.GetPoint());
//			WeightedPoint p = soloution.GetCameFrom();
//			while (true) {
//				if (p == null) {
//					break;
//				}
//				p = p.GetCameFrom();
//				if (p == null) {
//					break;
//				}
//				result.add(p.GetPoint());
//				//System.out.println(" STEP: ("+p.GetX()+","+p.GetY()+")");
//			}
//			//System.out.println("FROM: ("+x+","+y+")");
//			//System.out.println("INFO Took "+loop+" alg loops: PATH FINDING MINIMISED, WE CAN GET TO ("+soloution.GetX()+","+soloution.GetY()+") in "+result.size()+" steps! ");
//		} else {
//			//System.out.println("FATAL Took "+loop+" alg loops : PATH FINDING EPIC FAIL!");
//			result = null;
//		}
//		closed_set.clear();
//		open_set.clear();
//		Kill();
//    }

}






////	System.out.println("GOING FROM ("+fromSprite.GetX()+","+fromSprite.GetY()+") TO ("+toSprite.GetX()+","+toSprite.GetY()+")!");
////Do path finding
//
//if (fromSprite == null || toSprite == null) {
//	Kill();
//	return;
//}
//
////final HashSet<WeightedPoint> closed_set = new HashSet<WeightedPoint>();
////	final HashSet<WeightedPoint> open_set = new HashSet<WeightedPoint>();
//
//final HashMap<WorldPoint,PTWeight> _closed_set = new HashMap<WorldPoint,PTWeight>();
//final HashMap<WorldPoint,PTWeight> _open_set = new HashMap<WorldPoint,PTWeight>();
//
//
//final float DistToDest = utility.Seperation(fromSprite.GetLoc(), toSprite.GetLoc());
////get nearest accessible
//WeightedPoint closed_set_starter_node = theSpriteManager.ClipToGrid(fromSprite.GetLoc());
//
//
////couldn't find a start node
//if (closed_set_starter_node == null) {
//	Kill();
//	return;
//}
//WorldPoint closed_set_starter_point = closed_set_starter_node.GetLoc();
//
////final WeightedPoint start = new WeightedPoint(fromSprite.GetX(), fromSprite.GetY(), 0, DistToDest, DistToDest);
////closed_set_starter.SetG_Score(ID, 0);
////closed_set_starter.SetH_Score(ID, DistToDest);
////closed_set_starter.SetF_Score(ID, DistToDest);
////open_set.add(closed_set_starter);
//
////explicityly new objects
////int _x = closed_set_starter_node.GetX();
////int _y = closed_set_starter_node.GetY();
//PTWeight start = new PTWeight(0,  DistToDest, DistToDest);
//start.SetLoc(closed_set_starter_point.x,closed_set_starter_point.y);
//_open_set.put(closed_set_starter_point, start);
//
//PTWeight soloution = null;
//int loop = 0;
//while (_open_set.size() != 0) {
//	//While there are points in the Open Set
//	PTWeight A = null;
//	WorldPoint A_loc = null;
//	float _min_f = utility.minimiser_start;
//	//Find Open set point with smallest f
//	for (WorldPoint _w : _open_set.keySet()) {
//		PTWeight _p = _open_set.get(_w);
//		if (_p.f_score < _min_f) {
//			_min_f = _p.f_score;
//			A = _p;
//			A_loc = _w;
//		}
//	}
////	for (final WeightedPoint wp_os : open_set) {
////		if (wp_os.GetF_Score(ID) < _min_f) {
////			_min_f = wp_os.GetF_Score(ID);
////			A = wp_os;
////		}
////	}
//	
//	//Is this the soloution?
//	if ( (++loop > utility.pathfinding_max_depth)
//			|| utility.Seperation(A_loc, toSprite.GetLoc()) < (utility.pathfinding_accuracy + fromSprite.GetR() + toSprite.GetR())) {
//			//|| (Math.abs(A.GetX() - _to.GetX()) <= pathfinding_accuracy && Math.abs(A.GetY() - _to.GetY()) <= pathfinding_accuracy) ) {
//		//YIPPEEE (or bail out and get somewhat closer)
//		//System.out.println("-------------------FOUND-------------------" );
//		soloution = A;
//		break;
//	}
//	//Remove this point from open set and add to closed set
//	_closed_set.put(A_loc,A);
//	_open_set.remove(A_loc);
//	//_closed_set.put(A);
//	//_open_set.remove(A);
//
//	WeightedPoint a = theSpriteManager.thePathfinderGrid.point_collection_map.get(A_loc);
//	//System.out.println("LOK ARND ("+A_loc.x+","+A_loc.y+") W F_MIN:"+_min_f+", G:"+A.g_score+" H:"+A.h_score+" AND NGNBRS "+a.GetNieghbours().size()+", OPEN SET SIZE:"+_open_set.size()+" C.S SIZE:"+_closed_set.size() );
//	HashSet<WeightedPoint> myNeighbours = a.GetNieghbours();
//	for (WeightedPoint b : myNeighbours) {
//		//Bad tile?
//		if (b.GetBad() == true) continue;
//		
//		WorldPoint B_loc = b.GetLoc();
//		
//		//Is it in the closed set?
//		if (_closed_set.containsKey(B_loc) == true) continue;
//		//if (closed_set.contains(B) == true) continue;
//		
//		//Neighbour is legit & not is the closed set - let's get its score - see how it does
//		final float DistAtoB = utility.Seperation(A_loc, B_loc);
//		final float tentative_g_score = A.g_score + DistAtoB;
////		final float DistAtoB = utility.Seperation(A.GetLoc(), B.GetLoc());
////		final float tentative_g_score = A.GetG_Score(ID) + DistAtoB;
//
//		
//		//Now we know the weight of this node: add to open set if not already in
//		//is it better?
//		//boolean inOpenSet = false;
//		PTWeight B = _open_set.get(B_loc);
//		if (B == null) {
//			B = new PTWeight(-1, -1, -1);
//			_open_set.put(B_loc, B);
//		}
//		
//		boolean tentative_is_better = false;
//		if (B.g_score < 0 || tentative_g_score < B.g_score) {
//			tentative_is_better = true;
//		}
////		open_set.add(B);
////		boolean tentative_is_better = false;
////		if (B.GetG_Score(ID) < 0 || tentative_g_score <= B.GetG_Score(ID)) {
////			tentative_is_better = true;
////		}
//
//		//Right, so we have have either an object already in teh open set
//		//or we have a new B which we'll add to the open set
//		if (tentative_is_better) {
//			//System.out.println(" UPDT NEIGH ("+B.GetX()+","+B.GetY()+") W g_s "+tentative_g_score );
//			final float DistBtoGoal = utility.Seperation( toSprite.GetLoc(), B_loc);
//			//Node needs updating;
//			B.g_score = tentative_g_score;
//			B.h_score = DistBtoGoal;
//			B.f_score = tentative_g_score + DistBtoGoal;
//			B.SetLoc(B_loc.x, B_loc.y);
//			B.came_from = A;
//			//System.out.println("GOT CLOSER - Dist:"+DistBtoGoal);
//		}
////		if (tentative_is_better) {
////			//System.out.println(" UPDT NEIGH ("+B.GetX()+","+B.GetY()+") W g_s "+tentative_g_score );
////			final float DistBtoGoal = utility.Seperation( toSprite.GetX(), B.GetX(), toSprite.GetY(), B.GetY());
////			//Node needs updating;
////			B.SetG_Score(ID, tentative_g_score);
////			B.SetH_Score(ID, DistBtoGoal);
////			B.SetF_Score(ID, tentative_g_score + DistBtoGoal);
////			B.SetCameFrom(ID, A);
////			//System.out.println("GOT CLOSER - Dist:"+DistBtoGoal);
////		}
//	}
//}
//
//if (soloution != null) {
//	result = new Vector<WorldPoint>();
//	result.add(soloution.GetLoc());
//	PTWeight p = soloution.came_from;
//	while (true) {
//		if (p == null) {
//			break;
//		}
//		p = p.came_from;
//		if (p == null) {
//			break;
//		}
//		result.add(p.GetLoc());
//		//System.out.println(" STEP: ("+p.GetX()+","+p.GetY()+")");
//	}
//	//System.out.println("FROM: ("+x+","+y+")");
//	//System.out.println("INFO Took "+loop+" alg loops: PATH FINDING MINIMISED, WE CAN GET TO ("+soloution.GetX()+","+soloution.GetY()+") in "+result.size()+" steps! ");
//} else {
//	//System.out.println("FATAL Took "+loop+" alg loops : PATH FINDING EPIC FAIL!");
//	result = null;
//}
//_closed_set.clear();
//_open_set.clear();
//Kill();
//}