package com.timboeWeb.rpsrts;

import java.awt.Color;
import java.awt.Graphics2D;

import com.timboe.rpsrts.WorldTile;

public class WorldTile_Applet extends WorldTile {
	
	protected Color c;

	public WorldTile_Applet(int _x, int _y, int _tile_size, int _ID) {
		super(_x, _y, _tile_size, _ID);
		c = Color.DARK_GRAY;
	}
	
	public void DrawTile (Graphics2D _g2, boolean _aa, boolean _highlight) {
		_g2.setColor(c);
		if (_highlight == true) {
			_g2.setColor(Color.DARK_GRAY);
		}
		int extra = 0;
		if (_aa == false) {
			extra = 1;
		}
		_g2.fillRect(x-extra, y-extra, tile_s+(2*extra), tile_s+(2*extra));
	}
	
	@Override
	public void Reset() {
		c = Color.DARK_GRAY;
		part_of_biome = false;
		canWalk = false;
		ownedBy = null;
	}
	
	@Override
	public void SetColour(int R, int G, int B) {
		c = new Color(R,G,B);
	}

}
