package com.timboe.rpsrts.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.timboe.rpsrts.enumerators.ActorType;
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.sprites.Actor;
import com.timboe.rpsrts.sprites.Projectile;
import com.timboe.rpsrts.sprites.Sprite;

public class Projectile_App extends Projectile {
	Bitmap[] spriteGraphic;
	Bitmaps_App theBitmaps = Bitmaps_App.GetBitmaps_App();

	public Projectile_App(final int _ID, final Actor _source, final int _r, final Sprite _target) {
		super(_ID, _source, _r, _target);

		if (_source.GetOwner() == ObjectOwner.Player) {
			if (_source.GetType() == ActorType.Paper) spriteGraphic = theBitmaps.proj_paper_player;
			else if (_source.GetType() == ActorType.Rock) spriteGraphic = theBitmaps.proj_rock_player;
			else if (_source.GetType() == ActorType.Scissors) spriteGraphic = theBitmaps.proj_scissor_player;
		} else if (_source.GetOwner() == ObjectOwner.Enemy) {
			if (_source.GetType() == ActorType.Paper) spriteGraphic = theBitmaps.proj_paper_enemy;
			else if (_source.GetType() == ActorType.Rock) spriteGraphic = theBitmaps.proj_rock_enemy;
			else if (_source.GetType() == ActorType.Scissors) spriteGraphic = theBitmaps.proj_scissor_enemy;
		}
	}

	public void Render(final Canvas canvas, final Matrix _af, final Matrix _af_translate_zoom, final Matrix _af_shear_rotate, final Matrix _af_none, final int _tick_count) {
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
