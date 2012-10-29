package com.timboeWeb.rpsrts;

import java.awt.geom.AffineTransform;

import com.timboe.rpsrts.Utility;

public class TransformStore {
	private static TransformStore singleton = new TransformStore();
	public static TransformStore GetTransformStore() {
		return singleton;
	}
	
	Utility utility = Utility.GetUtility();

	
	private TransformStore() {
		
	}
	
    public AffineTransform af_none = null;
    public AffineTransform af_backing = null;
    public AffineTransform af = null;
    public AffineTransform af_translate_zoom = null;
    public AffineTransform af_shear_rotate = null;
    
	//Display parameters
	public float ZOOM=0.7f;
	public float YSHEAR=0.5f;
	public float ROTATE = 0f;
	public float TRANSLATE_X = 0f;
	public float TRANSLATE_Y = 0f;
	public float TOP_ROTATE;
	
	public void updateTransforms() {
		af.translate(((utility.window_X+TRANSLATE_X)/2),((utility.window_Y+TRANSLATE_Y)/2)); //*(1./ZOOM)
		af.scale(1.5*ZOOM, 1.5*ZOOM);
		af.scale(1, YSHEAR);
		af.rotate(-ROTATE);
	    
		int halfWorldSize = (utility.world_tiles * utility.tiles_size)/2;
		af_backing.translate(-halfWorldSize + (utility.window_X/2)+TRANSLATE_X,-halfWorldSize + (utility.window_Y/2)+TRANSLATE_Y );
		af_backing.scale(ZOOM, ZOOM*YSHEAR);
		af_backing.rotate(ROTATE, halfWorldSize, halfWorldSize);

		af_translate_zoom.translate(((utility.window_X+TRANSLATE_X)/2),((utility.window_Y+TRANSLATE_Y)/2)); //*(1./ZOOM)
		af_translate_zoom.scale(1.5*ZOOM, 1.5*ZOOM);

		af_shear_rotate.scale(1, YSHEAR);
		af_shear_rotate.rotate(-ROTATE);

	}

}
