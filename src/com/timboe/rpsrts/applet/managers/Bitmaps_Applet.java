package com.timboe.rpsrts.applet.managers;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.timboe.rpsrts.managers.Bitmaps;

public class Bitmaps_Applet extends Bitmaps {

	private static Bitmaps_Applet singleton = new Bitmaps_Applet();

	public static BufferedImage clip(final BufferedImage src, final int x, final int y, final int w, final int h) { //Stolen from notch
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        BufferedImage newImage = null;

        try
        {
            final GraphicsDevice screen = ge.getDefaultScreenDevice();
            final GraphicsConfiguration gc = screen.getDefaultConfiguration();
            newImage = gc.createCompatibleImage(w, h, Transparency.BITMASK);
        }
        catch (final Exception e)
        {
        }

        if (newImage == null)
        {
            newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }

        final int[] pixels = new int[w * h];
        src.getRGB(x, y, w, h, pixels, 0, w);
        newImage.setRGB(0, 0, w, h, pixels, 0, w);

        return newImage;
    }

	public static Bitmaps_Applet GetBitmaps_Applet() {
		return singleton;
	}
	public BufferedImage off[];
	public BufferedImage on[];
	public BufferedImage sound[];
	public BufferedImage quality[];
	public BufferedImage pause[];
	public BufferedImage ff[];
	public BufferedImage WIN;
	public BufferedImage LOOSE;

	public BufferedImage tree[];
	public BufferedImage cactus[];
	public BufferedImage mine[];
	public BufferedImage stone[];

	public BufferedImage base_player[];
	public BufferedImage base_enemy[];
	public BufferedImage construction_player[];
	public BufferedImage construction_enemy[];
	public BufferedImage smelter_player[];
	public BufferedImage smelter_enemy[];
	public BufferedImage woodshop_player[];
	public BufferedImage woodshop_enemy[];
	public BufferedImage rockery_player[];
	public BufferedImage rockery_enemy[];
	public BufferedImage attractor_scissors_player[];
	public BufferedImage attractor_paper_player[];
	public BufferedImage attractor_rock_player[];
	public BufferedImage attractor_scissors_enemy[];
	public BufferedImage attractor_paper_enemy[];
	public BufferedImage attractor_rock_enemy[];

	public BufferedImage scissor_player[];
	public BufferedImage scissor_enemy[];
	public BufferedImage paper_player[];
	public BufferedImage paper_enemy[];
	public BufferedImage rock_player[];
	public BufferedImage rock_enemy[];
	public BufferedImage spock_player[];
	public BufferedImage spock_enemy[];
	public BufferedImage lizard_player[];
	public BufferedImage lizard_enemy[];

	public BufferedImage proj_scissor_player[];
	public BufferedImage proj_scissor_enemy[];
	public BufferedImage proj_paper_player[];
	public BufferedImage proj_paper_enemy[];
	public BufferedImage proj_rock_player[];
	public BufferedImage proj_rock_enemy[];
	public BufferedImage proj_lizard_enemy[];
	public BufferedImage proj_lizard_player[];

	public BufferedImage X[]; //Switched out later

	private Bitmaps_Applet() {
		super();

		BufferedImage resource_sheet = null;
        BufferedImage resource_actors = null;
        BufferedImage resource_attractor = null;
        BufferedImage resource_trees = null;
        BufferedImage resource_on_off = null;
        BufferedImage resource_projectile = null;
        BufferedImage resource_mine = null;
        BufferedImage resource_stone = null;

		try {
			resource_sheet = ImageIO.read(Bitmaps.class.getResource("/resource/resource_sheet.gif"));
	        resource_actors = ImageIO.read(Bitmaps.class.getResource("/resource/resource_actors.gif"));
	        resource_attractor = ImageIO.read(Bitmaps.class.getResource("/resource/resource_attractor.gif"));
	        resource_trees = ImageIO.read(Bitmaps.class.getResource("/resource/resource_tree.gif"));
	        resource_on_off = ImageIO.read(Bitmaps.class.getResource("/resource/offon.gif"));
	        resource_projectile = ImageIO.read(Bitmaps.class.getResource("/resource/resource_projectile.gif"));
	        resource_mine = ImageIO.read( this.getClass().getResource("/resource/mine.gif") );
	        resource_stone =  ImageIO.read( this.getClass().getResource("/resource/stone.gif") );

	        WIN = ImageIO.read( this.getClass().getResource("/resource/youreawinner.png") );
	        LOOSE = ImageIO.read( this.getClass().getResource("/resource/yourealooser.png") );
		} catch (final IOException e) {
			e.printStackTrace();
		}

        X = new BufferedImage[1];

        on = new BufferedImage[4];
        off = new BufferedImage[4];
    	sound = new BufferedImage[2];
    	quality = new BufferedImage[2];
    	pause = new BufferedImage[2];
    	ff = new BufferedImage[2];
        for (int i = 0; i < 4; ++i) {
        	on[i] = clip(resource_on_off, 0, 0, 12, 6);
        	off[i] = clip(resource_on_off, 0, 6, 12, 6);
        	if (i >= 2) continue;
        	final int j = (i+1) * 12;
        	quality[i] = clip(resource_on_off, 0, j, 12, 12);
        	sound[i] = clip(resource_on_off, 12, j, 12, 12);
        	pause[i] = clip(resource_on_off, 24, j, 12, 12);
        	ff[i] = clip(resource_on_off, 36, j, 12, 12);
        }


		base_player = new BufferedImage[4];
		base_enemy = new BufferedImage[4];
		construction_player = new BufferedImage[4];
		construction_enemy = new BufferedImage[4];
		smelter_player = new BufferedImage[4];
		smelter_enemy = new BufferedImage[4];
		woodshop_player = new BufferedImage[4];
		woodshop_enemy = new BufferedImage[4];
		rockery_player = new BufferedImage[4];
		rockery_enemy = new BufferedImage[4];
		for (int i=0; i<4; ++i) {
			int j = 0;
			base_player[3-i] = clip(resource_sheet, i*16, j, 16, 16);
			j += 16;
			base_enemy[3-i] = clip(resource_sheet, i*16, j, 16, 16);
			j += 16;
			construction_player[i] = clip(resource_sheet, i*16, j, 16, 16);
			j += 16;
			construction_enemy[i] = clip(resource_sheet, i*16, j, 16, 16);
			j += 16;
			smelter_player[i] = clip(resource_sheet, i*16, j, 16, 16);
			j += 16;
			smelter_enemy[i] = clip(resource_sheet, i*16, j, 16, 16);
			j += 16;
			woodshop_player[i] = clip(resource_sheet, i*16, j, 16, 16);
			j += 16;
			woodshop_enemy[i] = clip(resource_sheet, i*16, j, 16, 16);
			j += 16;
			rockery_player[i] = clip(resource_sheet, i*16, j, 16, 16);
			j += 16;
			rockery_enemy [i] = clip(resource_sheet, i*16, j, 16, 16);
		}

		scissor_player = new BufferedImage[4];
		scissor_enemy = new BufferedImage[4];
		paper_player = new BufferedImage[4];
		paper_enemy = new BufferedImage[4];
		rock_player = new BufferedImage[4];
		rock_enemy = new BufferedImage[4];
		spock_player = new BufferedImage[4];
		spock_enemy = new BufferedImage[4];
		lizard_player = new BufferedImage[8];
		lizard_enemy = new BufferedImage[8];
		for (int i=0; i<4; ++i) {
			int j = 0;
			scissor_player[i] = clip(resource_actors, i*6, j, 6, 6);
			j += 6;
			scissor_enemy[i] = clip(resource_actors, i*6, j, 6, 6);
			j += 6;
			paper_player[i] = clip(resource_actors, i*6, j, 6, 6);
			j += 6;
			paper_enemy[i] = clip(resource_actors, i*6, j, 6, 6);
			j += 6;
			rock_player[i] = clip(resource_actors, i*6, j, 6, 6);
			j += 6;
			rock_enemy[i] = clip(resource_actors, i*6, j, 6, 6);
			j += 6;
			spock_player[i] = clip(resource_actors, i*5, j, 5, 15);
			j += 15;
			spock_enemy[i] = clip(resource_actors, i*5, j, 5, 15);
			j -= 15;
			lizard_player[i]   = clip(resource_actors, 20 + (i*15), j,    15, 6);
			lizard_player[i+4] = clip(resource_actors, 20 + (i*15), j+12, 15, 6);
			j += 6;
			lizard_enemy[i]   = clip(resource_actors, 20 + (i*15), j,    15, 6);
			lizard_enemy[i+4] = clip(resource_actors, 20 + (i*15), j+12, 15, 6);
		}

		proj_scissor_player = new BufferedImage[4];
		proj_scissor_enemy = new BufferedImage[4];
		proj_paper_player = new BufferedImage[4];
		proj_paper_enemy = new BufferedImage[4];
		proj_rock_player = new BufferedImage[4];
		proj_rock_enemy = new BufferedImage[4];
		proj_lizard_player = new BufferedImage[3];
		proj_lizard_enemy = new BufferedImage[3];
		for (int i=0; i<4; ++i) {
			int j = 0;
			proj_scissor_player[i] = clip(resource_projectile, i*3, j, 3, 3);
			j += 3;
			proj_scissor_enemy[i] = clip(resource_projectile, i*3, j, 3, 3);
			j += 3;
			proj_paper_player[i] = clip(resource_projectile, i*3, j, 3, 3);
			j += 3;
			proj_paper_enemy[i] = clip(resource_projectile, i*3, j, 3, 3);
			j += 3;
			proj_rock_player[i] = clip(resource_projectile, i*3, j, 3, 3);
			j += 3;
			proj_rock_enemy[i] = clip(resource_projectile, i*3, j, 3, 3);
			j += 3;
			//
			if (i >= 3) continue;
			proj_lizard_player[i] = clip(resource_projectile, i*4, j, 4, 4);
			j += 4;
			proj_lizard_enemy[i] = clip(resource_projectile, i*4, j, 4, 4);
		}

		attractor_paper_enemy = new BufferedImage[4];
		attractor_rock_enemy = new BufferedImage[4];
		attractor_scissors_enemy = new BufferedImage[4];
		attractor_paper_player = new BufferedImage[4];
		attractor_rock_player = new BufferedImage[4];
		attractor_scissors_player = new BufferedImage[4];
		for (int i=0; i<4; ++i) {
			int j = 0;
			attractor_scissors_player[i] = clip(resource_attractor, i*8, j, 8, 23);
			j += 23;
			attractor_scissors_enemy[i] = clip(resource_attractor, i*8, j, 8, 23);
			j += 23;
			attractor_paper_player[i] = clip(resource_attractor, i*8, j, 8, 23);
			j += 23;
			attractor_paper_enemy[i] = clip(resource_attractor, i*8, j, 8, 23);
			j += 23;
			attractor_rock_player[i] = clip(resource_attractor, i*8, j, 8, 23);
			j += 23;
			attractor_rock_enemy[i] = clip(resource_attractor, i*8, j, 8, 23);
		}

        mine = new BufferedImage[4];
        stone = new BufferedImage[4];
		cactus = new BufferedImage[6];
		tree = new BufferedImage[6];
		for (int i=0; i<6; ++i) {
			int j=0;
			tree[i] = clip(resource_trees, i*6, j, 6, 12);
			j += 12;
			cactus[i] = clip(resource_trees, i*6, j, 6, 12);
			if (i >= 4) continue;
			mine[i] = clip(resource_mine, i*6, 0, 6, 6);
			stone[i] = clip(resource_stone, i*6, 0, 6, 6);
		}

	}

}
