package com.timboe.rpsrts.android;

import com.timboe.rpsrts.managers.Bitmaps;

import android.graphics.Bitmap;

public class Bitmaps_App extends Bitmaps {
	
	private static Bitmaps_App singleton = new Bitmaps_App();
	
	public static Bitmaps_App GetBitmaps_App() {
		return singleton;
	}
	
	public Bitmap off[];
	public Bitmap on[];
	
	public Bitmap tree[];
	public Bitmap cactus[];
	public Bitmap mine[];
	public Bitmap stone[];
	
	public Bitmap base_player[];
	public Bitmap base_enemy[];
	public Bitmap construction_player[];
	public Bitmap construction_enemy[];
	public Bitmap smelter_player[];
	public Bitmap smelter_enemy[];
	public Bitmap woodshop_player[];
	public Bitmap woodshop_enemy[];
	public Bitmap rockery_player[];
	public Bitmap rockery_enemy[];
	
	public Bitmap attractor_scissors_player[];
	public Bitmap attractor_paper_player[];
	public Bitmap attractor_rock_player[];
	public Bitmap attractor_scissors_enemy[];
	public Bitmap attractor_paper_enemy[];
	public Bitmap attractor_rock_enemy[];
	
	public Bitmap scissor_player[];
	public Bitmap scissor_enemy[];
	public Bitmap paper_player[];
	public Bitmap paper_enemy[];
	public Bitmap rock_player[];
	public Bitmap rock_enemy[];
	
	public Bitmap proj_scissor_player[];
	public Bitmap proj_scissor_enemy[];
	public Bitmap proj_paper_player[];
	public Bitmap proj_paper_enemy[];
	public Bitmap proj_rock_player[];
	public Bitmap proj_rock_enemy[];

	public Bitmap X[];
	
	private  Bitmaps_App() {
		super();
	}
	
	public void Init(Bitmap resource_sheet,
			Bitmap resource_actors,
			Bitmap resource_attractor,
			Bitmap resource_trees,
			Bitmap resource_on_off,
			Bitmap resource_projectile,
			Bitmap resource_mine,
			Bitmap resouce_stone) {
		
        X = new Bitmap[1];
        
        on = new Bitmap[4];
        off = new Bitmap[4];
        for (int i = 0; i < 4; ++i) {
        	on[i] = Bitmap.createBitmap(resource_on_off, 0, 0, 12, 6);//clip(resource_on_off, 0, 0, 12, 6);
        	off[i] = Bitmap.createBitmap(resource_on_off, 0, 6, 12, 6);
        }
		
		base_player = new Bitmap[4];
		base_enemy = new Bitmap[4];
		construction_player = new Bitmap[4];
		construction_enemy = new Bitmap[4];
		smelter_player = new Bitmap[4];
		smelter_enemy = new Bitmap[4];
		woodshop_player = new Bitmap[4];
		woodshop_enemy = new Bitmap[4];
		rockery_player = new Bitmap[4];
		rockery_enemy = new Bitmap[4];
		for (int i=0; i<4; ++i) {
			int j = 0;
			base_player[i] = Bitmap.createBitmap(resource_sheet, i*16, j, 16, 16);//clip(resource_sheet, i*16, j, 16, 16);
			j += 16;
			base_enemy[i] = Bitmap.createBitmap(resource_sheet, i*16, j, 16, 16);
			j += 16;
			construction_player[i] = Bitmap.createBitmap(resource_sheet, i*16, j, 16, 16);
			j += 16;
			construction_enemy[i] = Bitmap.createBitmap(resource_sheet, i*16, j, 16, 16);
			j += 16;
			smelter_player[i] = Bitmap.createBitmap(resource_sheet, i*16, j, 16, 16);
			j += 16;
			smelter_enemy[i] = Bitmap.createBitmap(resource_sheet, i*16, j, 16, 16);
			j += 16;
			woodshop_player[i] = Bitmap.createBitmap(resource_sheet, i*16, j, 16, 16);;
			j += 16;
			woodshop_enemy[i] = Bitmap.createBitmap(resource_sheet, i*16, j, 16, 16);
			j += 16;
			rockery_player[i] = Bitmap.createBitmap(resource_sheet, i*16, j, 16, 16);
			j += 16;
			rockery_enemy [i] = Bitmap.createBitmap(resource_sheet, i*16, j, 16, 16);
		}
		
		scissor_player = new Bitmap[4];
		scissor_enemy = new Bitmap[4];
		paper_player = new Bitmap[4];
		paper_enemy = new Bitmap[4];
		rock_player = new Bitmap[4];
		rock_enemy = new Bitmap[4];
		for (int i=0; i<4; ++i) {
			int j = 0;
			scissor_player[i] = Bitmap.createBitmap(resource_actors, i*6, j, 6, 6);//clip(resource_actors, i*6, j, 6, 6);
			j += 6;
			scissor_enemy[i] = Bitmap.createBitmap(resource_actors, i*6, j, 6, 6);
			j += 6;
			paper_player[i] = Bitmap.createBitmap(resource_actors, i*6, j, 6, 6);
			j += 6;
			paper_enemy[i] = Bitmap.createBitmap(resource_actors, i*6, j, 6, 6);
			j += 6;
			rock_player[i] = Bitmap.createBitmap(resource_actors, i*6, j, 6, 6);
			j += 6;
			rock_enemy[i] = Bitmap.createBitmap(resource_actors, i*6, j, 6, 6);
		}
		
		proj_scissor_player = new Bitmap[4];
		proj_scissor_enemy = new Bitmap[4];
		proj_paper_player = new Bitmap[4];
		proj_paper_enemy = new Bitmap[4];
		proj_rock_player = new Bitmap[4];
		proj_rock_enemy = new Bitmap[4];
		for (int i=0; i<4; ++i) {
			int j = 0;
			proj_scissor_player[i] = Bitmap.createBitmap(resource_projectile, i*3, j, 3, 3);
			j += 3;
			proj_scissor_enemy[i] = Bitmap.createBitmap(resource_projectile, i*3, j, 3, 3);
			j += 3;
			proj_paper_player[i] = Bitmap.createBitmap(resource_projectile, i*3, j, 3, 3);
			j += 3;
			proj_paper_enemy[i] = Bitmap.createBitmap(resource_projectile, i*3, j, 3, 3);
			j += 3;
			proj_rock_player[i] = Bitmap.createBitmap(resource_projectile, i*3, j, 3, 3);
			j += 3;
			proj_rock_enemy[i] = Bitmap.createBitmap(resource_projectile, i*3, j, 3, 3);
		}

		attractor_paper_enemy = new Bitmap[4];
		attractor_rock_enemy = new Bitmap[4];
		attractor_scissors_enemy = new Bitmap[4];
		attractor_paper_player = new Bitmap[4];
		attractor_rock_player = new Bitmap[4];
		attractor_scissors_player = new Bitmap[4];
		for (int i=0; i<4; ++i) {
			int j = 0;
			attractor_scissors_player[i] = Bitmap.createBitmap(resource_attractor, i*8, j, 8, 23);
			j += 23;
			attractor_scissors_enemy[i] = Bitmap.createBitmap(resource_attractor, i*8, j, 8, 23);
			j += 23;
			attractor_paper_player[i] = Bitmap.createBitmap(resource_attractor, i*8, j, 8, 23);
			j += 23;
			attractor_paper_enemy[i] = Bitmap.createBitmap(resource_attractor, i*8, j, 8, 23);
			j += 23;
			attractor_rock_player[i] = Bitmap.createBitmap(resource_attractor, i*8, j, 8, 23);
			j += 23;
			attractor_rock_enemy[i] = Bitmap.createBitmap(resource_attractor, i*8, j, 8, 23);
		}

        mine = new Bitmap[4];//ImageIO.read( this.getClass().getResource("/resource/mine.gif") );
        stone = new Bitmap[4]; //ImageIO.read( this.getClass().getResource("/resource/stone.gif") );
		cactus = new Bitmap[6];
		tree = new Bitmap[6];
		for (int i=0; i<6; ++i) {
			int j=0;
			tree[i] = Bitmap.createBitmap(resource_trees, i*6, j, 6, 12);
			j += 12;
			cactus[i] = Bitmap.createBitmap(resource_trees, i*6, j, 6, 12);
			if (i >= 4) continue;
			mine[i] = Bitmap.createBitmap(resource_mine, i*6, 0, 6, 6);
			stone[i] = Bitmap.createBitmap(resouce_stone, i*6, 0, 6, 6);
		}
		
	}
}
