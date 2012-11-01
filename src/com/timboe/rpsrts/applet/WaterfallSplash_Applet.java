package com.timboe.rpsrts.applet;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import com.timboe.rpsrts.sprites.WaterfallSplash;

public class WaterfallSplash_Applet extends WaterfallSplash {

	TransformStore theTransforms = TransformStore.GetTransformStore();
	ShapeStore theShapeStore = ShapeStore.GetShapeStore();
	GeneralPath waterfall;

	public WaterfallSplash_Applet (int _ID, int _x, int _y, int _r) {
		super(_ID, _x, _y, _r);
	}
	
	public void Render(Graphics2D _g2) {
		//Only show if in `front' (this means negative Z order value)
		if (GetZOrder() > 0) {
			return;
		}
		_g2.setTransform(theTransforms.af_translate_zoom);
		Point2D transform = theTransforms.getTransformedPoint(x, y);
		GeneralPath clonefall = (GeneralPath) theShapeStore.GetWaterfall();
		clonefall.transform(AffineTransform.getTranslateInstance(transform.getX(), transform.getY() + offset));
		_g2.fill(clonefall);		
	}
	
}
