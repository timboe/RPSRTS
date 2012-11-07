package com.timboe.rpsrts.android;

import android.graphics.Canvas;
import android.graphics.Matrix;

import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.sprites.Spoogicles;

public class Spoogicles_App extends Spoogicles {

	protected Spoogicles_App(final int _ID, final int _x, final int _y, final ObjectOwner _oo, final int _spoogicles, final float _scale) {
		super(_ID, _x, _y, _oo, _spoogicles, _scale);
	}

	public void Render(final Canvas canvas, final Matrix _af, final Matrix _af_translate_zoom, final Matrix _af_shear_rotate, final Matrix _af_none, final int _tick_count) {
		///TODO
	}
}
