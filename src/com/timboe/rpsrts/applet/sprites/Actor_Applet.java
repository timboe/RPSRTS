package com.timboe.rpsrts.applet.sprites;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import com.timboe.rpsrts.applet.managers.Bitmaps_Applet;
import com.timboe.rpsrts.applet.managers.TransformStore;
import com.timboe.rpsrts.enumerators.ActorType;
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.sprites.Actor;
import com.timboe.rpsrts.sprites.Building;
import com.timboe.rpsrts.world.WorldPoint;

public class Actor_Applet extends Actor {
	
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
	BufferedImage[] spriteGraphic;
	protected Bitmaps_Applet theBitmaps = Bitmaps_Applet.GetBitmaps_Applet();
	protected TransformStore theTransforms = TransformStore.GetTransformStore();

	RescaleOp colourChange;
		
	public Actor_Applet(int _ID, int _x, int _y, int _r, ActorType _at, ObjectOwner _oo) {
		super(_ID, _x, _y, _r, _at, _oo);
		
        colourChange = new RescaleOp(0.51f,1f,null);
		
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
		} else if (type == ActorType.Spock) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = theBitmaps.spock_player;
			} else {
				spriteGraphic = theBitmaps.spock_enemy;
			}	
		} else if (type == ActorType.Lizard) {
			if (_oo == ObjectOwner.Player) {
				spriteGraphic = theBitmaps.lizard_player;
			} else {
				spriteGraphic = theBitmaps.lizard_enemy;
			}	
		}
	}
	
    public synchronized void Render(Graphics2D _g2, int _frame_count) {
		if (dead == true) return;
		Point2D transform = theTransforms.getTransformedPoint(x, y);
		final int _x = (int)transform.getX();
		final int _y = (int)transform.getY();

		if (utility.dbg == true) { //DBG
			_g2.setColor(Color.red);
			_g2.setTransform(theTransforms.af);
			_g2.fillOval(x - r, y - r, 2*r, 2*r);
		}
		
		if (flashTicks > 0) --flashTicks;
		
		_g2.setTransform(theTransforms.af_translate_zoom);
		
		int health_offset = 5;
		if (GetType() == ActorType.Spock) {
			_g2.drawImage(spriteGraphic[animStep % animSteps], _x - r + 1, _y - (4*r) - 3, null);
			health_offset = 14;
		} else if (GetType() == ActorType.Lizard) {
			_g2.setColor(Color.green);
			_g2.fillOval(_x - (2*r), _y - r, r * 4, r * 2);
			_g2.drawImage(spriteGraphic[animStep % animSteps], _x - (2*r) - 2, _y - r - 3, null);
		} else {
			_g2.drawImage(spriteGraphic[animStep % animSteps], _x - r, _y - r - 3, null);
		}
		
		//Do carry capacity
		if (carryAmount > 0) {
			_g2.setColor(Color.black);
			_g2.fillRect(_x - r, _y - r - health_offset - 1, r * 2, 1);
			_g2.setColor(Color.green);
			_g2.fillRect(_x - r, _y - r - health_offset - 1, (int) Math.round(r * 2 * ((float)carryAmount/(float)strength) ), 1);
		}

		//Do health
		_g2.setColor(Color.black);
		_g2.fillRect(_x - r, _y - r - health_offset, r * 2, 1);
		if (owner == ObjectOwner.Player) {
			_g2.setColor(Color.red);
		} else {
			_g2.setColor(Color.blue);
		}
		_g2.fillRect(_x - r, _y - r - health_offset, (int) Math.round(r * 2 * ((float)health/(float)maxHealth) ), 1);
		
		//Do poison
		if (poisoned > 0) {
			_g2.setColor(Color.cyan);
			_g2.fillOval(_x - r, _y - r, 2*r, 2);
		}
		
		if (utility.dbg == true) {
			_g2.setTransform(theTransforms.af);
			if (wander != null) {
				_g2.setColor(Color.yellow);
				_g2.drawLine(x, y, wander.getX(), wander.getY());
			}
			if (attack_target != null) {
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
			if (waypoint_list_sync != null) {
				for (final WorldPoint p : waypoint_list_sync) {
					if (p == null) {
						continue;
					}
					_g2.fillRect((int) p.getX()-1, (int) p.getY()-1, 2, 2);
				}
			}
			if (waypoint != null) {
				_g2.fillRect((int) waypoint.getX()-1, (int) waypoint.getY()-1, 3, 3);
			}
			if (destination != null) {
				_g2.drawOval(destination.GetX()-4, destination.GetY()-4, 8, 8);
			}
			if (boss != null) {
				_g2.setColor(Color.magenta);
				_g2.drawLine(x, y, boss.GetX(), boss.GetY());
			}
			for (Building _b : previous_bad_employers) {
				_g2.setColor(Color.cyan);
				_g2.drawLine(x, y, _b.GetX(), _b.GetY());
			}
			_g2.setColor(Color.blue);
			_g2.setTransform(theTransforms.af);
			_g2.fillOval(x - 1, y - 1, 2, 2);
		}			
		
		if (true) { //DBG

		}
	}
}
