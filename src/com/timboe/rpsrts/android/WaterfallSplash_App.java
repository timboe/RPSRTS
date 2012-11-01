package com.timboe.rpsrts.android;

import android.graphics.Canvas;

import com.timboe.rpsrts.sprites.WaterfallSplash;

public class WaterfallSplash_App extends WaterfallSplash {


	public WaterfallSplash_App(int _ID, int _x, int _y, int _r) {
		super(_ID, _x, _y, _r);
	}
	
	public void Render(Canvas canvas) {
		//Only show if in `front' (this means negative Z order value)
		if (GetZOrder() > 0) {
			return;
		}

	}
	
}

