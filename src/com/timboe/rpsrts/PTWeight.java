package com.timboe.rpsrts;

public class PTWeight implements Comparable<PTWeight> {
	int X;
	int Y;
	float g_score;
	float h_score;
	float f_score;
	PTWeight came_from;
	
//	@Override
//	public boolean equals(Object _to_compare) {
//		//System.out.println(": IN COMPARE :");
//		if (_to_compare.getClass() != this.getClass()) return false;
//		return (this.x == ((WorldPoint) _to_compare).x && this.y == ((WorldPoint) _to_compare).y);
//	}
	
public PTWeight(float _g, float _h, float _f) {
//		super(_x, _y); 
		g_score = _g;
		h_score = _h;
		f_score = _f;
		came_from = null;
	}
	@Override
	public int compareTo(PTWeight arg0) {
		return (int) (this.f_score - arg0.f_score);
	}
	
	//	@Override
//	public int hashCode() {
//		//System.out.println("<HASHCODE CALLED>");
//		return (1000000*x)+y;
//	}
//	
	public WorldPoint GetLoc() {
		return new WorldPoint(X,Y);
	}
	public void SetLoc(int _x, int _y) {
		X = _x;
		Y = _y;
	}
	
}
