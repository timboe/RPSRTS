package com.timboeAndroid.rpsrts;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.timboe.rpsrts.Actor;
import com.timboe.rpsrts.ActorType;
import com.timboe.rpsrts.Building;
import com.timboe.rpsrts.BuildingType;
import com.timboe.rpsrts.ObjectOwner;
import com.timboe.rpsrts.Projectile;
import com.timboe.rpsrts.Resource;
import com.timboe.rpsrts.ResourceType;
import com.timboe.rpsrts.Sprite;
import com.timboe.rpsrts.SpriteManager;
import com.timboe.rpsrts.WorldPoint;

public class SpriteManager_App extends SpriteManager {
	private static SpriteManager_App singleton = new SpriteManager_App();
	public static SpriteManager_App GetSpriteManager_App() {
		return singleton;
	}

	Bitmaps_App theBitmaps = Bitmaps_App.GetBitmaps_App();

	private SpriteManager_App() {
		super();
		this_object = (SpriteManager)this; 
	}
	
	@Override
	public Actor PlaceActor(WorldPoint _p, ActorType _at, ObjectOwner _o) {
		//NO rechecks that coordinates are safe - be warned!
		final Actor newActor = new Actor_App(++GlobalSpriteCounter
				, (int)_p.getX()
				, (int)_p.getY()
				, utility.actorRadius
				, _at
				, _o);
		synchronized (GetActorObjects()) {
			GetActorObjects().add(newActor);
		}
		return newActor;
	}
	
	@Override
	public Building PlaceBuilding(WorldPoint _p, BuildingType _bt, ObjectOwner _oo) {
		//Does NOT recheck that coordinates are safe - be warned!
		int _r = utility.buildingRadius;
		if (_bt == BuildingType.AttractorPaper
				|| _bt == BuildingType.AttractorRock
				|| _bt == BuildingType.AttractorScissors) {
			_r = utility.attractorRadius;
		}
		final Building newBuilding = new Building_App(++GlobalSpriteCounter
				, (int)_p.getX()
				, (int)_p.getY()
				, _r
				, _bt
				, _oo);
		synchronized (GetBuildingOjects()) {
			GetBuildingOjects().add(newBuilding);
		}
		synchronized (GetCollisionObjects()) {
			GetCollisionObjects().add(newBuilding);
		}
		return newBuilding;
	}
	
	@Override
	public void PlaceProjectile(Actor _source, Sprite _target) {
		final Projectile newProjectile = new Projectile_App(++GlobalSpriteCounter
				, _source
				, utility.projectileRadius
				, _target);
		synchronized (GetProjectileObjects()) {
			GetProjectileObjects().add(newProjectile);
		}
	}

	@Override
	public Resource PlaceResource(WorldPoint _p, ResourceType _rt, boolean AddToTempList) {
		//NO rechecks that coordinates are safe - be warned!
		final int _r = utility.resourceRadius;
		//System.out.println("new actor b4");
		final Resource newResource = new Resource_App(++GlobalSpriteCounter
				, (int)_p.getX()
				, (int)_p.getY()
				, _r
				, _rt);
		if (AddToTempList == false) {
			synchronized (GetResourceObjects()) {
				GetResourceObjects().add(newResource);
			}
			synchronized (GetCollisionObjects()) {
				GetCollisionObjects().add(newResource);
			}
		} else { //resource can't corrupt it's own list while it's ticking
			GetTempResourceObjects().add(newResource);
		}
		return newResource;
	}

	public void Render(Canvas canvas, Matrix _af, Matrix _af_translate_zoom, Matrix _af_shear_rotate, Matrix _af_none) {
		for (final Sprite _Z : GetSpritesZOrdered()) {
			if (_Z.GetIsActor() == true) {
				((Actor_App) _Z).Render(canvas, _af, _af_translate_zoom, _af_shear_rotate, _af_none, TickCount);
			} else if (_Z.GetIsResource() == true) {
				((Resource_App) _Z).Render(canvas, _af, _af_translate_zoom, _af_shear_rotate, _af_none, TickCount);
			} else if (_Z.GetIsBuilding() == true) {
				((Building_App) _Z).Render(canvas, _af, _af_translate_zoom, _af_shear_rotate, _af_none, TickCount);
			} else if (_Z.GetIsProjectile() == true) {
				((Projectile_App) _Z).Render(canvas, _af, _af_translate_zoom, _af_shear_rotate, _af_none, TickCount);
			} else if (_Z.GetIsSpoogicle() == true) {
				((Spoogicles_App) _Z).Render(canvas, _af, _af_translate_zoom, _af_shear_rotate, _af_none, TickCount);
			}
		}
	}
	
	
	public void SpecialRender(Canvas canvas, Matrix _af, Matrix _af_translate_zoom, Matrix _af_shear_rotate, Matrix _af_none, int _x, int _y, Bitmap[] _graphic, boolean drawingTopBar) {
		float[] transform = new float[2];
		transform[0] = _x;
		transform[1] = _y;
		_af_shear_rotate.mapPoints(transform);
		//transform = _af_shear_rotate.  .transform(new Point(_x, _y), transform);
		final int __x = (int)transform[0];// (int)Math.round(transform.getX());
		final int __y = (int)transform[1];//(int)Math.round(transform.getY());

		int _r = 0;
		int _y_offset = 0;
		final int animSteps = 4;
		if (_graphic == theBitmaps.attractor_paper_player
				||  _graphic == theBitmaps.attractor_scissors_player
				||  _graphic == theBitmaps.attractor_rock_player) {
			_r = 4;
			_y_offset = 14;
		} else if (_graphic == theBitmaps.off || _graphic == theBitmaps.on) {
			_r = 6;
			_y_offset = -6;
		} else if (_graphic == theBitmaps.paper_player 
				|| _graphic == theBitmaps.rock_player
				|| _graphic == theBitmaps.scissor_player) {
			_r = 3;
			_y_offset = 4;
		} else {
			_r = 8;
		}

		if (_graphic == theBitmaps.X) {
			if (drawingTopBar == false) {
				canvas.setMatrix(_af);
			} else {
				//_g2.setTransform(_af_none);
			}
			//Can't place here
			Paint paint = new Paint();
			paint.setColor(Color.RED);
			paint.setStyle(Paint.Style.FILL);

			final float[] _points = new float[8];
//			final int[] _y_points = new int[4];
//			_x_points[0] = _x - _r - 2;
//			_x_points[1] = _x - _r + 2;
//			_x_points[2] = _x + _r + 2;
//			_x_points[3] = _x + _r - 2;
//			_y_points[0] = _y - _r + 2;
//			_y_points[1] = _y - _r - 2;
//			_y_points[2] = _y + _r - 2;
//			_y_points[3] = _y + _r + 2;
			
			_points[0] = _x - _r - 2;
			_points[1] = _y - _r + 2;
			
			_points[2] = _x - _r + 2;
			_points[3] = _y - _r - 2;
							
			_points[4] = _x + _r + 2;
			_points[5] = _y + _r - 2;
											
			_points[6] = _x + _r - 2;
			_points[7] = _y + _r + 2;
													
			canvas.drawPoints(_points, paint);
			//_g2.fillPolygon(_x_points, _y_points, 4);

//			_x_points[0] = _x - _r - 2;
//			_x_points[1] = _x - _r + 2;
//			_x_points[2] = _x + _r + 2;
//			_x_points[3] = _x + _r - 2;
//			_y_points[0] = _y + _r - 2;
//			_y_points[1] = _y + _r + 2;
//			_y_points[2] = _y - _r + 2;
//			_y_points[3] = _y - _r - 2;
			
			_points[0] =  _x - _r - 2;
			_points[1] =  _y + _r - 2;
					
			_points[2] =  _x - _r + 2;
			_points[3] =  _y + _r + 2;
									
			_points[4] =  _x + _r + 2;
			_points[5] =  _y - _r + 2;
													
			_points[6] =  _x + _r - 2;
			_points[7] =  _y - _r - 2;
			
			canvas.drawPoints(_points, paint);
			//_g2.fillPolygon(_x_points, _y_points, 4);
			return;
		}

		if (drawingTopBar == false) {
			canvas.setMatrix(_af_translate_zoom);
			if (_graphic != null) {
				Rect box = new Rect(__x - _r,    __y - _r - _y_offset,    __x + _r,   __y + _r - _y_offset);
				canvas.drawBitmap(_graphic[TickCount/2 % animSteps], null, box, null);
				//_g2.drawImage(_graphic[TickCount/2 % animSteps], __x - _r, __y - _r - _y_offset, null);
			}
		} else { //Drawing top bar
			if (_graphic != null) {
				Rect box = new Rect(_x - _r,    _y - _r - _y_offset,    _x + _r,   _y + _r - _y_offset);
				canvas.drawBitmap(_graphic[TickCount/2 % animSteps], null, box, null);
			}
		}
	}
	
	
	public boolean TryPlaceItem(BuildingType _bt, Canvas canvas,
			Matrix _af, Matrix _af_translate_zoom, Matrix _af_shear_rotate, Matrix _af_none,
			int _mouse_x, int _mouse_y, boolean _place_remove) {
		boolean closeToExisting = false;
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		//_g2.setColor(Color.white);
		//_g2.setTransform(_af);
		canvas.setMatrix(_af);
		int radius_to_check = utility.buildingRadius;
		boolean doDistanceCheck = true;
		if (_bt == BuildingType.AttractorPaper
				|| _bt == BuildingType.AttractorRock
				|| _bt == BuildingType.AttractorScissors) {
			radius_to_check = utility.attractorRadius;
			doDistanceCheck = false;
		} else { //do distance check
			doDistanceCheck = true;
			for (final Building _b : GetBuildingOjects()) {
				if (_b.GetOwner() != ObjectOwner.Player) {
					continue;
				}
				if (_b.GetCollects().size() == 0) { //not gatherer
					continue;
				}
				for (int angle = (TickCount%utility.building_Place_degrees); angle <= utility.wg_DegInCircle; angle += utility.building_Place_degrees) {
					RectF _rect = new RectF(_b.GetX() - utility.building_gather_radius,
							_b.GetY() - utility.building_gather_radius,
							_b.GetX() + utility.building_gather_radius,
							_b.GetY() + utility.building_gather_radius);
					canvas.drawArc(_rect, angle, utility.building_Place_degrees_show, false, paint);
					//_g2.drawArc(_b.GetX() - utility.building_gather_radius, _b.GetY() - utility.building_gather_radius,
					//		utility.building_gather_radius * 2, utility.building_gather_radius * 2, angle, 1);
				}
				if (utility.Seperation(_mouse_x, _b.GetX(), _mouse_y, _b.GetY()) < utility.building_gather_radius) {
					closeToExisting = true;
				}
			}
		}

		if (_bt == BuildingType.X) {
			//REMOVE BUILDING
			for (final Building _b : GetBuildingOjects()) {
				if (utility.Seperation(_mouse_x, _b.GetX(), _mouse_y, _b.GetY()) < utility.buildingRadius) {
					if (_b.GetType() == BuildingType.Base) {
						continue;
					}
					if (_b.GetOwner() == ObjectOwner.Enemy) {
						continue;
					}
					_b.DeleteHover();
					if (_place_remove == true) {
						resource_manager.CanAffordBuy(_b.GetType(), ObjectOwner.Player, false, true);
						_b.Kill();
						return true;
					}
				}
			}
		} else if ( CheckSafe(true,true,_mouse_x, _mouse_y, radius_to_check, 0, 0) == true
				&& resource_manager.CanAffordBuy(_bt, ObjectOwner.Player, false, false)
				&& ( (doDistanceCheck == true && closeToExisting == true) || doDistanceCheck == false) ) {
			//PLACE BUILDING and location is A'OK
			if (_place_remove == true) {
				PlaceBuilding(new WorldPoint(_mouse_x,_mouse_y), _bt, ObjectOwner.Player);
				resource_manager.CanAffordBuy(_bt, ObjectOwner.Player, true, false);
				return true;
			}

			//Draw with player base, special method, for it has the render code
			if (_bt == BuildingType.Woodshop) {
				SpecialRender(canvas, _af, _af_translate_zoom, _af_shear_rotate, _af_none, _mouse_x, _mouse_y, theBitmaps.woodshop_player, false);
			} else if (_bt == BuildingType.Rockery) {
				SpecialRender(canvas, _af, _af_translate_zoom, _af_shear_rotate, _af_none, _mouse_x, _mouse_y, theBitmaps.rockery_player, false);
			} else if (_bt == BuildingType.Smelter) {
				SpecialRender(canvas, _af, _af_translate_zoom, _af_shear_rotate, _af_none, _mouse_x, _mouse_y, theBitmaps.smelter_player, false);
			} else if (_bt == BuildingType.AttractorPaper) {
				SpecialRender(canvas, _af, _af_translate_zoom, _af_shear_rotate, _af_none, _mouse_x, _mouse_y, theBitmaps.attractor_paper_player, false);
			} else if (_bt == BuildingType.AttractorRock) {
				SpecialRender(canvas, _af, _af_translate_zoom, _af_shear_rotate, _af_none, _mouse_x, _mouse_y, theBitmaps.attractor_rock_player, false);
			} else if (_bt == BuildingType.AttractorScissors) {
				SpecialRender(canvas, _af, _af_translate_zoom, _af_shear_rotate, _af_none, _mouse_x, _mouse_y, theBitmaps.attractor_scissors_player, false);
			}

			//Draw circle of collection
			if (doDistanceCheck == true) {
				paint.setColor(Color.WHITE);
				//_g2.setColor(Color.white);
				//_g2.setTransform(_af);
				canvas.setMatrix(_af);
				for (int angle = (TickCount%utility.building_Place_degrees); angle <= utility.wg_DegInCircle; angle += utility.building_Place_degrees) {
					RectF _rect = new RectF(_mouse_x - utility.building_gather_radius,
							_mouse_y - utility.building_gather_radius,
							_mouse_x + utility.building_gather_radius,
							_mouse_y + utility.building_gather_radius);
					canvas.drawArc(_rect, angle, utility.building_Place_degrees_show, false, paint);
					//_g2.drawArc(_mouse_x - utility.building_gather_radius, _mouse_y - utility.building_gather_radius,
					//		utility.building_gather_radius * 2, utility.building_gather_radius * 2, angle, 1);
				}
			}
		} else {
			//Not safe, show X
			SpecialRender(canvas, _af, _af_translate_zoom, _af_shear_rotate, _af_none, _mouse_x, _mouse_y, theBitmaps.X, false);
		}
		return false;
	}

}
