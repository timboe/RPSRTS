package com.timboe.rpsrts.applet.sprites;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import com.timboe.rpsrts.applet.managers.ShapeStore;
import com.timboe.rpsrts.applet.managers.TransformStore;
import com.timboe.rpsrts.sprites.WaterfallSplash;

public class WaterfallSplash_Applet extends WaterfallSplash {

	TransformStore theTransforms = TransformStore.GetTransformStore();
	ShapeStore theShapeStore = ShapeStore.GetShapeStore();
	GeneralPath waterfall;

	public WaterfallSplash_Applet (final int _ID, final int _x, final int _y, final int _r) {
		super(_ID, _x, _y, _r);
	}

	public void Render(final Graphics2D _g2) {
		//Only show if in `front' (this means negative Z order value)
		if (GetZOrder() > 0) {
			return;
		}
		_g2.setTransform(theTransforms.af_translate_zoom);
		final Point2D transform = theTransforms.getTransformedPoint(x, y);
		final GeneralPath clonefall = theShapeStore.GetWaterfall();
		clonefall.transform(AffineTransform.getTranslateInstance(transform.getX(), transform.getY() + offset));
		_g2.fill(clonefall);
	}

}
