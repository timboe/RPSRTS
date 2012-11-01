package com.timboe.rpsrts.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.timboe.rpsrts.enumerators.ActorType;
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.sprites.Actor;

public class Actor_App extends Actor {
	
	Bitmap[] spriteGraphic;	
	Bitmaps_App theBitmaps = Bitmaps_App.GetBitmaps_App();

	public Actor_App(int _ID, int _x, int _y, int _r, ActorType _at, ObjectOwner _oo) {
		super(_ID, _x, _y, _r, _at, _oo);

		if (type == ActorType.Paper) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = theBitmaps.paper_player;
			} else {
				spriteGraphic = theBitmaps.paper_enemy;
			}
		} else if (type == ActorType.Rock) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = theBitmaps.rock_player;
			} else {
				spriteGraphic = theBitmaps.rock_enemy;
			}
		} else if (type == ActorType.Scissors) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = theBitmaps.scissor_player;
			} else {
				spriteGraphic = theBitmaps.scissor_enemy;
			}

		}
	}
		
	public void Render(Canvas canvas, Matrix _af, Matrix _af_translate_zoom, Matrix _af_shear_rotate, Matrix _af_none, int _tick_count) {
		if (dead == true) return;
		Paint paint = new Paint();
		float[] transform = new float[2];
		transform[0] = x;
		transform[1] = y;
		_af_shear_rotate.mapPoints(transform);
		final int _x = (int)transform[0];
		final int _y = (int)transform[1];
		
		Rect box = new Rect(_x - r 
				, _y - r
				, _x + r
				, _y + r);
		canvas.setMatrix(_af_translate_zoom);
		canvas.drawBitmap(spriteGraphic[animStep % animSteps], null, box, null);	
//
//		_g2.setTransform(_af_translate_zoom);
//		_g2.drawImage(spriteGraphic[animStep % animSteps], _x - r, _y - r, null);
//
//		//Do carry capacity
		if (carryAmount > 0) {
			paint.setColor(Color.BLACK);
			canvas.drawRect(_x - r
					, _y - r - 3
					, _x + r
					, _y - r - 2
					, paint);
			
			//_g2.setColor(Color.black);
			//_g2.fillRect(_x - r, _y - r - 3, r * 2, 1);
			//_g2.setColor(Color.green);
			paint.setColor(Color.GREEN);
			canvas.drawRect(_x - r
					, _y - r - 3
					, _x - r + Math.round(r * 2 * ((float)carryAmount/(float)strength) )
					, _y - r - 2
					, paint);
			//_g2.fillRect(_x - r, _y - r - 3, (int) Math.round(r * 2 * ((float)carryAmount/(float)strength) ), 1);
		}
//
//		//Do health
		paint.setColor(Color.BLACK);
		canvas.drawRect(_x - r
				, _y - r - 2
				, _x + r
				, _y - r - 1
				, paint);
		if (owner == ObjectOwner.Player) {
			paint.setColor(Color.RED);
		} else {
			paint.setColor(Color.CYAN);
		}
		canvas.drawRect(_x - r
				, _y - r - 2
				, _x - r + Math.round(r * 2 * ((float)health/(float)maxHealth) )
				, _y - r - 1
				, paint);
//		_g2.fillRect(_x - r, _y - r - 2, (int) Math.round(r * 2 * ((float)health/(float)maxHealth) ), 1);
//
//		canvas.setMatrix(_af);
//		_g2.setTransform(_af);
//
//		if (theSpriteManager.utility.dbg == true && wander != null) {
//			_g2.setColor(Color.yellow);
//			_g2.drawLine(x, y, wander.getX(), wander.getY());
//		}
//
//		if (theSpriteManager.utility.dbg == true && attack_target != null) {
//			int _x_off = 0;
//			int _y_off = 0;
//			if (owner == ObjectOwner.Player) {
//				_g2.setColor(Color.white);
//				_x_off -= 3;
//				_y_off -= 3;
//			} else {
//				_g2.setColor(Color.black);
//				_x_off += 3;
//				_y_off += 3;
//			}
//			_g2.drawLine(x + _x_off, y + _y_off, attack_target.GetX() + _x_off, attack_target.GetY() + _y_off);
//		}
//
//		if (owner == ObjectOwner.Player) {
//			_g2.setColor(Color.red);
//		} else {
//			_g2.setColor(Color.blue);
//		}
//
//		if (theSpriteManager.utility.dbg == true && waypoint_list != null) {
//			for (final WorldPoint p : waypoint_list) {
//				if (p == null) {
//					continue;
//				}
//				_g2.fillRect((int) p.getX()-1, (int) p.getY()-1, 2, 2);
//			}
//		}
//		if (theSpriteManager.utility.dbg == true && waypoint != null) {
//			_g2.fillRect((int) waypoint.getX()-1, (int) waypoint.getY()-1, 3, 3);
//		}
//		if (theSpriteManager.utility.dbg == true && destination != null) {
//			_g2.drawOval(destination.GetX()-4, destination.GetY()-4, 8, 8);
//		}
	}
	
	
}
