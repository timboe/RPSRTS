package com.timboeAndroid.rpsrts;

import android.graphics.Canvas;
import android.graphics.Matrix;

import com.timboe.rpsrts.ObjectOwner;
import com.timboe.rpsrts.Spoogicles;

public class Spoogicles_App extends Spoogicles {

	protected Spoogicles_App(int _ID, int _x, int _y, ObjectOwner _oo, int _spoogicles, float _scale) {
		super(_ID, _x, _y, _oo, _spoogicles, _scale);
	}
	
	public void Render(Canvas canvas, Matrix _af, Matrix _af_translate_zoom, Matrix _af_shear_rotate, Matrix _af_none, int _tick_count) {
		///TODO
	}	
}
