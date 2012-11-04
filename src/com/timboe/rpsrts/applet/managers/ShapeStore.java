package com.timboe.rpsrts.applet.managers;

import java.awt.geom.GeneralPath;

import com.timboe.rpsrts.managers.Utility;

public class ShapeStore {
	private static ShapeStore singleton = new ShapeStore();
	public static ShapeStore GetShapeStore() {
		return singleton;
	}
	
	private Utility utility = Utility.GetUtility();
	
	private GeneralPath cross;
	private GeneralPath move;
	private GeneralPath waterfall;
	
	private ShapeStore() {
		
		//waterfall
		final int _r = utility.waterfall_splash_radius;
		final int _x = 0 - _r;
		waterfall = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
    	waterfall.moveTo(_x, 0);
    	waterfall.curveTo(	_x + (0.75f * _r), _r,
    						_x + (1.25f * _r), _r,
    						_x + (2.00f * _r), 0);
    	waterfall.curveTo(	_x + (1.25f * _r), (2f * _r),
    						_x + (0.75f * _r), (2f * _r),
    						_x               , 0); 	
	
    	//X
		cross = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		int b_r = utility.buildingRadius;
		cross.moveTo(0 - b_r - 2, 0 - b_r + 2);
		cross.lineTo(0 - b_r + 2, 0 - b_r - 2);
		////
		cross.lineTo( 0, -4);
		////
		cross.lineTo(0 + b_r - 2, 0 - b_r - 2);
		cross.lineTo(0 + b_r + 2, 0 - b_r + 2);
		//
		cross.lineTo( 4,  0);
		//
		cross.lineTo(0 + b_r + 2, 0 + b_r - 2);		
		cross.lineTo(0 + b_r - 2, 0 + b_r + 2);
		//
		cross.lineTo( 0,  4);
		//
		cross.lineTo(0 - b_r + 2, 0 + b_r + 2);
		cross.lineTo(0 - b_r - 2, 0 + b_r - 2);
		//
		cross.lineTo(-4, 0);
		

		move = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		move.moveTo(0 - b_r - 2, 0 - b_r + 2);
		////////////
		move.lineTo(0 - b_r - 5, 0 - b_r + 5);
	    move.lineTo(0 - b_r - 3, 0 - b_r - 3);
		move.lineTo(0 - b_r + 5, 0 - b_r - 5);
		////////////
		move.lineTo(0 - b_r + 2, 0 - b_r - 2);
		////
		move.lineTo( 0, -4);
		////
		move.lineTo(0 + b_r - 2, 0 - b_r - 2);
		////////////
		move.lineTo(0 + b_r - 5, 0 - b_r - 5);
	    move.lineTo(0 + b_r + 3, 0 - b_r - 3);
		move.lineTo(0 + b_r + 5, 0 - b_r + 5);
		////////////
		move.lineTo(0 + b_r + 2, 0 - b_r + 2);
		//
		move.lineTo( 4,  0);
		//
		move.lineTo(0 + b_r + 2, 0 + b_r - 2);	
		////////////
		move.lineTo(0 + b_r + 5, 0 + b_r - 5);
	    move.lineTo(0 + b_r + 5, 0 + b_r + 3);
		move.lineTo(0 + b_r - 5, 0 + b_r + 5);
		////////////
		move.lineTo(0 + b_r - 2, 0 + b_r + 2);
		//
		move.lineTo( 0,  4);
		//
		move.lineTo(0 - b_r + 2, 0 + b_r + 2);
		////////////
		move.lineTo(0 - b_r + 5, 0 + b_r + 5);
	    move.lineTo(0 - b_r - 3, 0 + b_r + 3);
		move.lineTo(0 - b_r - 5, 0 + b_r - 5);
		////////////
		move.lineTo(0 - b_r - 2, 0 + b_r - 2);
		//
		move.lineTo(-4, 0);


	}
	
	public GeneralPath GetWaterfall() {
		return (GeneralPath) waterfall.clone();
	}
	
	public GeneralPath GetCross() {
		return (GeneralPath) cross.clone();
	}
	
	public GeneralPath GetMove() {
		return (GeneralPath) move.clone();
	}
		
}
