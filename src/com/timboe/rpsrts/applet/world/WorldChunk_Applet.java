package com.timboe.rpsrts.applet.world;

import java.awt.Color;
import java.awt.Graphics2D;

import com.timboe.rpsrts.world.WorldChunk;

public class WorldChunk_Applet extends WorldChunk {

	public WorldChunk_Applet(final int _x, final int _y, final int _tile_size, final int _ID) {
		super(_x, _y, _tile_size, _ID);
	}

	public void DrawTileState (final Graphics2D _g2, final boolean _useEnergy, final boolean _aa) {
		int c;
		if (_useEnergy) {
			c = Math.round(state*10);
		} else {
			if (biome_ID % 2 == 0) {
				c = 255 - (biome_ID*10);
			} else {
				c = biome_ID*10;
			}
		}
		while (c > 255) {
			c -= 255;
		}
		while (c < 0) {
			c += 255;
		}
		final Color coll =  new Color(c,c,c);

		int extra = 0;
		if (_aa == false) {
			extra = 1;
		}

		_g2.setColor(coll);
		if (biome_ID == 0) {
			_g2.setColor (Color.black);
		}
		_g2.fillRect (super.x-extra, super.y-extra, super.tile_s+(2*extra), super.tile_s+(2*extra));

		_g2.setColor(Color.white);
		_g2.fillOval(Math.round(super.GetXCentre() - 3),Math.round(super.GetYCentre() - 3),6,6);
		_g2.drawString(Integer.toString(super.GetAngle())+"°", Math.round(super.GetXCentre())-20,Math.round(super.GetYCentre()) - 10 );
	}

}
