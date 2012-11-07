package com.timboe.rpsrts.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

import com.timboe.rpsrts.enumerators.ResourceType;
import com.timboe.rpsrts.sprites.Resource;

public class Resource_App extends Resource {
	Bitmap[] spriteGraphic;
	Bitmaps_App theBitmaps = Bitmaps_App.GetBitmaps_App();

	public Resource_App(final int _ID, final int _x, final int _y, final int _r, final ResourceType _type) {
		super(_ID, _x, _y, _r, _type);
	}

	public void Render(final Canvas canvas, final Matrix _af, final Matrix _af_translate_zoom, final Matrix _af_shear_rotate, final Matrix _af_none, final int _tick_count) {
		final float[] transform = new float[2];
		transform[0] = x;
		transform[1] = y;
		_af_shear_rotate.mapPoints(transform);
		final int _x = (int)transform[0];
		final int _y = (int)transform[1];

		int anim = stuff / 5;
		if (anim > 5) {
			anim = 5;
		}

		if (type == ResourceType.Tree) {
			canvas.setMatrix(_af_translate_zoom);
			final Rect box = new Rect(_x - r
					, _y - r - 6
					, _x + r
					, _y + r);
			canvas.drawBitmap(theBitmaps.tree[anim], null, box, null);
		} else if (type == ResourceType.Cactus) {
			canvas.setMatrix(_af_translate_zoom);
			final Rect box = new Rect(_x - r
					, _y - r - 6
					, _x + r
					, _y + r);
			canvas.drawBitmap(theBitmaps.cactus[anim], null, box, null);
		} else if (type == ResourceType.Mine) {
			canvas.setMatrix(_af);
			final Rect box = new Rect(x - r
					, y - r
					, x + r
					, y + r);
			canvas.drawBitmap(theBitmaps.mine[toDraw], null, box, null);
		} else if (type == ResourceType.Rockpile) {
			canvas.setMatrix(_af_translate_zoom);
			final Rect box = new Rect(_x - r
					, _y - r
					, _x + r
					, _y + r);
			canvas.drawBitmap(theBitmaps.stone[toDraw], null, box, null);
		}
//		if (not_reachable_penalty > 0) {
//			_g2.setColor(Color.red);
//			_g2.setTransform(_af);
//			_g2.fillOval(x - r, y - r, r * 2, r * 2);
//		}
//		if (theSpriteManager.utility.dbg == true) {
//			_g2.setColor(Color.white);
//			_g2.setTransform(_af_translate_zoom);
//			_g2.drawString(Integer.toString(stuff), _x - r, _y - r);
//		}
	}
}
