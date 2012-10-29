package com.timboeWeb.rpsrts;

import java.awt.Graphics2D;
import com.timboe.rpsrts.Sprite;

public class Sprite_Applet extends Sprite {
	
	public Sprite_Applet(int _ID, int _x, int _y, int _r) {
		super(_ID, _x, _y, _r);
		
	}

	public void Render(Graphics2D _g2, int _tick_count) {
		System.out.println("BAD RENDER CALLED");
		//Override
	}
	
}
