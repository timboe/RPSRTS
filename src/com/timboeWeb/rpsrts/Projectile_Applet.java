package com.timboeWeb.rpsrts;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import com.timboe.rpsrts.Actor;
import com.timboe.rpsrts.ActorType;
import com.timboe.rpsrts.GameWorld;
import com.timboe.rpsrts.ObjectOwner;
import com.timboe.rpsrts.Projectile;
import com.timboe.rpsrts.Sprite;
import com.timboe.rpsrts.SpriteManager;
import com.timboeWeb.rpsrts.Bitmaps_Applet;

public class Projectile_Applet extends Projectile {
	
	BufferedImage[] spriteGraphic;	
	Bitmaps_Applet theBitmaps;

	public Projectile_Applet(int _ID, Actor _source, int _r, GameWorld _gw,
			Bitmaps_Applet _bm, SpriteManager _sm, Sprite _target) {
		super(_ID, _source, _r, _gw, _sm, _target);

		if (_source.GetOwner() == ObjectOwner.Player) {
			if (_source.GetType() == ActorType.Paper) spriteGraphic = _bm.proj_paper_player;
			else if (_source.GetType() == ActorType.Rock) spriteGraphic = _bm.proj_rock_player;
			else if (_source.GetType() == ActorType.Scissors) spriteGraphic = _bm.proj_scissor_player;
		} else if (_source.GetOwner() == ObjectOwner.Enemy) {
			if (_source.GetType() == ActorType.Paper) spriteGraphic = _bm.proj_paper_enemy;
			else if (_source.GetType() == ActorType.Rock) spriteGraphic = _bm.proj_rock_enemy;
			else if (_source.GetType() == ActorType.Scissors) spriteGraphic = _bm.proj_scissor_enemy;
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
				
		_g2.setTransform(_af_translate_zoom);
		_g2.drawImage(spriteGraphic[animStep % animSteps], _x - r, _y - r, null);
	}

}
