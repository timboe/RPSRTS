package com.timboeAndroid.rpsrts;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.timboe.rpsrts.Building;
import com.timboe.rpsrts.BuildingType;
import com.timboe.rpsrts.GameWorld;
import com.timboe.rpsrts.ObjectOwner;
import com.timboe.rpsrts.SpriteManager;

public class Building_App extends Building {
	
	Bitmap[] spriteGraphic;	
	Bitmaps_App theBitmaps;
	
	public Building_App(int _ID, int _x, int _y, int _r, GameWorld _gw,
			Bitmaps_App _bm, SpriteManager _sm, BuildingType _bt, ObjectOwner _oo) {
		super(_ID, _x, _y, _r, _gw, _sm, _bt, _oo);
		
		theBitmaps = _bm;
		
		if (type == BuildingType.Base) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = _bm.base_player;
			} else {
				spriteGraphic = _bm.base_enemy;
			}
		} else if (type == BuildingType.AttractorPaper) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = _bm.attractor_paper_player;
			} else {
				spriteGraphic = _bm.attractor_paper_enemy;
			}
		} else if (type == BuildingType.AttractorRock) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = _bm.attractor_rock_player;
			} else {
				spriteGraphic = _bm.attractor_rock_enemy;
			}
		} else if (type == BuildingType.AttractorScissors) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = _bm.attractor_scissors_player;
			} else {
				spriteGraphic = _bm.attractor_scissors_enemy;
			}
		} else if (type == BuildingType.Woodshop) {
				if (_oo == ObjectOwner.Player) {
					spriteGraphic = _bm.woodshop_player;
				} else {
					spriteGraphic = _bm.woodshop_enemy;
				}
		} else if (type == BuildingType.Rockery) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = _bm.rockery_player;
			} else {
				spriteGraphic = _bm.rockery_enemy;
			}
		} else if (type == BuildingType.Smelter) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = _bm.smelter_player;
			} else {
				spriteGraphic = _bm.smelter_enemy;
			}
		}
	}
	
	public void Render(Canvas canvas, Matrix _af, Matrix _af_translate_zoom, Matrix _af_shear_rotate, Matrix _af_none, int _tick_count) {
		if (dead == true) return;
		if (_tick_count % 2 == 0) {
			++animStep;
		}
		Paint paint = new Paint();
		float[] transform = new float[2];
		transform[0] = x;
		transform[1] = y;
		_af_shear_rotate.mapPoints(transform);
		final int _x = (int)transform[0];
		final int _y = (int)transform[1];

		if (delete_hover) {
			//Can't place here
			paint.setColor(Color.RED);
			paint.setStyle(Paint.Style.FILL);
	
			final float[] _points = new float[8];
			_points[0] = x - r - 2;
			_points[1] = y - r + 2;
			_points[2] = x - r + 2;
			_points[3] = y - r - 2;		
			_points[4] = x + r + 2;
			_points[5] = y + r - 2;						
			_points[6] = x + r - 2;
			_points[7] = y + r + 2;							
			canvas.drawPoints(_points, paint);

			_points[0] =  x - r - 2;
			_points[1] =  y + r - 2;
			_points[2] =  x - r + 2;
			_points[3] =  y + r + 2;				
			_points[4] =  x + r + 2;
			_points[5] =  y - r + 2;								
			_points[6] =  x + r - 2;
			_points[7] =  y - r - 2;
			canvas.drawPoints(_points, paint);
			delete_hover = false;
			return;
		}
//
//		//Do health
		if (health < maxHealth) {
			canvas.setMatrix(_af_translate_zoom);
			paint.setColor(Color.BLACK);
			canvas.drawRect(_x - r
					, _y - r - 4 - y_offset
					, _x + r
					, _y - r - 3 - y_offset
					, paint);
			//_g2.fillRect(_x - r, _y - r - 4 - y_offset, r * 2, 1);
			if (owner == ObjectOwner.Player) {
				paint.setColor(Color.RED);
			} else {
				paint.setColor(Color.BLUE);
			}
			canvas.drawRect(_x - r
					, _y - r - 4 - y_offset
					, _x - r + Math.round(r * 2 * ((float)health/(float)maxHealth))
					, _y - r - 3 - y_offset
					, paint);
			//_g2.fillRect(_x - r, _y - r - 4 - y_offset, _x + r + (int) Math.round(r * 2 * ((double)health/(double)maxHealth) ), 1);
		}

//		if (theSpriteManager.utility.dbg == true) {
//			_g2.setTransform(_af_translate_zoom);
//			_g2.setColor(Color.red);
//			_g2.drawString(Integer.toString(GetEmployees()), _x - r, _y - r - 10);
//		}
		
		Rect box = new Rect(_x - r 
				, _y - r - y_offset
				, _x + r
				, _y + r - y_offset);
		
		if (underConstruction == true) {
			int conStep = 0;
			final double healthFrac = (double)health/(double)maxHealth;
			if (healthFrac <= 0.25) {
				conStep = 0;
			} else if (healthFrac <= 0.5) {
				conStep = 1;
			} else if (healthFrac <= 0.75) {
				conStep = 2;
			} else {
				conStep = 3;
			}

			canvas.setMatrix(_af_translate_zoom);
			if (owner == ObjectOwner.Player) {
				canvas.drawBitmap(theBitmaps.construction_player[conStep], null, box, null);
			} else {
				canvas.drawBitmap(theBitmaps.construction_enemy[conStep], null, box, null);
			}
			return;
		}

		canvas.setMatrix(_af_translate_zoom);
		canvas.drawBitmap(spriteGraphic[animStep % animSteps], null, box, null);	
	}
}
