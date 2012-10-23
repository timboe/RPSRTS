package com.timboeWeb.rpsrts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import com.timboe.rpsrts.Actor;
import com.timboe.rpsrts.ActorType;
import com.timboe.rpsrts.GameWorld;
import com.timboe.rpsrts.ObjectOwner;
import com.timboe.rpsrts.WorldPoint;

public class Actor_Applet extends Actor {
	
	BufferedImage[] spriteGraphic;	
	Bitmaps_Applet theBitmaps;
	RescaleOp colourChange;

	public Actor_Applet(int _ID, int _x, int _y, int _r, GameWorld _gw, Bitmaps_Applet _bm,
			SpriteManager_Applet _sm, ActorType _at, ObjectOwner _oo) {
		super(_ID, _x, _y, _r, _gw, _sm, _at, _oo);
		
        colourChange = new RescaleOp(0.51f,1f,null);

		theBitmaps = _bm;
		
		if (type == ActorType.Paper) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = _bm.paper_player;
			} else {
				spriteGraphic = _bm.paper_enemy;
			}
		} else if (type == ActorType.Rock) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = _bm.rock_player;
			} else {
				spriteGraphic = _bm.rock_enemy;
			}
		} else if (type == ActorType.Scissors) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = _bm.scissor_player;
			} else {
				spriteGraphic = _bm.scissor_enemy;
			}

		}
	}
		
	public void Render(Graphics2D _g2, AffineTransform _af, AffineTransform _af_translate_zoom, AffineTransform _af_shear_rotate, AffineTransform _af_none, int _tick_count) {
		if (dead == true) return;
		Point2D transform = null;
		transform = _af_shear_rotate.transform(new Point(x, y), transform);
		final int _x = (int)Math.round(transform.getX());
		final int _y = (int)Math.round(transform.getY());

		BufferedImage toDraw = spriteGraphic[animStep % animSteps];
		//if (false) recolourImage(spriteGraphic[animStep % animSteps],255,255,255);
		//else toDraw = spriteGraphic[animStep % animSteps];
		
		if (flashTicks > 0) --flashTicks;
		
		_g2.setTransform(_af_translate_zoom);
		_g2.drawImage(toDraw, _x - r, _y - r, null);

		//Do carry capacity
		if (carryAmount > 0) {
			_g2.setColor(Color.black);
			_g2.fillRect(_x - r, _y - r - 3, r * 2, 1);
			_g2.setColor(Color.green);
			_g2.fillRect(_x - r, _y - r - 3, (int) Math.round(r * 2 * ((float)carryAmount/(float)strength) ), 1);
		}

		//Do health
		_g2.setColor(Color.black);
		_g2.fillRect(_x - r, _y - r - 2, r * 2, 1);
		if (owner == ObjectOwner.Player) {
			_g2.setColor(Color.red);
		} else {
			_g2.setColor(Color.blue);
		}
		_g2.fillRect(_x - r, _y - r - 2, (int) Math.round(r * 2 * ((float)health/(float)maxHealth) ), 1);

		_g2.setTransform(_af);

		if (theSpriteManager.utility.dbg == true && wander != null) {
			_g2.setColor(Color.yellow);
			_g2.drawLine(x, y, wander.getX(), wander.getY());
		}

		if (theSpriteManager.utility.dbg == true && attack_target != null) {
			int _x_off = 0;
			int _y_off = 0;
			if (owner == ObjectOwner.Player) {
				_g2.setColor(Color.white);
				_x_off -= 3;
				_y_off -= 3;
			} else {
				_g2.setColor(Color.black);
				_x_off += 3;
				_y_off += 3;
			}
			_g2.drawLine(x + _x_off, y + _y_off, attack_target.GetX() + _x_off, attack_target.GetY() + _y_off);
		}

		if (owner == ObjectOwner.Player) {
			_g2.setColor(Color.red);
		} else {
			_g2.setColor(Color.blue);
		}

		if (theSpriteManager.utility.dbg == true && waypoint_list != null) {
			for (final WorldPoint p : waypoint_list) {
				if (p == null) {
					continue;
				}
				_g2.fillRect((int) p.getX()-1, (int) p.getY()-1, 2, 2);
			}
		}
		if (theSpriteManager.utility.dbg == true && waypoint != null) {
			_g2.fillRect((int) waypoint.getX()-1, (int) waypoint.getY()-1, 3, 3);
		}
		if (theSpriteManager.utility.dbg == true && destination != null) {
			_g2.drawOval(destination.GetX()-4, destination.GetY()-4, 8, 8);
		}
		
		_g2.setColor(Color.white);
		_g2.setTransform(_af);
		_g2.fillOval(x - 1, y - 1 + r, 2, 2);
	}
	
    public static void recolourImage(BufferedImage img, int r, int g, int b) {
        for(int x=0;x<img.getWidth();x++){
            for(int y=0;y<img.getHeight();y++){
                int px = img.getRGB(x, y);
                int alpha = (px >> 24) & 0xFF;
                int red = (px >> 16) & 0xFF;
                int green = (px >> 8) & 0xFF;
                int blue = px & 0xFF;
                int pixel = (alpha<<24) + (Math.max(Math.min((red+r),255),0)<<16) + (Math.max(Math.min((green+g),255),0)<<8) + Math.max(Math.min((blue+b),255),0);
                img.setRGB(x, y, pixel);
            }
        }
    }
}
