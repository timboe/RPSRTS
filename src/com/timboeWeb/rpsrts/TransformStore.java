package com.timboeWeb.rpsrts;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import com.timboe.rpsrts.Utility;

public class TransformStore {
	private static TransformStore singleton = new TransformStore();
	public static TransformStore GetTransformStore() {
		return singleton;
	}
	
	Utility utility = Utility.GetUtility();

	
	private TransformStore() {
		System.out.println("--- Transform Manager spawned (depends on Util) : "+this);
	    aa_on.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_SPEED);
	}
	
    RenderingHints aa_on = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
    RenderingHints aa_off = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
    
    private boolean aa = true;
    private boolean disable_aa = false;
	
    public AffineTransform af_none = new AffineTransform();
    public AffineTransform af_backing = new AffineTransform();
    public AffineTransform af = new AffineTransform();
    public AffineTransform af_translate_zoom = new AffineTransform();
    public AffineTransform af_shear_rotate = new AffineTransform();
    
	//Display parameters
	private float ZOOM=0.9f;
	private float YSHEAR=0.6f;
	private float ROTATE = 0f;
	private float TRANSLATE_X = 0f;
	private float TRANSLATE_Y = 0f;
	public float TOP_ROTATE; //keep this here 
	
	private final float YSHEAR_MIN = 0.15f;
	private final float YSHEAR_MAX = 1f;//0.85f;
	private final float TRANSLATE_X_MAX = 0.0165f;
	private final float TRANSLATE_Y_MAX = 0.01f;

	private boolean updateNeeded = true;
	
	public void updateTransforms() {
		if (updateNeeded == false) return;
		//System.out.println("TRANSLATE_Y : "+TRANSLATE_Y);
				
		final float trans_X = (utility.window_X+TRANSLATE_X)/2f;
		final float trans_Y = (utility.window_Y+TRANSLATE_Y)/2f;
		
		//First half
		af_translate_zoom.setToIdentity();
		af_translate_zoom.translate(trans_X + (TRANSLATE_X * ZOOM * 100000f), trans_Y + (TRANSLATE_Y * ZOOM * 100000f));
		af_translate_zoom.scale(ZOOM, ZOOM);
		
		//System.out.println("TRANS X:"+TRANSLATE_X+" trans_X:"+trans_X+" TRANS*big:"+(TRANSLATE_X * ZOOM * 100000f)+" totalX:"+ (trans_X + (TRANSLATE_X * ZOOM * 100000f)));

		//Second half
		af_shear_rotate.setToIdentity();
		af_shear_rotate.scale(1, YSHEAR);
		af_shear_rotate.rotate(ROTATE);
		
		//Both halves
		af.setTransform(af_translate_zoom);
		af.scale(1, YSHEAR);
		af.rotate(ROTATE);
	    
		af_backing.setTransform(af);
		af_backing.translate(-utility.world_size2,-utility.world_size2);

		updateNeeded = false;
	}
	
	public float GetZoom() {
		return ZOOM;
	}
	
	public void SetZoom(float _z) {
		ZOOM = _z;
	}

	public void modifyTranslate(float _mod_x, float _mod_y) {
		updateNeeded = true;
		TRANSLATE_X += _mod_x/(100000f * ZOOM);
		TRANSLATE_Y += _mod_y/(100000f * ZOOM);
		if (TRANSLATE_X > TRANSLATE_X_MAX) {
			TRANSLATE_X = TRANSLATE_X_MAX;
		} else if (TRANSLATE_X < -TRANSLATE_X_MAX) {
			TRANSLATE_X = -TRANSLATE_X_MAX;
		}
		if (TRANSLATE_Y > TRANSLATE_Y_MAX) {
			TRANSLATE_Y = TRANSLATE_Y_MAX;
		} else if (TRANSLATE_Y < -TRANSLATE_Y_MAX) {
			TRANSLATE_Y = -TRANSLATE_Y_MAX;
		}
	}
	
	public void modifyShear(float _mod) {
		updateNeeded = true;
		YSHEAR += _mod;
		if (YSHEAR > YSHEAR_MAX) {
			YSHEAR = YSHEAR_MAX;
		}
		if (YSHEAR < YSHEAR_MIN) {
			YSHEAR = YSHEAR_MIN;
		}
	}
	
	public void modifyRotate(float _mod) {
		updateNeeded = true;
		ROTATE -= _mod / ZOOM;
		utility.rotateAngle = ROTATE; //Coppied for Z ordering sake
	}
	
	public void zoomIn(boolean slow) {
		updateNeeded = true;
		if (slow == true) {
			ZOOM = ZOOM * 1.01f;
		} else {
			ZOOM = ZOOM * 1.20f;
		}
		if (ZOOM >= 10.f) {
			ZOOM = 10.f;
		}
	}
	
	public void zoomOut(boolean slow) {
		updateNeeded = true;
		if (slow == true) {
			 ZOOM = ZOOM * 0.99f;
		} else {
			 ZOOM = ZOOM * 0.8f;
		}
		if (ZOOM <= 0.3f) {
			ZOOM = 0.3f;
		}
	}
	
	public void SetAA(Graphics2D _g2, boolean _on) {
    	if (disable_aa == true) {
    		_g2.setRenderingHints(aa_off);
    		aa = false;
    		return;
    	}
    	if (_on == true && aa == false) {
    		_g2.setRenderingHints(aa_on);
    	} else if (_on == false && aa == true) {
    		_g2.setRenderingHints(aa_off);
    	}
    	aa = _on;
    }
	
	public void toggleAA() {
		disable_aa = !disable_aa;
	}
	
	public boolean GetAA() {
    	return aa;
    }
	
	public float getShear() {
		return YSHEAR;
	}
	
	public float getShearPercentage() {
		return (YSHEAR_MAX - YSHEAR) * (1f/(1f-YSHEAR_MIN));
	}
	
	public float getRotate() {
		return ROTATE;
	}
}
