package com.timboe.rpsrts.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.timboe.rpsrts.world.WorldTile;

public class WorldTile_App extends WorldTile {
	
	int r;
	int g;
	int b;

	public WorldTile_App(int _x, int _y, int _tile_size, int _ID) {
		super(_x, _y, _tile_size, _ID);
		r = 50;
		g = 50;
		b = 50;
	}
	
	public void DrawTile (Canvas canvas, boolean _aa, boolean _highlight) {
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.rgb(r,g,b));
		if (_highlight == true) {
			paint.setColor(Color.DKGRAY);
		}
		int extra = 0;
		if (_aa == false) {
			extra = 1;
		}
		canvas.drawRect(x-extra, y-extra, x+tile_s+(2*extra), y+tile_s+(2*extra), paint);
	}
	
	@Override
	public void Reset() {
		r = 50;
		g = 50;
		b = 50;
		part_of_biome = false;
		canWalk = false;
		ownedBy = null;
	}
	
	@Override
	public void SetColour(int R, int G, int B) {
		r = R;
		g = G;
		b = B;
	}

}
