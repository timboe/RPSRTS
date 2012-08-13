package com.timboeAndroid.rpsrts;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.timboe.rpsrts.Actor;
import com.timboe.rpsrts.ActorType;
import com.timboe.rpsrts.GameWorld;
import com.timboe.rpsrts.ObjectOwner;
import com.timboe.rpsrts.Projectile;
import com.timboe.rpsrts.Sprite;
import com.timboe.rpsrts.SpriteManager;

public class Projectile_App extends Projectile {
	Bitmap[] spriteGraphic;	
	Bitmaps_App theBitmaps;
	
	public Projectile_App(int _ID, Actor _source, int _r, GameWorld _gw,
			Bitmaps_App _bm, SpriteManager _sm, Sprite _target) {
		super(_ID, _source, _r, _gw, _sm, _target);

		if (_source.GetOwner() == ObjectOwner.Player) {
			if (_source.GetType() == ActorType.Paper) spriteGraphic = _bm.proj_paper_player;
			else if (_source.GetType() == ActorType.Rock) spriteGraphic = _bm.proj_rock_player;
			else if (_source.GetType() == ActorType.Scissors) spriteGraphic = _bm.proj_scissor_player;
		} else if (_source.GetOwner() == ObjectOwner.Enemy) {
			if (_source.GetType() == ActorType.Paper) spriteGraphic = _bm.proj_paper_enemy;
			else if (_source.GetType() == ActorType.Rock) spriteGraphic = _bm.proj_rock_enemy;
			else if (_source.GetType() == ActorType.Scissors) spriteGraphic = _bm.proj_scissor_enemy;
		}
	}
	
	public void Render(Canvas canvas, Matrix _af, Matrix _af_translate_zoom, Matrix _af_shear_rotate, Matrix _af_none, int _tick_count) {
//		if (dead == true) return;
//		
//		if (_tick_count % 2 == 0) {
//			++animStep;
//		}
//		
//		Point2D transform = null;
//		transform = _af_shear_rotate.transform(new Point(x, y), transform);
//		final int _x = (int)Math.round(transform.getX());
//		final int _y = (int)Math.round(transform.getY());
//				
//		_g2.setTransform(_af_translate_zoom);
//		_g2.drawImage(spriteGraphic[animStep % animSteps], _x - r, _y - r, null);
	}

}
