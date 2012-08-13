package com.timboe.rpsrts;

public class WorldPoint {
	int x;
	int y;
	
	@Override
	public boolean equals(Object _to_compare) {
		//System.out.println(": IN COMPARE :");
		if (_to_compare.getClass() != this.getClass()) return false;
		return (this.x == ((WorldPoint) _to_compare).x && this.y == ((WorldPoint) _to_compare).y);
	}
	
	@Override
	public int hashCode() {
		//System.out.println("<HASHCODE CALLED>");
		return (1000000*x)+y;
	}
	
	public WorldPoint(int _x, int _y) {
		x = _x;
		y = _y;
	}
	
	public WorldPoint() {
		x = 0;
		y = 0;
	}

	public void setLocation(int _x, int _y) {
		x = _x;
		y = _y;
	}
	
	public void setX(int _x) {
		x = _x;
	}
	
	public void setY(int _y) {
		y = _y;
	}
	
	public void incrimentX(int _x) {
		x += _x;
	}
	
	public void incrimentY(int _y) {
		y += _y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

}
