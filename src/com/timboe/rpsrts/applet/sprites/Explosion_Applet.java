package com.timboe.rpsrts.applet.sprites;

import java.awt.Color;
import java.awt.Graphics2D;

import com.timboe.rpsrts.applet.managers.TransformStore;
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.sprites.Explosion;

public class Explosion_Applet extends Explosion {

	private TransformStore theTransforms = TransformStore.GetTransformStore();
	
	public Explosion_Applet(int _ID, int _x, int _y, int _r, ObjectOwner _oo) {
		super(_ID, _x, _y, _r, _oo);
	}
	
	public synchronized void Render(Graphics2D _g2, int _tick_count) {
		Color _c;
		if (owner == ObjectOwner.Player) {
			_c = Color.red;
		} else {
			_c = Color.blue;
		}
		_g2.setTransform(theTransforms.af);
		for (int s = 0; s < shells.size(); ++s) {
			int _r = shells.get(s).intValue();
			if (_r > 1000) continue;
			if (s % 2 == 0) {
				_g2.setColor(Color.black);
			} else {
				_g2.setColor(_c);
			}
			_g2.drawOval(x - _r, y - _r, 2 * _r, 2 * _r);
		}
		for (int c = 0; c < chunks.size(); ++c) {
			int _r = chunks.get(c).get();
			if (_r > 1000) continue;
			if (c % 2 == 0) {
				_g2.setColor(Color.black);
			} else {
				_g2.setColor(_c);
			}
			float _a = chunk_angle.get(c).floatValue();
			_g2.fillOval((int)(x + (_r * Math.cos(_a))), (int) (y + (_r * Math.sin(_a))), 2, 2);
		}
	}

}
