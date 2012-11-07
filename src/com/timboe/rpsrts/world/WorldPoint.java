package com.timboe.rpsrts.world;

public class WorldPoint {
	int x;
	int y;

	public WorldPoint() {
		x = 0;
		y = 0;
	}

	public WorldPoint(final int _x, final int _y) {
		x = _x;
		y = _y;
	}

	@Override
	public boolean equals(final Object _to_compare) {
		//System.out.println(": IN COMPARE :");
		if (_to_compare.getClass() != this.getClass()) return false;
		return (this.x == ((WorldPoint) _to_compare).x && this.y == ((WorldPoint) _to_compare).y);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public int hashCode() {
		//System.out.println("<HASHCODE CALLED>");
		return (1000000*x)+y;
	}

	public void incrimentX(final int _x) {
		x += _x;
	}

	public void incrimentY(final int _y) {
		y += _y;
	}

	public void setLocation(final int _x, final int _y) {
		x = _x;
		y = _y;
	}

	public void setX(final int _x) {
		x = _x;
	}

	public void setY(final int _y) {
		y = _y;
	}

}
