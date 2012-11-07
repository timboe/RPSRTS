package com.timboe.rpsrts.android;

import android.graphics.Canvas;

import com.timboe.rpsrts.sprites.WaterfallSplash;

public class WaterfallSplash_App extends WaterfallSplash {


	public WaterfallSplash_App(final int _ID, final int _x, final int _y, final int _r) {
		super(_ID, _x, _y, _r);
	}

	public void Render(final Canvas canvas) {
		//Only show if in `front' (this means negative Z order value)
		if (GetZOrder() > 0) {
			return;
		}

	}

}

