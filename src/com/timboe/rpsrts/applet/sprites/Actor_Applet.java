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

	public static void recolourImage(final BufferedImage img, final int r, final int g, final int b) {
        for(int x=0;x<img.getWidth();x++){
            for(int y=0;y<img.getHeight();y++){
                final int px = img.getRGB(x, y);
                final int alpha = (px >> 24) & 0xFF;
                final int red = (px >> 16) & 0xFF;
                final int green = (px >> 8) & 0xFF;
                final int blue = px & 0xFF;
                final int pixel = (alpha<<24) + (Math.max(Math.min((red+r),255),0)<<16) + (Math.max(Math.min((green+g),255),0)<<8) + Math.max(Math.min((blue+b),255),0);
                img.setRGB(x, y, pixel);
            }
        }
    }
	BufferedImage[] spriteGraphic;
	protected Bitmaps_Applet theBitmaps = Bitmaps_Applet.GetBitmaps_Applet();
	protected TransformStore theTransforms = TransformStore.GetTransformStore();

	RescaleOp colourChange;

	public Actor_Applet(final int _ID, final int _x, final int _y, final int _r, final ActorType _at, final ObjectOwner _oo) {
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

    public synchronized void Render(final Graphics2D _g2, final int _frame_count) {
		if (dead == true) return;
		final Point2D transform = theTransforms.getTransformedPoint(x, y);
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
			int anim_offset = 0;
			if (GetFacingWest() == false) {
				anim_offset = 4;
			}
			_g2.drawImage(spriteGraphic[(animStep % animSteps) + anim_offset], _x - (2*r) - 2, _y - r - 3, null);
		} else {
			_g2.drawImage(spriteGraphic[animStep % animSteps], _x - r, _y - r - 3, null);
		}

		//Do carry capacity
		if (carryAmount > 0) {
			_g2.setColor(Color.black);
			_g2.fillRect(_x - r, _y - r - health_offset - 1, r * 2, 1);
			_g2.setColor(Color.green);
			_g2.fillRect(_x - r, _y - r - health_offset - 1, Math.round(r * 2 * ((float)carryAmount/(float)strength) ), 1);
		}

		//Do health
		_g2.setColor(Color.black);
		_g2.fillRect(_x - r, _y - r - health_offset, r * 2, 1);
		if (owner == ObjectOwner.Player) {
			_g2.setColor(Color.red);
		} else {
			_g2.setColor(Color.blue);
		}
		_g2.fillRect(_x - r, _y - r - health_offset, Math.round(r * 2 * (health/maxHealth) ), 1);

		//Do poison
		if (poisoned > 0) {
			if (GetOwner() == ObjectOwner.Player) {
				_g2.drawImage(theBitmaps.proj_lizard_enemy[animStep % 3], _x - r, _y - r - health_offset - 4, null);
			} else {
				_g2.drawImage(theBitmaps.proj_lizard_player[animStep % 3], _x - r, _y - r - health_offset - 4, null);
			}
		}

		if (utility.dbg == true) {
			//_g2.drawString(job.toString(), _x, _y);
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
			if (waypoint_list != null) {
				for (final WorldPoint p : waypoint_list) {
					if (p == null) {
						continue;
					}
					_g2.fillRect(p.getX()-1, p.getY()-1, 2, 2);
				}
			}
			if (waypoint != null) {
				_g2.fillRect(waypoint.getX()-1, waypoint.getY()-1, 3, 3);
			}
			if (destination != null) {
				_g2.drawOval(destination.GetX()-4, destination.GetY()-4, 8, 8);
			}
			if (boss != null) {
				_g2.setColor(Color.magenta);
				_g2.drawLine(x, y, boss.GetX(), boss.GetY());
			}
			for (final Building _b : previous_bad_employers) {
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
