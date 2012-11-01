package com.timboe.rpsrts.applet;

import java.awt.Color;
import java.awt.Graphics2D;

import com.timboe.rpsrts.world.WorldChunk;

public class WorldChunk_Applet extends WorldChunk {

	public WorldChunk_Applet(int _x, int _y, int _tile_size, int _ID) {
		super(_x, _y, _tile_size, _ID);
	}
	
	public void DrawTileState (Graphics2D _g2, boolean _useEnergy, boolean _aa) {
		int c;
		if (_useEnergy) {
			c = (int)Math.round(state*10);
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
		_g2.fillOval((int)Math.round(super.GetXCentre() - 3),(int)Math.round(super.GetYCentre() - 3),6,6);
		_g2.drawString(Integer.toString(super.GetAngle())+"�", (int)Math.round(super.GetXCentre())-20,(int)Math.round(super.GetYCentre()) - 10 );
	}

}