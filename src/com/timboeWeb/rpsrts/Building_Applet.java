package com.timboeWeb.rpsrts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import com.timboe.rpsrts.Building;
import com.timboe.rpsrts.BuildingType;
import com.timboe.rpsrts.GameWorld;
import com.timboe.rpsrts.ObjectOwner;
import com.timboe.rpsrts.SpriteManager;

public class Building_Applet extends Building {

	BufferedImage[] spriteGraphic;	
	Bitmaps_Applet theBitmaps;
	
	public Building_Applet(int _ID, int _x, int _y, int _r, GameWorld _gw,
			Bitmaps_Applet _bm, SpriteManager _sm, BuildingType _bt, ObjectOwner _oo) {
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
	
	public void Render(Graphics2D _g2, AffineTransform _af, AffineTransform _af_translate_zoom, AffineTransform _af_shear_rotate, AffineTransform _af_none, int _tick_count) {
		if (dead == true) return;
		if (_tick_count % 2 == 0) {
			++animStep;
		}

		Point2D transform = null;
		transform = _af_shear_rotate.transform(new Point(x, y), transform);
		final int _x = (int)Math.round(transform.getX());
		final int _y = (int)Math.round(transform.getY());

		if (delete_hover) {
			//Can't place here
			_g2.setTransform(_af);
			_g2.setColor(Color.red);
			final int[] _x_points = new int[4];
			final int[] _y_points = new int[4];
			final int b_r = utility.buildingRadius;
			_x_points[0] = x - b_r - 2;
			_x_points[1] = x - b_r + 2;
			_x_points[2] = x + b_r + 2;
			_x_points[3] = x + b_r - 2;

			_y_points[0] = y - b_r + 2 + y_offset/2;
			_y_points[1] = y - b_r - 2 + y_offset/2;
			_y_points[2] = y + b_r - 2 + y_offset/2;
			_y_points[3] = y + b_r + 2 + y_offset/2;


			_g2.fillPolygon(_x_points, _y_points, 4);

			_x_points[0] = x - b_r - 2;
			_x_points[1] = x - b_r + 2;
			_x_points[2] = x + b_r + 2;
			_x_points[3] = x + b_r - 2;

			_y_points[0] = y + b_r - 2 + y_offset/2;
			_y_points[1] = y + b_r + 2 + y_offset/2;
			_y_points[2] = y - b_r + 2 + y_offset/2;
			_y_points[3] = y - b_r - 2 + y_offset/2;
			_g2.fillPolygon(_x_points, _y_points, 4);

			delete_hover = false;
			return;
		}

		//Do health
		if (health < maxHealth) {
			_g2.setTransform(_af_translate_zoom);
			_g2.setColor(Color.black);
			_g2.fillRect(_x - r, _y - r - 4 - y_offset, r * 2, 1);
			if (owner == ObjectOwner.Player) {
				_g2.setColor(Color.red);
			} else {
				_g2.setColor(Color.blue);
			}
			_g2.fillRect(_x - r, _y - r - 4 - y_offset, (int) Math.round(r * 2 * ((float)health/(float)maxHealth) ), 1);
		}

		if (utility.dbg == true) {
			_g2.setTransform(_af_translate_zoom);
			_g2.setColor(Color.red);
			_g2.drawString(Integer.toString(GetEmployees()), _x - r, _y - r - 10);
		}
		
		if (underConstruction == true) {
			int conStep = 0;
			final float healthFrac = (float)health/(float)maxHealth;
			if (healthFrac <= 0.25) {
				conStep = 0;
			} else if (healthFrac <= 0.5) {
				conStep = 1;
			} else if (healthFrac <= 0.75) {
				conStep = 2;
			} else {
				conStep = 3;
			}
			_g2.setTransform(_af_translate_zoom);
			if (owner == ObjectOwner.Player) {
				_g2.drawImage(theBitmaps.construction_player[conStep], _x - r, _y - r, null);
			} else {
				_g2.drawImage(theBitmaps.construction_enemy[conStep], _x - r, _y - r, null);
			}
			return;
		}

		_g2.setTransform(_af_translate_zoom);
		_g2.drawImage(spriteGraphic[animStep % animSteps], _x, _y, null);
		
		_g2.setColor(Color.blue);
		_g2.setTransform(_af);
		_g2.fillRect(x, y, 1, 1);
		

	}
//
//	public void SpecialRender(Graphics2D _g2, AffineTransform _af, AffineTransform _af_translate_zoom, AffineTransform _af_shear_rotate, AffineTransform _af_none, int _x, int _y, BufferedImage[] _graphic) {
//		//if (true) return;
//
//	}

}
