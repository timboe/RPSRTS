package com.timboe.rpsrts;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.timboeWeb.rpsrts.Bitmaps_Applet;
import com.timboeWeb.rpsrts.GameWorld_Applet;
import com.timboeWeb.rpsrts.SpriteManager_Applet;

public class RPSRTS extends Applet implements Runnable, MouseWheelListener, MouseMotionListener, MouseListener, KeyListener {

	/**
	 *
	 */
	private static final long serialVersionUID = -4515697164989975837L;
	float TIME_OF_FRAME = 0;
	float TIME_OF_LAST_SECOND = 0;
	int FPS_LAST_SECOND = 0;
	float TIME_TO_RENDER = 0;
	int FPS = 0;
	int FRAMES=0;
	int FRAMES_LAST_SECOND=0;
	int SLEEP = 30;

	//Top bar settings
	int con_start_x = 400;
	int x_add = 50;
	int y_height = 70;

	int last_X = -1;
	int last_Y = -1;
	Point CurMouse;
	Point2D MouseTF;
	
	int endGame = 0;

	BuildingType buildingToPlace;
	Building buildingToMove;
	int buildingStatBox;
	boolean mouseClick = false;
	boolean mouseDrag = false;
	boolean sendMouseDragPing = false;
	
	BufferedImage background_buffered;

	//Display parameters
	float ZOOM=0.7f;
	float YSHEAR=0.5f;
	float ROTATE = 0f;
	float TRANSLATE_X = 0f;
	float TRANSLATE_Y = 0f;
	float TOP_ROTATE;

	private Thread th;

	//private final Color sea_blue = new Color(65,105,225);
	private final Color dsea_blue = new Color(53,85,183);
	private final Color backing_brown = new Color(92,50,38);
	private final Color front_blue = new Color(99,189,200);
	private Image dbImage;
	private Graphics dbg;

	GameWorld_Applet theWorld;
	SpriteManager_Applet theSpriteManger;
	Bitmaps_Applet theBitmaps;
	Utility utility;
	
	public BufferedImage TopBar;
	
	//World parameters
	int window_X = 1000;
	int window_Y = 600;
	int tiles_per_chunk = 8; //Used for coarse kT algo
	int world_tiles = 176; //Number of tile elements on X
	int tiles_size = 7; //Size of tile element in pixels

    RenderingHints aa_on = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
    RenderingHints aa_off = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
    private boolean aa = true;
    private float last_zoom;
    private boolean disable_aa = true;
    AffineTransform af_none = null;
    AffineTransform af_backing = null;
    AffineTransform af = null;
    AffineTransform af_translate_zoom = null;
    AffineTransform af_shear_rotate = null;
    //AffineTransform af_translate_zoom_counter_rotate = null;
    //AffineTransform af_anti_shear_rotate = null;
    Font myFont = new Font(Font.MONOSPACED, Font.BOLD, 12);
    
//    private void DrawBufferedBackground(Graphics2D _g2) {
//    	if (background_buffered == null) {
//    		background_buffered = new BufferedImage(world_tiles*tiles_size, world_tiles*tiles_size, BufferedImage.TYPE_3BYTE_BGR);
//    		Graphics2D gc = background_buffered.createGraphics();
//    		gc.translate((world_tiles * tiles_size/2),(world_tiles * tiles_size/2));
//			theWorld.DrawTiles(gc,false,GetAA());
//			DrawBufferedBackground(_g2); //recurse to actually draw
//    	} else {
//			DrawSea(_g2);
//			_g2.setTransform(af_backing);
//			_g2.drawImage(background_buffered, null, 0, 0);
//    	}
//    }

    private void ContentCreation(Graphics2D _g2, AffineTransform _af) {
    	SetAA(_g2, false);
    	ROTATE += 0.001;
    	if (theWorld.GetWorldGenerated() == false) {
	    	final int status = theWorld.GenerateWorld();
	    	if (status == 1) { //Object made, circle island, plane grid
	    		theWorld.DrawChunks(_g2,true,GetAA());
	    		theWorld.DrawIslandEdge(_g2);
	    		TopText(_g2,"Setup Grid");
	    	} else if (status == 2) { //Island perimeter
	    		theWorld.DrawChunks(_g2,true,GetAA());
	    		theWorld.DrawIslandEdge(_g2);
	    		TopText(_g2,"Crinkle the Fjords");
	    	} else if (status == 3) { //Chunks touching and inside perimeter, random energy
	    		theWorld.DrawChunks(_g2,true,GetAA());
	    		theWorld.DrawIslandEdge(_g2);
	    		TopText(_g2,"Random Seed Chunks");
	    	} else if (status == 4) { //Doing kt
	    		theWorld.DrawChunks(_g2,true,GetAA());
	    		theWorld.DrawIslandEdge(_g2);
	    		TopText(_g2,"Apply kT Algorithm");
	    	} else if (status == 5) { //Doing kt
	    		theWorld.DrawChunks(_g2,false,GetAA());
	    		theWorld.DrawIslandEdge(_g2);
	    		TopText(_g2,"Apply kT Algorithm");
	    	} else if (status == 6) { //Doing biomes
	    		DrawSea(_g2);
	    		theWorld.DrawTiles(_g2,false,GetAA());
	    		theWorld.DrawIslandEdge(_g2);
	    		TopText(_g2,"Assign Biomes");
	    	} else { //Eroding
	    		DrawSea(_g2);
	    		theWorld.DrawTiles(_g2,false,GetAA());
	    		theWorld.DrawIslandEdge(_g2);
	    		TopText(_g2,"Erode Edges");
	    	}
    	} else {
    		final int seeding = theSpriteManger.SeedWorld();
    		if (seeding == -1) { //Tits up - start again
    			theSpriteManger.Reset();
    			theWorld.Reset();
    		}
    		DrawSea(_g2);
    		theWorld.DrawTiles(_g2,false,GetAA());
    		theWorld.DrawIslandEdge(_g2);
    		theSpriteManger.Render(_g2, af, af_translate_zoom, af_shear_rotate, af_none);
    		_g2.setTransform(af_none);
    		TopText(_g2,"Populating World");
    	}
	}
    
    @Override
	public void destroy() { }

	private void DrawSea(Graphics2D _g2) {
	    _g2.setColor(dsea_blue);
	    final float M = 1;
	    final float offset = world_tiles*tiles_size;
	    _g2.fillOval ((int)(-offset*M),(int)(-offset*M),(int)(offset*2*M),(int)(offset*2*M));
	    _g2.setColor(Color.white);
//	    _g2.fillOval ((int)(-offset*M*1.0001),(int)(-offset*M*1.0001),5,5);
	}

	private void DrawTopBar(Graphics2D _g2) {
		_g2.setTransform(af_none);
		_g2.setColor(backing_brown);
		//_g2.fillRect(0, 0, window_X, y_height);
		_g2.drawImage(TopBar, 0, 0, null);
		_g2.setColor(front_blue);

		TOP_ROTATE += 0.01;
		if(buildingToPlace != null) {
	    	if (buildingToPlace == BuildingType.Woodshop) {
				_g2.translate(con_start_x + (0 * x_add), y_height/2);
			} else if(buildingToPlace == BuildingType.Rockery) {
				_g2.translate(con_start_x + (1 * x_add), y_height/2);
			} else if(buildingToPlace == BuildingType.Smelter) {
				_g2.translate(con_start_x + (2 * x_add), y_height/2);
			} else if(buildingToPlace == BuildingType.AttractorPaper) {
				_g2.translate(con_start_x + (3 * x_add), y_height/2);
			} else if(buildingToPlace == BuildingType.AttractorRock) {
				_g2.translate(con_start_x + (4 * x_add), y_height/2);
			} else if(buildingToPlace == BuildingType.AttractorScissors) {
				_g2.translate(con_start_x + (5 * x_add), y_height/2);
			} else if(buildingToPlace == BuildingType.X) {
				_g2.translate(con_start_x + (6 * x_add), y_height/2);
			}
    		_g2.rotate(TOP_ROTATE);
			_g2.fillOval(-20, -20, 40, 40);
			_g2.fillRect(-17, -17, 34, 34);
	    	_g2.setTransform(af_none);
	    }

		_g2.drawString(theSpriteManger.resource_manager.GetResourceText(), 15, 20);
		_g2.drawString(theSpriteManger.resource_manager.GetUnitText(), 15, 40);
		_g2.drawString("SEED:"+utility.rndSeed+" FPS:"+FPS+", DELAY:" +SLEEP, 15, 60);

    	_g2.setTransform(af_none);
		_g2.translate(con_start_x + (0 * x_add), y_height/2);
		_g2.scale(2, 2);
		theSpriteManger.SpecialRender(_g2, af, af_translate_zoom, af_shear_rotate, af_none, 0, 0, theBitmaps.woodshop_player, true);
    	_g2.setTransform(af_none);
		_g2.translate(con_start_x + (1 * x_add), y_height/2);
		_g2.scale(2, 2);
		theSpriteManger.SpecialRender(_g2, af, af_translate_zoom, af_shear_rotate, af_none, 0, 0, theBitmaps.rockery_player, true);
    	_g2.setTransform(af_none);
		_g2.translate(con_start_x + (2 * x_add), y_height/2);
		_g2.scale(2, 2);
		theSpriteManger.SpecialRender(_g2, af, af_translate_zoom, af_shear_rotate, af_none, 0, 0, theBitmaps.smelter_player, true);
    	_g2.setTransform(af_none);
		_g2.translate(con_start_x + (3 * x_add), y_height/2 + 12);
		_g2.scale(2, 2);
		theSpriteManger.SpecialRender(_g2, af, af_translate_zoom, af_shear_rotate, af_none, 0, 0, theBitmaps.attractor_paper_player, true);
    	_g2.setTransform(af_none);
		_g2.translate(con_start_x + (4 * x_add), y_height/2 + 12);
		_g2.scale(2, 2);
		theSpriteManger.SpecialRender(_g2, af, af_translate_zoom, af_shear_rotate, af_none, 0, 0, theBitmaps.attractor_rock_player, true);
    	_g2.setTransform(af_none);
		_g2.translate(con_start_x + (5 * x_add), y_height/2 + 12);
		_g2.scale(2, 2);
		theSpriteManger.SpecialRender(_g2, af, af_translate_zoom, af_shear_rotate, af_none, 0, 0, theBitmaps.attractor_scissors_player, true);
		_g2.setTransform(af_none);
		_g2.translate(con_start_x + (6 * x_add), y_height/2);
		_g2.scale(2, 2);
		theSpriteManger.SpecialRender(_g2, af, af_translate_zoom, af_shear_rotate, af_none, 0, 0, theBitmaps.X, true);

    	_g2.setTransform(af_none);
		_g2.translate(con_start_x + (7 * x_add), y_height/2);
		_g2.scale(3, 3);
		theSpriteManger.SpecialRender(_g2, af, af_translate_zoom, af_shear_rotate, af_none, 0, 0, theBitmaps.paper_player, true);
		if (theSpriteManger.resource_manager.GEN_PAPER_PLAYER == true) {
			theSpriteManger.SpecialRender(_g2, af, af_translate_zoom, af_shear_rotate, af_none, 0, 0, theBitmaps.on, true);
		} else {
			theSpriteManger.SpecialRender(_g2, af, af_translate_zoom, af_shear_rotate, af_none, 0, 0, theBitmaps.off, true);
		}
    	_g2.setTransform(af_none);
		_g2.translate(con_start_x + (8 * x_add), y_height/2);
		_g2.scale(3, 3);
		theSpriteManger.SpecialRender(_g2, af, af_translate_zoom, af_shear_rotate, af_none, 0, 0, theBitmaps.rock_player, true);
		if (theSpriteManger.resource_manager.GEN_ROCK_PLAYER == true) {
			theSpriteManger.SpecialRender(_g2, af, af_translate_zoom, af_shear_rotate, af_none, 0, 0, theBitmaps.on, true);
		} else {
			theSpriteManger.SpecialRender(_g2, af, af_translate_zoom, af_shear_rotate, af_none, 0, 0, theBitmaps.off, true);
		}
    	_g2.setTransform(af_none);
		_g2.translate(con_start_x + (9 * x_add), y_height/2);
		_g2.scale(3, 3);
		theSpriteManger.SpecialRender(_g2, af, af_translate_zoom, af_shear_rotate, af_none, 0, 0, theBitmaps.scissor_player, true);
		if (theSpriteManger.resource_manager.GEN_SCISSORS_PLAYER == true) {
			theSpriteManger.SpecialRender(_g2, af, af_translate_zoom, af_shear_rotate, af_none, 0, 0, theBitmaps.on, true);
		} else {
			theSpriteManger.SpecialRender(_g2, af, af_translate_zoom, af_shear_rotate, af_none, 0, 0, theBitmaps.off, true);
		}
		
		
		
		
		if (buildingStatBox != 0) {
			_g2.setTransform(af_none);
			int _x = 0;
			int _y = 0;
			final int _os = 15;
			final int box_w = 500;
			final int box_h = 70;
	    	if(buildingStatBox == 1) {
	    		_x = (int)CurMouse.getX() - con_start_x/2 - x_add*0;
	    		_y = (int)CurMouse.getY() + x_add/2;
				_g2.setColor(backing_brown);
				_g2.fillRect(_x, _y, box_w, box_h);
				_g2.setColor(Color.white);
				_g2.drawRect(_x, _y, box_w, box_h);
				_g2.setColor(front_blue);
	    		_g2.drawString("The Woodshop costs "+utility.COST_Woodshop_Iron+" Iron.", _x + _os, _y + (_os * 1));
	    		_g2.drawString("Nearby wood is collected by Scissors and brought to the Woodshop.", _x + _os, _y + (_os * 2));
	    		_g2.drawString("The woodshop converts "+utility.COST_Paper_Wood+" wood into Paper.", _x + _os, _y + (_os * 3));
	    		_g2.drawString("Each additional Woodshop can support "+utility.EXTRA_Paper_PerWoodmill+" Paper.", _x + _os, _y + (_os * 4));
	    	} else if(buildingStatBox == 2) {
	    		_x = (int)CurMouse.getX() - con_start_x/2 - x_add*1;
	    		_y = (int)CurMouse.getY() + x_add/2;
				_g2.setColor(backing_brown);
				_g2.fillRect(_x, _y, box_w, box_h);
				_g2.setColor(Color.white);
				_g2.drawRect(_x, _y, box_w, box_h);
				_g2.setColor(front_blue);
	    		_g2.drawString("The Rockery costs "+utility.COST_Rockery_Wood+" Wood to build.",  _x + _os, _y + (_os * 1));
	    		_g2.drawString("Nearby Stone is collected by Paper and brought to the Rockery.",  _x + _os, _y + (_os * 2));
	    		_g2.drawString("The Rockery converts "+utility.COST_Rock_Stone+" Stone into Rock.",  _x + _os, _y + (_os * 3));
	    		_g2.drawString("Each additional Rockery can support "+utility.EXTRA_Rock_PerRockery+" Rocks.",  _x + _os, _y + (_os * 4));
			} else if(buildingStatBox == 3) {
	    		_x = (int)CurMouse.getX() - con_start_x/2 - x_add*2;
	    		_y = (int)CurMouse.getY() + x_add/2;
				_g2.setColor(backing_brown);
				_g2.fillRect(_x, _y, box_w, box_h);
				_g2.setColor(Color.white);
				_g2.drawRect(_x, _y, box_w, box_h);
				_g2.setColor(front_blue);
	    		_g2.drawString("The Smelter costs "+utility.COST_Smelter_Stone+" Stone to build.",  _x + _os, _y + (_os * 1));
	    		_g2.drawString("Nearby Iron is collected by Rocks and brought to the Smelter.",  _x + _os, _y + (_os * 2));
	    		_g2.drawString("The Smelter converts "+utility.COST_Scissors_Iron+" Iron into Scissors.",  _x + _os, _y + (_os * 3));
	    		_g2.drawString("Each additional Smelter can support "+utility.EXTRA_Scissors_PerSmelter+" Scissors.",  _x + _os, _y + (_os * 4));
			} else if(buildingStatBox == 4) {
	    		_x = (int)CurMouse.getX() - con_start_x/2 - x_add*3;
	    		_y = (int)CurMouse.getY() + x_add/2;
				_g2.setColor(backing_brown);
				_g2.fillRect(_x, _y, box_w, box_h/2);
				_g2.setColor(Color.white);
				_g2.drawRect(_x, _y, box_w, box_h/2);
				_g2.setColor(front_blue);
	    		_g2.drawString("The Totem of Paper Costs "+utility.COST_AttractorPaper_Wood+" Wood to build.",  _x + _os, _y + (_os * 1));
	    		_g2.drawString("It attracks your Paper towards it.",  _x + _os, _y + (_os * 2));
			} else if(buildingStatBox == 5) {
	    		_x = (int)CurMouse.getX() - con_start_x/2 - x_add*4;
	    		_y = (int)CurMouse.getY() + x_add/2;
				_g2.setColor(backing_brown);
				_g2.fillRect(_x, _y, box_w, box_h/2);
				_g2.setColor(Color.white);
				_g2.drawRect(_x, _y, box_w, box_h/2);
				_g2.setColor(front_blue);
	    		_g2.drawString("The Totem of Rocks Costs "+utility.COST_AttractorRock_Stone+" Stone to build.",  _x + _os, _y + (_os * 1));
	    		_g2.drawString("It attracks your Rocks towards it.",  _x + _os, _y + (_os * 2));
			} else if(buildingStatBox == 6) {
	    		_x = (int)CurMouse.getX() - con_start_x/2 - x_add*5;
	    		_y = (int)CurMouse.getY() + x_add/2;
				_g2.setColor(backing_brown);
				_g2.fillRect(_x, _y, box_w, box_h/2);
				_g2.setColor(Color.white);
				_g2.drawRect(_x, _y, box_w, box_h/2);
				_g2.setColor(front_blue);
	    		_g2.drawString("The Totem of Scissors Costs "+utility.COST_AttractorScissors_Iron+" Iron to build.",  _x + _os, _y + (_os * 1));
	    		_g2.drawString("It attracks your Scissors towards it.",  _x + _os, _y + (_os * 2));
			} else if(buildingStatBox == 7) {
	    		_x = (int)CurMouse.getX() - con_start_x/2 - x_add*6;
	    		_y = (int)CurMouse.getY() + x_add/2;
				_g2.setColor(backing_brown);
				_g2.fillRect(_x, _y, box_w, box_h/2);
				_g2.setColor(Color.white);
				_g2.drawRect(_x, _y, box_w, box_h/2);
				_g2.setColor(front_blue);
	    		_g2.drawString("Remove a building.",  _x + _os, _y + (_os * 1));
	    		_g2.drawString("This will return 50% of the building costs.",  _x + _os, _y + (_os * 2));
			} else if(buildingStatBox == 8) {
	    		_x = (int)CurMouse.getX() - con_start_x/2 - x_add*6;
	    		_y = (int)CurMouse.getY() + x_add/2;
				_g2.setColor(backing_brown);
				_g2.fillRect(_x, _y, box_w, box_h/2);
				_g2.setColor(Color.white);
				_g2.drawRect(_x, _y, box_w, box_h/2);
				_g2.setColor(front_blue);
	    		_g2.drawString("Toggle Paper Production.",  _x + _os, _y + (_os * 1));
	    		_g2.drawString("When Green, your Woodshops will output Paper up to your maximum.",  _x + _os, _y + (_os * 2));
			} else if(buildingStatBox == 9) {
	    		_x = (int)CurMouse.getX() - con_start_x/2 - x_add*6;
	    		_y = (int)CurMouse.getY() + x_add/2;
				_g2.setColor(backing_brown);
				_g2.fillRect(_x, _y, box_w, box_h/2);
				_g2.setColor(Color.white);
				_g2.drawRect(_x, _y, box_w, box_h/2);
				_g2.setColor(front_blue);
	    		_g2.drawString("Toggle Rock Production.",  _x + _os, _y + (_os * 1));
	    		_g2.drawString("When Green, your Rockery will output Rocks up to your maximum.",  _x + _os, _y + (_os * 2));
			} else if(buildingStatBox == 10) {
	    		_x = (int)CurMouse.getX() - con_start_x/2 - x_add*6;
	    		_y = (int)CurMouse.getY() + x_add/2;
				_g2.setColor(backing_brown);
				_g2.fillRect(_x, _y, box_w, box_h/2);
				_g2.setColor(Color.white);
				_g2.drawRect(_x, _y, box_w, box_h/2);
				_g2.setColor(front_blue);
	    		_g2.drawString("Toggle Scissor Production.",  _x + _os, _y + (_os * 1));
	    		_g2.drawString("When Green, your Smelters will output Scissors up to your maximum.",  _x + _os, _y + (_os * 2));
			}
	    	
		}
		
	}

	public boolean GetAA() {
    	return aa;
    }

	@Override
	public void init() {
		
		if (world_tiles % tiles_per_chunk != 0 ) {
			System.out.println("FATAL: Chunks must divide evenly into tiles");
		}

		setSize(window_X, window_Y);

		utility = new Utility();
		utility.wg_kT_R = 14 * tiles_size;
		utility.biome_golbal_density_mod = tiles_size;
		
		try {
			TopBar = ImageIO.read(Bitmaps.class.getResource("/resource/topbar.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		theWorld = new GameWorld_Applet(world_tiles, tiles_size, tiles_per_chunk, utility);
		try {
			theBitmaps = new Bitmaps_Applet();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		theSpriteManger = new SpriteManager_Applet(theWorld, theBitmaps, utility);

		addMouseWheelListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);

	    aa_on.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_SPEED);

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			mouseClick = false;
			buildingToPlace = null;
			buildingToMove = null;
		} else if (e.getKeyChar() == 'a') {
			disable_aa = !disable_aa;
		} else if (e.getKeyChar() == 'd') {
			utility.dbg = !utility.dbg;
		} else if (e.getKeyChar() == 'w') {
			utility.wg = !utility.wg;
		} else if (e.getKeyChar() == 'c') {
			theSpriteManger.resource_manager.AddResources(ResourceType.Cactus, 100, ObjectOwner.Player);
			theSpriteManger.resource_manager.AddResources(ResourceType.Rockpile, 100, ObjectOwner.Player);
			theSpriteManger.resource_manager.AddResources(ResourceType.Mine, 100, ObjectOwner.Player);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		CurMouse = e.getPoint();
		if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
			if (last_X > -1) {
				ROTATE = (float) (ROTATE + ((e.getX() - last_X)/(float)window_X)*Math.PI);
				YSHEAR = YSHEAR + ((e.getY() - last_Y)/(float)window_Y);
				if (YSHEAR > 1) {
					YSHEAR = 1;
				}
				if (YSHEAR < 0.05) {
					YSHEAR = 0.05f;
				}
			}
			last_X = e.getX();
			last_Y = e.getY();
		} else if (e.getModifiers() == InputEvent.BUTTON1_MASK && buildingToMove == null) {
			sendMouseDragPing = true;
			mouseDrag = true;
			if (last_X > -1) {
				TRANSLATE_X = TRANSLATE_X + ((e.getX() - last_X)) * 2;
				TRANSLATE_Y = TRANSLATE_Y + ((e.getY() - last_Y)) * 2;
	//			if (YSHEAR > 1) {
	//				YSHEAR = 1;
	//			}
	//			if (YSHEAR < 0.05) {
	//				YSHEAR = 0.05;
	//			}
			}
			last_X = e.getX();
			last_Y = e.getY();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		CurMouse = e.getPoint();
		if (CurMouse.getX() > (con_start_x + (0 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (0 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
			buildingStatBox = 1;
		} else if (CurMouse.getX() > (con_start_x + (1 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (1 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
			buildingStatBox = 2;
		} else if (CurMouse.getX() > (con_start_x + (2 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (2 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
			buildingStatBox = 3;
		} else if (CurMouse.getX() > (con_start_x + (3 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (3 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
			buildingStatBox = 4;
		} else if (CurMouse.getX() > (con_start_x + (4 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (4 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
			buildingStatBox = 5;
		} else if (CurMouse.getX() > (con_start_x + (5 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (5 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
			buildingStatBox = 6;
		} else if (CurMouse.getX() > (con_start_x + (6 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (6 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
			buildingStatBox = 7;
		} else if (CurMouse.getX() > (con_start_x + (7 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (7 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
			buildingStatBox = 8;
		} else if (CurMouse.getX() > (con_start_x + (8 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (8 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
			buildingStatBox = 9;
		} else if (CurMouse.getX() > (con_start_x + (9 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (9 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
			buildingStatBox = 10;
		} else {
			buildingStatBox = 0;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (endGame == 1 || endGame == 2) endGame = 3;
			mouseClick = false;
		    if (CurMouse.getX() > (con_start_x + (0 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (0 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
				if (buildingToPlace == BuildingType.Woodshop) buildingToPlace = null;
				else buildingToPlace = BuildingType.Woodshop;
			} else if (CurMouse.getX() > (con_start_x + (1 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (1 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
				if (buildingToPlace == BuildingType.Rockery) buildingToPlace = null;
				else buildingToPlace = BuildingType.Rockery;
			} else if (CurMouse.getX() > (con_start_x + (2 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (2 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
				if (buildingToPlace == BuildingType.Smelter) buildingToPlace = null;
				else buildingToPlace = BuildingType.Smelter;
			} else if (CurMouse.getX() > (con_start_x + (3 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (3 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
				if (buildingToPlace == BuildingType.AttractorPaper) buildingToPlace = null;
				else buildingToPlace = BuildingType.AttractorPaper;
			} else if (CurMouse.getX() > (con_start_x + (4 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (4 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
				if (buildingToPlace == BuildingType.AttractorRock) buildingToPlace = null;
				else buildingToPlace = BuildingType.AttractorRock;
			} else if (CurMouse.getX() > (con_start_x + (5 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (5 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
				if (buildingToPlace == BuildingType.AttractorScissors) buildingToPlace = null;
				else buildingToPlace = BuildingType.AttractorScissors;
			} else if (CurMouse.getX() > (con_start_x + (6 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (6 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
				if (buildingToPlace == BuildingType.X) buildingToPlace = null;
				else buildingToPlace = BuildingType.X;
			} else if (CurMouse.getX() > (con_start_x + (7 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (7 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
				theSpriteManger.resource_manager.GEN_PAPER_PLAYER = !theSpriteManger.resource_manager.GEN_PAPER_PLAYER;
			} else if (CurMouse.getX() > (con_start_x + (8 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (8 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
				theSpriteManger.resource_manager.GEN_ROCK_PLAYER = !theSpriteManger.resource_manager.GEN_ROCK_PLAYER;
			} else if (CurMouse.getX() > (con_start_x + (9 * x_add) - x_add/2) && CurMouse.getX() < (con_start_x + (9 * x_add) + x_add/2) && CurMouse.getY() > (y_height/2 - x_add/2) && CurMouse.getY() <  (y_height/2 + (x_add/2)) ) {
				theSpriteManger.resource_manager.GEN_SCISSORS_PLAYER = !theSpriteManger.resource_manager.GEN_SCISSORS_PLAYER;
			} else {
				mouseClick = true;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseDrag = false;
		buildingToMove = null;
		if (e.getButton() == MouseEvent.BUTTON3 || e.getButton() == MouseEvent.BUTTON1) {
			last_X = -1;
			last_Y = -1;
		}


	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() < 0) {
			ZOOM = ZOOM * 1.2f;
		}
		else {
			ZOOM = ZOOM * 0.8f;
		}
		if (ZOOM > 10.) ZOOM = 10.f;
		if (ZOOM < 0.1) ZOOM = 0.1f;
	}

	@Override
	public void paint (Graphics g) {
		final float TIME = (System.nanoTime() / 1000000);
		TIME_TO_RENDER = TIME - TIME_OF_FRAME;
		TIME_OF_FRAME = TIME;
		++FRAMES;
		if (TIME > TIME_OF_LAST_SECOND) {
			FPS = FRAMES - FRAMES_LAST_SECOND;
			FRAMES_LAST_SECOND = FRAMES;
			TIME_OF_LAST_SECOND = (float) (TIME + 1000.);
		}

		final Graphics2D g2 = (Graphics2D)g;
		g2.setFont(myFont);
		if (af_none == null) {
			af_none = g2.getTransform();
		}
		if (utility.wg == true) {
			utility.wg = false;
			theSpriteManger.Reset();
			theWorld.Reset();
		}
		af = g2.getTransform();
		af_translate_zoom = g2.getTransform();
		af_shear_rotate = g2.getTransform();
		af_backing = g2.getTransform();
		//af_translate_zoom_counter_rotate = g2.getTransform();
		//af_anti_shear_rotate = g2.getTransform();

		g2.setColor (Color.black);
		g2.fillRect (0, 0, window_X, window_Y);
		
		af.translate(((window_X+TRANSLATE_X)/2),((window_Y+TRANSLATE_Y)/2)); //*(1./ZOOM)
		af.scale(1.5*ZOOM, 1.5*ZOOM);
	    af.scale(1, YSHEAR);
	    af.rotate(-ROTATE);
	    g2.setTransform(af);
	    
		int halfWorldSize = (world_tiles * tiles_size)/2;
	    af_backing.translate(-halfWorldSize + (window_X/2)+TRANSLATE_X,-halfWorldSize + (window_Y/2)+TRANSLATE_Y );
	    af_backing.scale(ZOOM, ZOOM*YSHEAR);
	    af_backing.rotate(ROTATE, halfWorldSize, halfWorldSize);

	    af_translate_zoom.translate(((window_X+TRANSLATE_X)/2),((window_Y+TRANSLATE_Y)/2)); //*(1./ZOOM)
	    af_translate_zoom.scale(1.5*ZOOM, 1.5*ZOOM);
	    
	    //af_translate_zoom_counter_rotate.rotate(-ROTATE);
	    //af_translate_zoom_counter_rotate.translate(((window_X+TRANSLATE_X)/2),((window_Y+TRANSLATE_Y)/2)); //*(1./ZOOM)
	    //af_translate_zoom_counter_rotate.scale(1.5*ZOOM, 1.5*ZOOM);

	    //
	    af_shear_rotate.scale(1, YSHEAR);
	    af_shear_rotate.rotate(-ROTATE);
	    
	    //af_anti_shear_rotate.scale(1, YSHEAR);
	    //af_anti_shear_rotate.rotate(-ROTATE);	   


	    if (CurMouse != null) {
			try {
				MouseTF = af.inverseTransform(CurMouse, MouseTF);
			} catch (final NoninvertibleTransformException e1) {
				e1.printStackTrace();
			}
		}

	    if (last_X > -1 || last_zoom != ZOOM) {
			SetAA(g2,false);
		} else {
			SetAA(g2,true);
		}

	    if (theSpriteManger.GetWorldSeeded() == false) {
	    	ContentCreation(g2, af);
	    } else {
	    	//DrawBufferedBackground(g2);
		    theWorld.DrawTiles(g2, false, GetAA());
    		//theSpriteManger.PlaceSpooge(0, 200, ObjectOwner.Player, 5, 1f); //test
		    theSpriteManger.Render(g2, 
		    		af, 
		    		af_translate_zoom, 
		    		af_shear_rotate, 
		    		af_none);
		    if (buildingToPlace != null) {
		    	final boolean placed = theSpriteManger.TryPlaceItem(buildingToPlace, g2, af, af_translate_zoom, af_shear_rotate, af_none, (int)MouseTF.getX(), (int)MouseTF.getY(), mouseClick);
		    	if (placed == true) {
		    		buildingToPlace = null;
		    	}
		    } else if (buildingToMove != null) {
		    		buildingToMove.MoveBuilding((int)MouseTF.getX(), (int)MouseTF.getY());
	        } else if (sendMouseDragPing == true) {
		    	//am i over a building?
		    	buildingToMove = theSpriteManger.GetBuildingAtMouse((int)MouseTF.getX(), (int)MouseTF.getY());
		    	if (buildingToMove != null) {
		    		if (buildingToMove.GetCollects().size() > 0) buildingToMove = null;
		    		else if (buildingToMove.GetOwner() == ObjectOwner.Enemy) buildingToMove = null;
		    		else if (buildingToMove.GetType() == BuildingType.Base) buildingToMove = null;
		    	}
	        }
		    DrawTopBar(g2);
		    theSpriteManger.Tick();
		    
			//check WIN/LOOSE
			if (theSpriteManger.enemy_base.GetDead() == true && endGame == 0) {
				endGame = 1;
			} else if (theSpriteManger.player_base.GetDead() == true && endGame == 0) {
				endGame = 2;
			}

			if (endGame == 1) {
				g2.setTransform(af_none);
				g2.drawImage(theBitmaps.WIN,0,0,null);
			} else if (endGame == 2) {
				g2.setTransform(af_none);
				g2.drawImage(theBitmaps.LOOSE,0,0,null);
			}
	    }

		mouseClick = false;
		sendMouseDragPing = false;
	    last_zoom = ZOOM;
	    g2.setTransform(af_none);
	}

	@Override
	public void run() {
		// lower ThreadPriority
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		// run a long while (true) this means in our case "always"
		while (true) {
			// repaint
			repaint();
			try	{
				if (TIME_TO_RENDER + SLEEP > 1000./30) --SLEEP;
				else if (TIME_TO_RENDER + SLEEP < 1000./30) ++SLEEP;
				if (SLEEP < 3) SLEEP = 3;
				else if (SLEEP > 40) SLEEP = 40;

				Thread.sleep (SLEEP);
			}
			catch (final InterruptedException ex)	{
				// do nothing
			}
			// set ThreadPriority to maximum value
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
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

	@Override
	public void start() {
		th = new Thread (this);
		th.start ();
	}
	@Override
	public void stop() { }
	private void TopText(Graphics2D _g2, String _str) {
		_g2.setTransform(af_none);
		_g2.setColor(Color.white);
		_g2.drawString(_str, 25, 25);
	}
	@Override
	public void update (Graphics g)	{

		final Graphics2D g2 = (Graphics2D)g;

		// initialize buffer
		if (dbImage == null) {
			dbImage = createImage (this.getSize().width, this.getSize().height);
			dbg = dbImage.getGraphics ();
		}

		// clear screen in background
		dbg.setColor (getBackground ());
		dbg.fillRect (0, 0, this.getSize().width, this.getSize().height);

		// draw elements in background
		dbg.setColor (getForeground());
		paint (dbg);

		// draw image on the screen
		g2.drawImage (dbImage, 0, 0, this);
	}


}