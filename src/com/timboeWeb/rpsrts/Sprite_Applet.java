package com.timboeWeb.rpsrts;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import com.timboe.rpsrts.Bitmaps;
import com.timboe.rpsrts.GameWorld;
import com.timboe.rpsrts.Sprite;
import com.timboe.rpsrts.SpriteManager;

public class Sprite_Applet extends Sprite {
//	Bitmaps theBitmaps;

	
	public Sprite_Applet(int _ID, int _x, int _y, int _r, GameWorld _gw, Bitmaps _bm,
			SpriteManager _sm) {
		super(_ID, _x, _y, _r, _gw, _sm);
		
		//theBitmaps = _bm;
	}

	public void Render(Graphics2D _g2, AffineTransform _af, AffineTransform _af_translate_zoom, AffineTransform _af_shear_rotate, AffineTransform _af_none, int _tick_count) {
		System.out.println("BAD RENDER CALLED");
		//Override
	}
	
}
