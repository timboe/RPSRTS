package com.timboe.rpsrts.applet.sprites;

import java.awt.Graphics2D;

import com.timboe.rpsrts.sprites.Sprite;

public class Sprite_Applet extends Sprite {

	public Sprite_Applet(final int _ID, final int _x, final int _y, final int _r) {
		super(_ID, _x, _y, _r);

	}

	public void Render(final Graphics2D _g2, final int _tick_count) {
		System.out.println("BAD RENDER CALLED");
		//Override
	}

}
