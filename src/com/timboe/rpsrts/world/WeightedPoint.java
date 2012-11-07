package com.timboe.rpsrts.world;

import java.util.HashSet;

import com.timboe.rpsrts.managers.Utility;
import com.timboe.rpsrts.sprites.Sprite;

public class WeightedPoint {
	private final Utility utility = Utility.GetUtility();

	int x;
	int y;
	WorldPoint myLocation;

	HashSet<WeightedPoint> nieghbour_collection = new HashSet<WeightedPoint>();

	HashSet<Sprite> mySprites = new HashSet<Sprite>();
	HashSet<Sprite> myCollisions = new HashSet<Sprite>();

	public WeightedPoint(final int _x, final int _y) {
		x = _x;
		y = _y;
		myLocation = new WorldPoint(_x, _y);
	}

	public void AddNieghbour(final WeightedPoint _n) {
		nieghbour_collection.add(_n);
	}

	@Override
	public boolean equals(final Object _to_compare) {
		if (_to_compare.getClass() != this.getClass()) return false;
		return (this.x == ((WeightedPoint)_to_compare).x && this.y == ((WeightedPoint)_to_compare).y);
	}

	public boolean GetBad() { //If bad tile then bad, else if bad object
		if (myCollisions.size() > 0) return true;
		return false;
	}


	public WorldPoint GetLoc() {
		return myLocation;
	}

	public HashSet<WeightedPoint> GetNieghbours() {
		return nieghbour_collection;
	}

	public HashSet<Sprite> GetOwnedSprites() {
		return mySprites;
	}

	public WorldPoint GetPoint() {
		return new WorldPoint(x,y);
	}

	public int GetX() {
		return x;
	}

//	public void Render(Graphics2D _g2, AffineTransform _af) {
//		if (GetBad() == false) return;
//		_g2.setColor(Color.WHITE);
//		_g2.setTransform(_af);
//		_g2.drawOval(x-1, y-1, 2, 2);
//	}

	public int GetY() {
		return y;
	}

	public void GiveCollision(final Sprite _s) {
		if (utility.Seperation(myLocation, _s.GetLoc()) < ( _s.GetR() + (2*utility.actorRadius)) ) {//TODO 2x?
			myCollisions.add(_s);
		}
	}


//	public WeightedPoint GetCameFrom(int ID) {
//		if (weights.containsKey(ID)) {
//			return weights.get(ID).came_from;
//		}
//		return null;
//	}

//	public float GetF_Score(int ID) {
//		//if (weights.size() > 30) Prune();
//		if (weights.containsKey(ID)) {
//			return weights.get(ID).f_score;
//		}
//		return -1;
//	}

//	public float GetH_Score(int ID) { //unused
//		if (weights.containsKey(ID)) {
//			return weights.get(ID).h_score;
//		}
//		return -1;
//	}

//	public float GetG_Score(int ID) {
//		if (weights.containsKey(ID)) {
//			return weights.get(ID).g_score;
//		}
//		return -1;
//	}

	public void GiveSprite(final Sprite _s) {
		if (utility.Seperation(myLocation, _s.GetLoc()) < ( _s.GetR() + (2*utility.actorRadius)) ) { //TODO 2x?
			myCollisions.add(_s);
		}
		mySprites.add(_s);
	}

	@Override
	public int hashCode() {
		//System.out.println("<HASHCODE CALLED>");
		return (104527*x)+(103573*y);
		//return new HashCode(83) .append(x).append(y).toHashCode();
	}


	public void RemoveSprite(final Sprite _s) {
		myCollisions.remove(_s);
		mySprites.remove(_s);
	}

//	public void SetCameFrom(int ID, WeightedPoint _cf) {
//		if (weights.containsKey(ID)) {
//			weights.get(ID).came_from = _cf;
//		} else {
//			System.out.println("FATAL: WP: SetCameFrom when ID not in map! SET SOMETHING ELSE (c,f,g) FIRST");
//		}
//	}

//	public void SetF_Score(int ID, float _fs) {
//		if (weights.containsKey(ID)) {
//			weights.get(ID).f_score = _fs;
//		} else {
//			weights.put(ID, new PTWeight(0,0,_fs));
//		}
//	}

//	public void SetG_Score(int ID, float _gs) {
//		if (weights.containsKey(ID)) {
//			weights.get(ID).g_score = _gs;
//		} else {
//			weights.put(ID, new PTWeight(_gs, 0, 0));
//		}
//	}

//	public void SetH_Score(int ID,float _hs) {
//		if (weights.containsKey(ID)) {
//			weights.get(ID).h_score = _hs;
//		} else {
//			weights.put(ID, new PTWeight(0, _hs, 0));
//		}
//	}

//	int PruneBelow(Integer _key) {
//		//System.out.println("PRUNED NODE ("+x+","+y+")");
//		//while (weights.size() > 5) {
//			int s = weights.size();
//			weights.headMap(_key).clear();
//			return s - weights.size();
//		//	weights.remove(weights.firstKey());
//		//}
//	}
}
