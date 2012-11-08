package com.timboe.rpsrts.world;

import java.util.HashSet;

import com.timboe.rpsrts.managers.Utility;
import com.timboe.rpsrts.sprites.Sprite;

public class WeightedPoint {
	private final Utility utility = Utility.GetUtility();

	private int x;
	private int y;
	private WorldPoint myLocation;

	private HashSet<WeightedPoint> nieghbour_collection = new HashSet<WeightedPoint>();

	private HashSet<Sprite> mySprites = new HashSet<Sprite>();
	private HashSet<Sprite> myCollisions = new HashSet<Sprite>();

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

	public int GetY() {
		return y;
	}

	public void GiveCollision(final Sprite _s) { //These are objects around me I may need to run collision detection on
		if (utility.Seperation(myLocation, _s.GetLoc()) < ( _s.GetR() + (2*utility.actorRadius)) ) {//TODO 2x?
			myCollisions.add(_s);
		}
	}

	public void GiveSprite(final Sprite _s) { //These are objects that 'sit on me' (mine)
		if (utility.Seperation(myLocation, _s.GetLoc()) < ( _s.GetR() + (2*utility.actorRadius)) ) { //TODO 2x?
			myCollisions.add(_s);
		}
		mySprites.add(_s);
	}

	@Override
	public int hashCode() {
		return (104527*x)+(103573*y);
	}


	public void RemoveSprite(final Sprite _s) {
		myCollisions.remove(_s);
		mySprites.remove(_s);
	}

}
