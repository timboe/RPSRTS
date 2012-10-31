package com.timboe.rpsrts;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;


import com.timboeWeb.rpsrts.GameWorld_Applet;
import com.timboeWeb.rpsrts.SceneRenderer_Applet;
import com.timboeWeb.rpsrts.SpriteManager_Applet;
import com.timboeWeb.rpsrts.TransformStore;

public class RPSRTS extends Applet implements Runnable, MouseWheelListener, MouseMotionListener, MouseListener, KeyListener {

	/**
	 *
	 */
	private static final long serialVersionUID = -4515697164989975837L;
	private long _TIME_OF_LAST_TICK = 0; // Internal
	private final int _DO_FPS_EVERY_X_TICKS = 1; // refresh FPS after X frames
	private long _TIME_OF_NEXT_TICK; // Internal
	private int _TICK; // Counter
	private final int _DESIRED_TPS = 30; // Ticks per second to aim for

	int last_X = -1; //Mouse cursor historic
	int last_Y = -1;
	
	private Thread th; //My running thread

	private Image dbImage; //Double buffer
	private Graphics dbg;

	private final Utility utility = Utility.GetUtility();
	private final GameWorld_Applet theWorld = GameWorld_Applet.GetGameWorld_Applet();
	private final SpriteManager_Applet theSpriteManger = SpriteManager_Applet.GetSpriteManager_Applet();
	private final TransformStore theTransforms = TransformStore.GetTransformStore();
	private final ResourceManager resource_manager = ResourceManager.GetResourceManager();
	private final SceneRenderer_Applet theSceneRenderer = SceneRenderer_Applet.GetSceneRenderer_Applet();
	
    @Override
	public void destroy() { }

	@Override
	public void init() {
		
		if (utility.world_tiles % utility.tiles_per_chunk != 0 ) {
			System.out.println("FATAL: Chunks must divide evenly into tiles");
		}
		
		utility._RPSRTS = this;

		setSize(utility.window_X, utility.window_Y);
				
		theWorld.Init(); //important!!!!

		addMouseWheelListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);

		if (utility.dbg == true) {
			genNewWorld();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (utility.gameMode == GameMode.titleScreen) {
			if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				if (utility.rndSeedTxt.length() > 0) {
					utility.rndSeedTxt = utility.rndSeedTxt.substring(0, utility.rndSeedTxt.length()-1 );
				}
			} else if (utility.rndSeedTxt.length() < 8) {
				if (e.getKeyChar() >= '!' && e.getKeyChar() <= '~') { //assuming ASCII
					utility.rndSeedTxt += e.getKeyChar();
				}
			}
		} else {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				utility.mouseClick = false;
				theSceneRenderer.buildingToPlace = null;
				theSceneRenderer.buildingToMove = null;
			} else if (e.getKeyChar() == 'a') {
				theTransforms.toggleAA();
			} else if (e.getKeyChar() == 'd') {
				utility.dbg = !utility.dbg;
			} else if (e.getKeyChar() == 'w') {
				genNewWorld();
			} else if (e.getKeyChar() == 'c') {
				resource_manager.AddResources(ResourceType.Cactus, 100, ObjectOwner.Player);
				resource_manager.AddResources(ResourceType.Rockpile, 100, ObjectOwner.Player);
				resource_manager.AddResources(ResourceType.Mine, 100, ObjectOwner.Player);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {
		theSceneRenderer.CurMouse = e.getPoint();
		if (utility.gameMode == GameMode.titleScreen) return;
		int bothMask = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK;
		if (e.getModifiers() == InputEvent.BUTTON2_MASK || (e.getModifiersEx() & bothMask) == bothMask) {
			if (last_X > -1) {
				if (e.getY() < last_Y) {
					theTransforms.zoomIn(true);
				} else {
					theTransforms.zoomOut(true);
				}
			}
			last_X = e.getX();
			last_Y = e.getY();
		} else if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
			if (last_X > -1) {
				theTransforms.modifyRotate((float) ( ((e.getX() - last_X)/(float)utility.window_X)*Math.PI ) );
				theTransforms.modifyShear((e.getY() - last_Y)/(float)utility.window_Y);

			}
			last_X = e.getX();
			last_Y = e.getY();
		} else if (e.getModifiers() == InputEvent.BUTTON1_MASK && theSceneRenderer.buildingToMove == null) {
			utility.sendMouseDragPing = true;
			utility.mouseDrag = true;
			if (last_X > -1) {
				theTransforms.modifyTranslate((e.getX() - last_X), (e.getY() - last_Y));
			}
			last_X = e.getX();
			last_Y = e.getY();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mouseMoved(MouseEvent e) {
		theSceneRenderer.CurMouse = e.getPoint();
		if (utility.gameMode == GameMode.titleScreen) return;
		theSceneRenderer.mouseMove();

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			utility.mouseClick = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		utility.mouseDrag = false;
		theSceneRenderer.buildingToMove = null;
		if (e.getButton() == MouseEvent.BUTTON3 || e.getButton() == MouseEvent.BUTTON1) {
			last_X = -1;
			last_Y = -1;
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() < 0) {
			theTransforms.zoomIn(false);
		} else {
			theTransforms.zoomOut(false);
		}

	}
	
	public void genNewWorld() {
		utility.gameMode = GameMode.titleScreen;
		utility.doWorldGen = true;
		utility.setSeed();
		theSpriteManger.Reset();
		theWorld.Reset();
		theSceneRenderer.background_buffered = null;
		_TICK = 0;
	}

	public void doWinLoose() {
		if (theSpriteManger.enemy_base != null && theSpriteManger.enemy_base.GetDead() == true) {
			utility.gameMode = GameMode.gameOverWon;
			utility.loose_time = System.currentTimeMillis() / 1000l;
		} else if (theSpriteManger.player_base != null && theSpriteManger.player_base.GetDead() == true) {
			utility.gameMode = GameMode.gameOverLost;
			utility.loose_time = System.currentTimeMillis() / 1000l;
		}
	}
	
	@Override
	public void paint (Graphics g) {
		final Graphics2D g2 = (Graphics2D)g;

		if (utility.worldGenLock == true) {
			System.out.println("LOCK!!!!!");
			return;
		}
		utility.worldGenLock = true;
		
	    theTransforms.SetAA(g2,true);
		theTransforms.updateTransforms();
		theSceneRenderer.doInverseMouseTransform();

		theSceneRenderer.sceneBlackout(g2);
	    if (utility.gameMode == GameMode.titleScreen) {
	    	theSceneRenderer.sceneTitle(g2);
	    } else if (utility.gameMode == GameMode.gameOn){
	    	theSceneRenderer.sceneGame(g2);
	    	if (utility.mouseClick == true) theSceneRenderer.mouseClick();
		    theSpriteManger.Tick();
		    doWinLoose();
	    } else if (utility.gameMode == GameMode.gameOverLost || utility.gameMode == GameMode.gameOverWon) {
	    	theSceneRenderer.sceneGameOver(g2);
		    theSpriteManger.Tick();
	    }

		utility.mouseClick = false;
		utility.sendMouseDragPing = false;
	    g2.setTransform(theTransforms.af_none);
	    g2.setColor(Color.white);
		g2.fillOval(utility.window_X/2 - 5, utility.window_Y/2  -5, 10, 10);
		utility.worldGenLock = false;
	}


	@Override
	public void run() {
		// lower ThreadPriority
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		// run a long while (true) this means in our case "always"
		while (true) {
			if (_TIME_OF_NEXT_TICK > System.currentTimeMillis()) {
				// too soon to repaint, wait...
				try {
					Thread.sleep(Math.abs(_TIME_OF_NEXT_TICK
							- System.currentTimeMillis()));
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
			_TIME_OF_NEXT_TICK = System.currentTimeMillis()
					+ Math.round(1000f / _DESIRED_TPS);

			if (_TICK % _DO_FPS_EVERY_X_TICKS == 0) {
				utility.FPS = Math.round(1. / (System.currentTimeMillis() - _TIME_OF_LAST_TICK)
								  * 1000. * _DO_FPS_EVERY_X_TICKS);
				_TIME_OF_LAST_TICK = System.currentTimeMillis();
			}
			++_TICK;
			repaint();
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		}
	}



	@Override
	public void start() {
		th = new Thread (this);
		th.start ();
	}
	@Override
	public void stop() { }
	

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
