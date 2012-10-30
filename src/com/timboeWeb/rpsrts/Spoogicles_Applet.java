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
			if (isDead.get(s).get()  == true) continue;
			
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

			spooge_y_vel.get(s).mod(0.4f * scale);

			x.get(s).mod( spooge_x_vel.get(s).floatValue() );
			y.get(s).mod( spooge_y_vel.get(s).floatValue() );

			//check height
			if (Y + y.get(s).floatValue() > floorLevel) {
				if (utility.rnd() < 0.75f) {
					isDead.get(s).set(true);
					++nDead;
				}
				y.get(s).set( -Math.abs(spooge_y_vel.get(s).floatValue()) );
			}
		}	
	}
}
