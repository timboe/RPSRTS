package com.timboeWeb.rpsrts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

import com.timboe.rpsrts.ObjectOwner;
import com.timboe.rpsrts.Spoogicles;

public class Spoogicles_Applet extends Spoogicles {
	protected TransformStore theTransforms = TransformStore.GetTransformStore();

	protected Spoogicles_Applet(int _ID, int _x, int _y, ObjectOwner _oo, int _spoogicles, float _scale) {
		super(_ID, _x, _y, _oo, _spoogicles, _scale);
	}
	
	public void Render(Graphics2D _g2, int _tick_count) {

		if (nDead == nSpooge) {
			Kill();
			return;
		}
		
		for (int s = 0; s < nSpooge; ++s) {
			if (isDead.get(s).booleanValue()  == true) continue;
			float tempF = 0;
			
			Point2D transform = null;
			final int X = Math.round(x_start.get(s).floatValue());
			final int Y = Math.round(y_start.get(s).floatValue());
			transform = theTransforms.af_shear_rotate.transform(new Point(X,Y), transform);
			final int _x = (int)Math.round(transform.getX());
			final int _y = (int)Math.round(transform.getY());
			
			//draw
			if (s % 2 == 0) {
				_g2.setColor(Color.white);
			} else {
				if (oo == ObjectOwner.Player) _g2.setColor(Color.red);
				else _g2.setColor(Color.blue);
			}
			_g2.setTransform(theTransforms.af_translate_zoom);
			_g2.fillRect((int) (_x + x.get(s).floatValue()), (int) (_y + y.get(s).floatValue()), 1, 1);
			
			tempF = spooge_y_vel.get(s).floatValue();
			spooge_y_vel.remove(s);
			spooge_y_vel.add( new Float( tempF + (0.4f * scale) ) );
			
			tempF = x.get(s).floatValue();
			x.remove(s);
			x.add( new Float( tempF + spooge_x_vel.get(s).floatValue() ));
			tempF = y.get(s).floatValue();
			y.remove(s);
			y.add( new Float( tempF + spooge_y_vel.get(s).floatValue() ));
			
			//check height
			if (Y + y.get(s).floatValue() > floorLevel) {
				if (utility.rnd() < 0.75f) {
					isDead.remove(s);
					isDead.add(new Boolean(true) );
					++nDead;
				}
				//spooge_y_vel.get(s).floatValue() = );
				
				tempF = spooge_y_vel.get(s).floatValue();
				spooge_y_vel.remove(s);
				spooge_y_vel.add( new Float( -Math.abs(tempF) ) );
				
			}
			
		}
		
		
		
	}

}
