package com.timboeAndroid.rpsrts;

import com.timboe.rpsrts.GameWorld;
import com.timboe.rpsrts.Sprite;
import com.timboe.rpsrts.SpriteManager;

public class Sprite_App extends Sprite {
//	Bitmaps theBitmaps;
//	BufferedImage[] spriteGraphic;	
	
	Sprite_App(int _ID, int _x, int _y, int _r, GameWorld _gw, SpriteManager _sm) {
		super(_ID, _x, _y, _r, _gw, _sm);
		
		//theBitmaps = _bm;
	}

//	public void Render(Graphics2D _g2, AffineTransform _af, AffineTransform _af_translate_zoom, AffineTransform _af_shear_rotate, AffineTransform _af_none, int _tick_count) {
//		System.out.println("BAD RENDER CALLED");
//		//Override
//	}

}
