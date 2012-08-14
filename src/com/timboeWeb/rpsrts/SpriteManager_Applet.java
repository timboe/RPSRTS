package com.timboeWeb.rpsrts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;

import com.timboe.rpsrts.Actor;
import com.timboe.rpsrts.ActorType;
import com.timboe.rpsrts.Building;
import com.timboe.rpsrts.BuildingType;
import com.timboe.rpsrts.GameWorld;
import com.timboe.rpsrts.ObjectOwner;
import com.timboe.rpsrts.Projectile;
import com.timboe.rpsrts.Resource;
import com.timboe.rpsrts.ResourceType;
import com.timboe.rpsrts.Spoogicles;
import com.timboe.rpsrts.Sprite;
import com.timboe.rpsrts.SpriteManager;
import com.timboe.rpsrts.Utility;
//import com.timboe.rpsrts.WeightedPoint;
import com.timboe.rpsrts.WorldPoint;


public class SpriteManager_Applet extends SpriteManager {

	Bitmaps_Applet theBitmaps;
	
	public SpriteManager_Applet(GameWorld_Applet _theWorld, Bitmaps_Applet _bm, Utility _utility) {
		super((GameWorld)_theWorld, _utility);
		theBitmaps = _bm;

	}
	
	@Override
	public Actor PlaceActor(WorldPoint _p, ActorType _at, ObjectOwner _o) {
		//NO rechecks that coordinates are safe - be warned!
		final Actor newActor = new Actor_Applet(++GlobalSpriteCounter
				, (int)_p.getX()
				, (int)_p.getY()
				, utility.actorRadius
				, theWorld
				, (Bitmaps_Applet) theBitmaps
				, this
				, _at
				, _o);
		ActorObjects.add(newActor);
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
		final Building newBuilding = new Building_Applet(++GlobalSpriteCounter
				, (int)_p.getX()
				, (int)_p.getY()
				, _r
				, theWorld
				, (Bitmaps_Applet) theBitmaps
				, this
				, _bt
				, _oo);
		GetBuildingOjects().add(newBuilding);
		//synchronized (CollisionObjectsThreadSafe) {
			GetCollisionObjects().add(newBuilding);
		//}
		return newBuilding;
	}
	
	@Override
	public void PlaceProjectile(Actor _source, Sprite _target) {
		final Projectile newProjectile = new Projectile_Applet(++GlobalSpriteCounter
				, _source
				, utility.projectileRadius
				, theWorld
				, (Bitmaps_Applet) theBitmaps
				, this
				, _target);
		GetProjectileObjects().add(newProjectile);
	}

	@Override
	public Resource PlaceResource(WorldPoint _p, ResourceType _rt, boolean AddToTempList) {
		//NO rechecks that coordinates are safe - be warned!
		final int _r = utility.resourceRadius;
		//System.out.println("new actor b4");
		final Resource newResource = new Resource_Applet(++GlobalSpriteCounter
				, (int)_p.getX()
				, (int)_p.getY()
				, _r, theWorld
				, (Bitmaps_Applet) theBitmaps
				, this
				, _rt);
		if (AddToTempList == false) {
			GetResourceObjects().add(newResource);
			GetCollisionObjects().add(newResource);
		} else { //resource can't corrupt it's own list while it's ticking
			GetTempResourceObjects().add(newResource);
		}
		return newResource;
	}
	
	@Override
	public void PlaceSpooge(int _x, int _y, ObjectOwner _oo, int _n, float _scale) {
		final Spoogicles newSpoogicles = new Spoogicles_Applet(++GlobalSpriteCounter,
				_x,
				_y,
				theWorld,
				this,
				_oo,
				_n,
				_scale);		
		GetSpoogiclesObjects().add(newSpoogicles);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void Render(Graphics2D _g2, 
			AffineTransform _af, 
			AffineTransform _af_translate_zoom, 
			AffineTransform _af_shear_rotate, 
			AffineTransform _af_none) {
//		if (thePathfinderGrid != null && utility.dbg == true) {
//			for (final WeightedPoint _w : thePathfinderGrid.point_collection) {
//				_w.Render(_g2, _af);
//			}
//		}
		HashSet<Actor_Applet> ActorObjects_Applet = (HashSet) ActorObjects;
		for (final Actor_Applet _s : ActorObjects_Applet) {
			_s.Render(_g2, _af, _af_translate_zoom, _af_shear_rotate, _af_none, TickCount);
		}
		HashSet<Building_Applet> BuildingObjects_Applet = (HashSet) BuildingOjects;
		for (final Building_Applet _s : BuildingObjects_Applet) {
			_s.Render(_g2, _af, _af_translate_zoom, _af_shear_rotate, _af_none, TickCount);
		}
		HashSet<Resource_Applet> ResourceObjects_Applet = (HashSet) ResourceObjects;
		for (final Resource_Applet _s : ResourceObjects_Applet) {
			_s.Render(_g2, _af, _af_translate_zoom, _af_shear_rotate, _af_none, TickCount);
		}
		HashSet<Projectile_Applet> ProjectileObjects_Applet = (HashSet) ProjectileObjects;
		for (final Projectile_Applet _s : ProjectileObjects_Applet) {
			_s.Render(_g2, _af, _af_translate_zoom, _af_shear_rotate, _af_none, TickCount);
		}
		HashSet<Spoogicles_Applet> SpoogiclesObjects_Applet = (HashSet) SpoogicleObjects;
		for (final Spoogicles_Applet _s : SpoogiclesObjects_Applet) {
			_s.Render(_g2, 
					_af, 
					_af_translate_zoom, 
					_af_shear_rotate, 
					_af_none,
					TickCount);
		}
	}
	
	
	public void SpecialRender(Graphics2D _g2, AffineTransform _af, AffineTransform _af_translate_zoom, AffineTransform _af_shear_rotate, AffineTransform _af_none, int _x, int _y, BufferedImage[] _graphic, boolean drawingTopBar) {
		Point2D transform = null;
		transform = _af_shear_rotate.transform(new Point(_x, _y), transform);
		final int __x = (int)Math.round(transform.getX());
		final int __y = (int)Math.round(transform.getY());

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
				_g2.setTransform(_af);
			} else {
				//_g2.setTransform(_af_none);
			}
			//Can't place here
			_g2.setColor(Color.red);
			final int[] _x_points = new int[4];
			final int[] _y_points = new int[4];
			_x_points[0] = _x - _r - 2;
			_x_points[1] = _x - _r + 2;
			_x_points[2] = _x + _r + 2;
			_x_points[3] = _x + _r - 2;

			_y_points[0] = _y - _r + 2;
			_y_points[1] = _y - _r - 2;
			_y_points[2] = _y + _r - 2;
			_y_points[3] = _y + _r + 2;

			_g2.fillPolygon(_x_points, _y_points, 4);

			_x_points[0] = _x - _r - 2;
			_x_points[1] = _x - _r + 2;
			_x_points[2] = _x + _r + 2;
			_x_points[3] = _x + _r - 2;

			_y_points[0] = _y + _r - 2;
			_y_points[1] = _y + _r + 2;
			_y_points[2] = _y - _r + 2;
			_y_points[3] = _y - _r - 2;
			_g2.fillPolygon(_x_points, _y_points, 4);

			return;
		}

		if (drawingTopBar == false) {
			_g2.setTransform(_af_translate_zoom);
			if (_graphic != null) {
				_g2.drawImage(_graphic[TickCount/2 % animSteps], __x - _r, __y - _r - _y_offset, null);
			}
		} else { //Drawing top bar
			if (_graphic != null) {
				_g2.drawImage(_graphic[TickCount/2 % animSteps], _x - _r, _y - _r - _y_offset, null);
			}
		}
	}
	
	
	public boolean TryPlaceItem(BuildingType _bt, Graphics2D _g2,
			AffineTransform _af, AffineTransform _af_translate_zoom, AffineTransform _af_shear_rotate, AffineTransform _af_none,
			int _mouse_x, int _mouse_y, boolean _place_remove) {
		boolean closeToExisting = false;
		_g2.setColor(Color.white);
		_g2.setTransform(_af);
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
					_g2.drawArc(_b.GetX() - utility.building_gather_radius, _b.GetY() - utility.building_gather_radius,
							utility.building_gather_radius * 2, utility.building_gather_radius * 2, angle, utility.building_Place_degrees_show);
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
				SpecialRender(_g2, _af, _af_translate_zoom, _af_shear_rotate, _af_none, _mouse_x, _mouse_y, theBitmaps.woodshop_player, false);
			} else if (_bt == BuildingType.Rockery) {
				SpecialRender(_g2, _af, _af_translate_zoom, _af_shear_rotate, _af_none, _mouse_x, _mouse_y, theBitmaps.rockery_player, false);
			} else if (_bt == BuildingType.Smelter) {
				SpecialRender(_g2, _af, _af_translate_zoom, _af_shear_rotate, _af_none, _mouse_x, _mouse_y, theBitmaps.smelter_player, false);
			} else if (_bt == BuildingType.AttractorPaper) {
				SpecialRender(_g2, _af, _af_translate_zoom, _af_shear_rotate, _af_none, _mouse_x, _mouse_y, theBitmaps.attractor_paper_player, false);
			} else if (_bt == BuildingType.AttractorRock) {
				SpecialRender(_g2, _af, _af_translate_zoom, _af_shear_rotate, _af_none, _mouse_x, _mouse_y, theBitmaps.attractor_rock_player, false);
			} else if (_bt == BuildingType.AttractorScissors) {
				SpecialRender(_g2, _af, _af_translate_zoom, _af_shear_rotate, _af_none, _mouse_x, _mouse_y, theBitmaps.attractor_scissors_player, false);
			}

			//Draw circle of collection
			if (doDistanceCheck == true) {
				_g2.setColor(Color.white);
				_g2.setTransform(_af);
				for (int angle = (TickCount%utility.building_Place_degrees); angle <= utility.wg_DegInCircle; angle += utility.building_Place_degrees) {
					_g2.drawArc(_mouse_x - utility.building_gather_radius, _mouse_y - utility.building_gather_radius,
							utility.building_gather_radius * 2, utility.building_gather_radius * 2, angle, utility.building_Place_degrees_show);
				}
			}
		} else {
			//Not safe, show X
			SpecialRender(_g2, _af, _af_translate_zoom, _af_shear_rotate, _af_none, _mouse_x, _mouse_y, theBitmaps.X, false);
		}
		return false;
	}


}