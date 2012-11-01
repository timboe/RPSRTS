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
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Locale;

import com.timboe.rpsrts.applet.GameWorld_Applet;
import com.timboe.rpsrts.applet.SceneRenderer_Applet;
import com.timboe.rpsrts.applet.ShapeStore;
import com.timboe.rpsrts.applet.SpriteManager_Applet;
import com.timboe.rpsrts.applet.TransformStore;
import com.timboe.rpsrts.enumerators.GameMode;
import com.timboe.rpsrts.enumerators.ObjectOwner;
import com.timboe.rpsrts.enumerators.Pwd;
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
	private final ShapeStore theShapeStore = ShapeStore.GetShapeStore();
	
    @Override
	public void destroy() { }

	public void doWinLoose() {
		if (theSpriteManger.GetBase(ObjectOwner.Enemy) != null && theSpriteManger.GetBase(ObjectOwner.Enemy).GetDead() == true) {
			utility.gameMode = GameMode.gameOverWon;
		} else if (theSpriteManger.GetBase(ObjectOwner.Player) != null && theSpriteManger.GetBase(ObjectOwner.Player).GetDead() == true) {
			utility.gameMode = GameMode.gameOverLost;
		}
		if (utility.gameMode != GameMode.gameOn) {
			int seconds_to_win = (int) (utility.game_time_count / 1000);
			int bonus = (int) (((utility.extra_score_mins * 60) - seconds_to_win) * utility.quick_win_bonus);
			if (bonus < 0) bonus = 0;
			if(utility.gameMode == GameMode.gameOverWon) {
				resource_manager.ScorePoints(ObjectOwner.Player, bonus);
			} else {
				resource_manager.ScorePoints(ObjectOwner.Enemy, bonus);
			}
			utility.SetTicksPerRender(utility.slowmo_ticks_per_render); 
			utility.loose_time = System.currentTimeMillis() / 1000l;
		}
	}

	public void genNewWorld() {
		utility.gameMode = GameMode.titleScreen;
		theTransforms.Reset();
		utility.doWorldGen = true;
		utility.game_time_count = 0;
		utility.setSeed();
		theSpriteManger.Reset();
		theWorld.Reset();
		theSceneRenderer.background_buffered = null;
		utility.SetTicksPerRender(utility.game_ticks_per_render);
		utility._TICK = 0;
	}
	
	public void SubmitHighScore() throws IOException {
		//Create Post String
		//Thanks to http://robbamforth.wordpress.com/2009/04/27/java-how-to-post-to-a-htmlphp-post-form/
		String data = URLEncoder.encode("pwd",    "UTF-8") + "=" + URLEncoder.encode(Pwd.GetPass(), "UTF-8");
		data += "&" + URLEncoder.encode("name",    "UTF-8") + "=" + URLEncoder.encode(utility.playerName, "UTF-8");
		data += "&" + URLEncoder.encode("score",   "UTF-8") + "=" + URLEncoder.encode(Integer.toString(resource_manager.GetScore(ObjectOwner.Player)), "UTF-8");
		data += "&" + URLEncoder.encode("wintime", "UTF-8") + "=" + URLEncoder.encode(Long.toString(utility.game_time_count/1000l), "UTF-8");
		                
		         
		// Send Data To Page
		URL url = new URL("http://tim-martin.co.uk/rpsrts_score.php");
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();
		   
		// Get The Response
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
		        System.out.println(line);
		}
	}

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
		} else if (utility.gameMode == GameMode.gameOverWon) {
			if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				if (utility.playerName.length() > 0) {
					utility.playerName = utility.playerName.substring(0, utility.playerName.length()-1 );
				}
			} else if (utility.playerName.length() < 3) {
				if (e.getKeyChar() >= '!' && e.getKeyChar() <= '~') { //assuming ASCII
					utility.playerName += e.getKeyChar();
					utility.playerName.toUpperCase(Locale.ENGLISH);
				}
			}
		}

		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			utility.mouseClick = false;
			theSceneRenderer.buildingToPlace = null;
			theSceneRenderer.buildingToMove = null;
		} else if (e.getKeyChar() == 'a') {
			theTransforms.toggleAA();
		} else if (e.getKeyChar() == 'd') {
			utility.dbg = !utility.dbg;
		} else if (e.getKeyChar() == 's') {
			if (utility.GetTicksPerRender() == utility.game_ticks_per_render) {
				utility.SetTicksPerRender(utility.GetTicksPerRender() * 100);
			} else {
				utility.SetTicksPerRender(utility.game_ticks_per_render);
			}
		} else if (e.getKeyChar() == 'w') {
			genNewWorld();
		} else if (e.getKeyChar() == 'c') {
			resource_manager.AddResources(ResourceType.Cactus, 100, ObjectOwner.Player);
			resource_manager.AddResources(ResourceType.Rockpile, 100, ObjectOwner.Player);
			resource_manager.AddResources(ResourceType.Mine, 100, ObjectOwner.Player);
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
		if (utility.gameMode == GameMode.titleScreen) return;
		if (e.getWheelRotation() < 0) {
			theTransforms.zoomIn(false);
		} else {
			theTransforms.zoomOut(false);
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
		    doWinLoose();
	    } else if (utility.gameMode == GameMode.gameOverLost || utility.gameMode == GameMode.gameOverWon) {
	    	theSceneRenderer.sceneGameOver(g2);
	    }

		utility.mouseClick = false;
		utility.sendMouseDragPing = false;
	    g2.setTransform(theTransforms.af_none);
	    g2.setColor(Color.gray);
	    GeneralPath x = theShapeStore.GetCross();
	    x.transform(AffineTransform.getTranslateInstance(utility.window_X/2, utility.window_Y/2));
	    x.transform(AffineTransform.getScaleInstance(0.25,0.25));
	    g2.draw(x);
		utility.worldGenLock = false;
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
					Thread.sleep(Math.abs(utility._TIME_OF_NEXT_TICK
							- System.currentTimeMillis()));
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
			utility._TIME_OF_NEXT_TICK = System.currentTimeMillis()
					+ Math.round(1000f / utility.GetDesiredTPS());
			
			++utility._TICK;
			if (utility.gamePaused == false && utility.gameMode != GameMode.titleScreen) {
				theSpriteManger.Tick();
				utility.game_time_count += (System.currentTimeMillis() - utility._TIME_OF_LAST_TICK);
			}

			if (utility._TICK % utility.do_fps_every_x_ticks == 0) {
				utility.FPS = Math.round((1. / (System.currentTimeMillis() - utility._TIME_OF_LAST_TICK)* 1000. * utility.do_fps_every_x_ticks) / utility.GetTicksPerRender());
				utility._TIME_OF_LAST_TICK = System.currentTimeMillis();
			}
			
			if (utility._TICK % utility.GetTicksPerRender() == 0) {
				repaint();
			}
			
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
