package com.timboe.rpsrts.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.timboe.rpsrts.world.WorldChunk;

public class WorldChunk_App extends WorldChunk {

	public WorldChunk_App(final int _x, final int _y, final int _tile_size, final int _ID) {
		super(_x, _y, _tile_size, _ID);
	}

	public void DrawTileState (final Canvas canvas, final boolean _useEnergy, final boolean _aa) {
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

		int extra = 0;
		if (_aa == false) {
			extra = 1;
		}

		final Paint paint = new Paint();
		paint.setColor(Color.rgb(c, c, c));
		paint.setStyle(Paint.Style.FILL);

		if (biome_ID == 0) {
			paint.setColor(Color.BLACK);
		}

		canvas.drawRect(super.x-extra, super.y-extra, super.x + super.tile_s+(2*extra),  super.y + super.tile_s+(2*extra), paint);
		paint.setColor(Color.WHITE);
		canvas.drawCircle(Math.round(super.GetXCentre()),Math.round(super.GetYCentre()), 3, paint);
		paint.setTextSize(12);
		//canvas.drawText(Integer.toString(super.GetAngle())+"°", (int)Math.round(super.GetXCentre())-20, (int)Math.round(super.GetYCentre()) - 2, paint);
		canvas.drawText(Integer.toString(super.ID), Math.round(super.GetXCentre())-20, Math.round(super.GetYCentre()) - 2, paint);

	}

}
