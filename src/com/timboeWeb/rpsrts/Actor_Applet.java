package com.timboeWeb.rpsrts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.awt.geom.Point2D;

import com.timboe.rpsrts.Actor;
import com.timboe.rpsrts.ActorType;
import com.timboe.rpsrts.ObjectOwner;
import com.timboe.rpsrts.WorldPoint;

public class Actor_Applet extends Actor {
	
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

		}
	}
		
	public void Render(Graphics2D _g2, int _frame_count) {
		if (dead == true) return;
		Point2D transform = theTransforms.getTransformedPoint(x, y);
		final int _x = (int)Math.round(transform.getX());
		final int _y = (int)Math.round(transform.getY());

		
		if (true) { //DBG
			_g2.setColor(Color.red);
			_g2.setTransform(theTransforms.af);
			_g2.fillOval(x - r, y - r, 2*r, 2*r);
		}
		
		if (flashTicks > 0) --flashTicks;
		
		_g2.setTransform(theTransforms.af_translate_zoom);
		BufferedImage toDraw = spriteGraphic[animStep % animSteps];
		_g2.drawImage(toDraw, _x - r, _y - r - 3, null);

		//Do carry capacity
		if (carryAmount > 0) {
			_g2.setColor(Color.black);
			_g2.fillRect(_x - r, _y - r - 6, r * 2, 1);
			_g2.setColor(Color.green);
			_g2.fillRect(_x - r, _y - r - 6, (int) Math.round(r * 2 * ((float)carryAmount/(float)strength) ), 1);
		}

		//Do health
		_g2.setColor(Color.black);
		_g2.fillRect(_x - r, _y - r - 5, r * 2, 1);
		if (owner == ObjectOwner.Player) {
			_g2.setColor(Color.red);
		} else {
			_g2.setColor(Color.blue);
		}
		_g2.fillRect(_x - r, _y - r - 5, (int) Math.round(r * 2 * ((float)health/(float)maxHealth) ), 1);
		
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
			if (waypoint_list != null) {
				for (final WorldPoint p : waypoint_list) {
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
		}			
		
		if (true) { //DBG
			_g2.setColor(Color.blue);
			_g2.setTransform(theTransforms.af);
			_g2.fillOval(x - 1, y - 1, 2, 2);
		}
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
