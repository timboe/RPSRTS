package com.timboe.rpsrts.applet.world;

import java.awt.Color;
import java.awt.Graphics2D;

import com.timboe.rpsrts.world.WorldTile;

public class WorldTile_Applet extends WorldTile {

	protected Color c;

	public WorldTile_Applet(final int _x, final int _y, final int _tile_size, final int _ID) {
		super(_x, _y, _tile_size, _ID);
		c = Color.DARK_GRAY;
	}

	public void DrawTile (final Graphics2D _g2, final boolean _aa, final boolean _highlight) {
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
	public void SetColour(final int R, final int G, final int B) {
		c = new Color(R,G,B);
	}

}
