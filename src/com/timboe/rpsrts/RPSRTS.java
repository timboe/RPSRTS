package com.timboe.rpsrts;

import java.applet.Applet;
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
import java.util.Locale;

import com.timboe.rpsrts.applet.managers.GameWorld_Applet;
import com.timboe.rpsrts.applet.managers.SceneRenderer_Applet;
import com.timboe.rpsrts.applet.managers.SpriteManager_Applet;
import com.timboe.rpsrts.applet.managers.TransformStore;
import com.timboe.rpsrts.enumerators.GameMode;
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.enumerators.ResourceType;
import com.timboe.rpsrts.managers.ResourceManager;
import com.timboe.rpsrts.managers.Utility;

public class RPSRTS extends Applet implements Runnable, MouseWheelListener, MouseMotionListener, MouseListener, KeyListener {

	/**
	 *
	 */
	private static final long serialVersionUID = -4515697164989975837L;


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

	public void doWinLoose() {
		if (theSpriteManger.GetBase(ObjectOwner.Enemy) != null && theSpriteManger.GetBase(ObjectOwner.Enemy).GetDead() == true) {
			utility.gameMode = GameMode.gameOverWon;
		} else if (theSpriteManger.GetBase(ObjectOwner.Player) != null && theSpriteManger.GetBase(ObjectOwner.Player).GetDead() == true) {
			utility.gameMode = GameMode.gameOverLost;
		}
		if (utility.gameMode != GameMode.gameOn) {
			final int seconds_to_win = (int) (utility.game_time_count / 1000);
			int bonus = (int) (((utility.extra_score_mins * 60) - seconds_to_win) * utility.quick_win_bonus);
			if (bonus < 0) bonus = 0;
			if(utility.gameMode == GameMode.gameOverWon) {
				resource_manager.ScorePoints(ObjectOwner.Player, bonus);
			} else {
				resource_manager.ScorePoints(ObjectOwner.Enemy, bonus);
			}
			utility.SetTicksPerRender(utility.slowmo_ticks_per_render);
			utility.fastForward = false;
			utility.loose_time = System.currentTimeMillis() / 1000l;
		}
	}

	public void genNewWorld(boolean _start) {
		utility.gameMode = GameMode.titleScreen;
		theTransforms.Reset();
		utility.game_time_count = 0;
		theSpriteManger.Reset();
		theWorld.Reset();
		theSceneRenderer.background_buffered = null;
		utility._TICK = 0;
		if (_start == true) {
			utility.doWorldGen = true;
		} else {
			utility.doWorldGen = false;
		}
		utility.setSeed();
	}

	@Override
	public void init() {

		if (utility.world_tiles % utility.tiles_per_chunk != 0 ) {
			System.out.println("FATAL: Chunks must divide evenly into tiles");
		}

		utility._RPSRTS = this;

		setSize(utility.window_X, utility.window_Y);

		theWorld.Init(); //important!!!!
		utility.rndSeedTxt = Integer.toString( utility.rndI(10000000) );

		addMouseWheelListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);

		if (utility.dbg == true) {
			genNewWorld(true);
		} else {
			genNewWorld(false);
		}
	}
	@Override
	public void keyPressed(final KeyEvent e) {
		if (utility.gameMode == GameMode.titleScreen && utility.doWorldGen == false) {
			if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				if (utility.rndSeedTxt.length() > 0) {
					utility.rndSeedTxt = utility.rndSeedTxt.substring(0, utility.rndSeedTxt.length()-1 );
				}
			} else if (utility.rndSeedTxt.length() < 8) {
				if (e.getKeyChar() >= '!' && e.getKeyChar() <= '~') { //assuming ASCII
					utility.rndSeedTxt += e.getKeyChar();
				}
			}
		} else if (utility.gameMode == GameMode.gameOverWon) {
			if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				if (utility.playerName.length() > 0) {
					utility.playerName = utility.playerName.substring(0, utility.playerName.length()-1 );
				}
			} else if (utility.playerName.length() < 3) {
				if (e.getKeyChar() >= '!' && e.getKeyChar() <= '~') { //assuming ASCII
					utility.playerName += e.getKeyChar();
					utility.playerName = utility.playerName.toUpperCase(Locale.ENGLISH);
				}
			}
		}

		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			utility.mouseClick = false;
			theSceneRenderer.buildingToPlace = null;
			theSceneRenderer.buildingToMove = null;
		} else if (e.getKeyChar() == 'q' || e.getKeyChar() == 'Q') {
			theSceneRenderer.toggleQuality();
		} else if (e.getKeyChar() == 'D') {
			utility.dbg = !utility.dbg;
		} else if (e.getKeyChar() == 's' || e.getKeyChar() == 'S') {
			theSceneRenderer.toggleSound();
		} else if (e.getKeyChar() == 'W') {
			genNewWorld(true);
		} else if (e.getKeyChar() == '£') {
			resource_manager.AddResources(ResourceType.Cactus, 100, ObjectOwner.Player);
			resource_manager.AddResources(ResourceType.Rockpile, 100, ObjectOwner.Player);
			resource_manager.AddResources(ResourceType.Mine, 100, ObjectOwner.Player);
		} else if (e.getKeyChar() == 'p' || e.getKeyChar() == 'P') {
			theSceneRenderer.togglePause();
		} else if (e.getKeyChar() == 'f' || e.getKeyChar() == 'F') {
			theSceneRenderer.toggleFF();
		}

	}
	@Override
	public void keyReleased(final KeyEvent e) {}

	@Override
	public void keyTyped(final KeyEvent e) {}

	@Override
	public void mouseClicked(final MouseEvent e) {}
	@Override
	public void mouseDragged(final MouseEvent e) {
		theSceneRenderer.CurMouse = e.getPoint();
		if (utility.gameMode == GameMode.titleScreen) return;
		final int bothMask = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK;
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
	public void mouseEntered(final MouseEvent arg0) {}

	@Override
	public void mouseExited(final MouseEvent arg0) {}

	@Override
	public void mouseMoved(final MouseEvent e) {
		theSceneRenderer.CurMouse = e.getPoint();
		theSceneRenderer.mouseMove();
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			utility.mouseClick = true;
		}
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		utility.mouseDrag = false;
		theSceneRenderer.buildingToMove = null;
		if (e.getButton() == MouseEvent.BUTTON3 || e.getButton() == MouseEvent.BUTTON1) {
			last_X = -1;
			last_Y = -1;
		}
		if (e.getButton() == MouseEvent.BUTTON3) {
			theSceneRenderer.buildingToPlace = null;
			theSceneRenderer.buildingToMove = null;
		}
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		if (utility.gameMode != GameMode.titleScreen) {
			if (e.getWheelRotation() < 0) {
				theTransforms.zoomIn(false);
			} else {
				theTransforms.zoomOut(false);
			}
		}
		e.consume();
	}

	@Override
	public void paint (final Graphics g) {
		final Graphics2D g2 = (Graphics2D)g;

	    theTransforms.SetAA(g2,true);
		theTransforms.updateTransforms();
		theSceneRenderer.doInverseMouseTransform();
    	if (utility.mouseClick == true) theSceneRenderer.mouseClick();

		theSceneRenderer.sceneBlackout(g2);
	    if (utility.gameMode == GameMode.titleScreen) {
	    	theSceneRenderer.sceneTitle(g2);
	    } else if (utility.gameMode == GameMode.gameOn){
	    	theSceneRenderer.sceneGame(g2);
		    doWinLoose();
	    } else if (utility.gameMode == GameMode.gameOverLost || utility.gameMode == GameMode.gameOverWon) {
	    	theSceneRenderer.sceneGameOver(g2);
	    }

		utility.mouseClick = false;
		utility.sendMouseDragPing = false;
	    g2.setTransform(theTransforms.af_none);
	}


	@Override
	public void run() {
		// lower ThreadPriority
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		// run a long while (true) this means in our case "always"
		while (true) {
			if (utility._TIME_OF_NEXT_TICK > System.currentTimeMillis()) {
				// too soon to repaint, wait...
				try {
					Thread.sleep(Math.abs(utility._TIME_OF_NEXT_TICK - System.currentTimeMillis()));
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
			utility._TIME_OF_NEXT_TICK = System.currentTimeMillis() + Math.round(1000f / utility.GetDesiredTPS());

			++utility._TICK;
			if (utility.gamePaused == false && utility.gameMode != GameMode.titleScreen) {
				theSpriteManger.Tick();
				utility.game_time_count += (System.currentTimeMillis() - utility._TIME_OF_LAST_TICK);
			}

			if (utility._TICK % utility.do_fps_every_x_ticks == 0) {
				utility.FPS = Math.round((1. / (System.currentTimeMillis() - utility._TIME_OF_LAST_TICK)* 1000.) / utility.GetTicksPerRender());
				if (utility.FPS > 999) {
					utility.FPS = 999;
				}
			}

			if (utility._TICK % utility.GetTicksPerRender() == 0) {
				repaint();
			}

			utility._TIME_OF_LAST_TICK = System.currentTimeMillis();
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
	public void update (final Graphics g)	{

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
