package com.github.piotrostrow.eo.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.net.packets.login.LoginReplyPacket;
import com.github.piotrostrow.eo.net.packets.login.WelcomeReplyPacket2;
import com.github.piotrostrow.eo.shaders.GfxShader;

import java.util.LinkedList;
import java.util.Queue;

public class PlayerTextureAtlasFactory {

	public static PlayerTextureAtlas create(LoginReplyPacket.Character character) {
		return create(character.race, character.gender, character.hairstyle, character.haircolor);
	}

	public static PlayerTextureAtlas create(WelcomeReplyPacket2.Character character) {
		return create(character.race, character.gender, character.hairstyle, character.haircolor);
	}

	public static PlayerTextureAtlas create(int race, int gender, int hairstyle, int haircolor) {
		PlayerTextureAtlasFactory factory = new PlayerTextureAtlasFactory();
		factory.render(race, gender, hairstyle, haircolor);


		return new PlayerTextureAtlas(factory.idle, factory.movement, factory.attack, factory.spell, factory.sitChair, factory.sitGround, factory.attackBow);
	}

	// texture dimensions
	private static final int atlasWidth, atlasHeight;

	private static int regionWidth = 128;
	private static int regionHeight = 128;

	private static final SpriteBatch spriteBatch;
	private static final Matrix4 projection = new Matrix4();

	private static final TextureRegion[][] aIdle;
	private static final TextureRegion[][] aMovement;
	private static final TextureRegion[][] aAttack;
	private static final TextureRegion[][] aSpell;
	private static final TextureRegion[][] aSitChair;
	private static final TextureRegion[][] aSitGround;
	private static final TextureRegion[][] aAttackBow;

	static {
		atlasWidth = atlasHeight = 512 + 256;

		Texture eoIdleSS = Assets.gfx(8, 101);
		Texture eoMovementSS = Assets.gfx(8, 102);
		Texture eoAttackSS = Assets.gfx(8, 103);
		Texture eoSkillSS = Assets.gfx(8, 104);
		Texture eoSitBenchSS = Assets.gfx(8, 105);
		Texture eoSitGroundSS = Assets.gfx(8, 106);
		Texture eoAttackBowSS = Assets.gfx(8, 107);

		aIdle = TextureRegion.split(eoIdleSS, eoIdleSS.getWidth() / 4, eoIdleSS.getHeight() / 7);
		aMovement = TextureRegion.split(eoMovementSS, eoMovementSS.getWidth() / 16, eoMovementSS.getHeight() / 7);
		aAttack = TextureRegion.split(eoAttackSS, eoAttackSS.getWidth() / 8, eoAttackSS.getHeight() / 7);
		aSpell = TextureRegion.split(eoSkillSS, eoSkillSS.getWidth() / 4, eoSkillSS.getHeight() / 7);
		aSitChair = TextureRegion.split(eoSitBenchSS, eoSitBenchSS.getWidth() / 4, eoSitBenchSS.getHeight() / 7);
		aSitGround = TextureRegion.split(eoSitGroundSS, eoSitGroundSS.getWidth() / 4, eoSitGroundSS.getHeight() / 7);
		aAttackBow = TextureRegion.split(eoAttackBowSS, eoAttackBowSS.getWidth() / 4, eoAttackBowSS.getHeight() / 7);

		spriteBatch = new SpriteBatch();
		spriteBatch.setShader(new GfxShader());
	}

	private final CustomFrameBuffer frameBuffer;

	private final TextureRegion[] idle = new TextureRegion[4];
	private final TextureRegion[] movement = new TextureRegion[16];
	private final TextureRegion[] attack = new TextureRegion[8];
	private final TextureRegion[] spell = new TextureRegion[4];
	private final TextureRegion[] sitChair = new TextureRegion[4];
	private final TextureRegion[] sitGround = new TextureRegion[4];
	private final TextureRegion[] attackBow = new TextureRegion[4];

	private PlayerTextureAtlasFactory() {
		frameBuffer = new CustomFrameBuffer(Pixmap.Format.RGBA8888, atlasWidth, atlasHeight, true);
		Texture texture = frameBuffer.getColorBufferTexture();
		texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

		Queue<TextureRegion> queue = new LinkedList<>();
		for (int y = 0; y + regionHeight <= atlasHeight; y += regionHeight) {
			for (int x = 0; x + regionWidth <= atlasWidth; x += regionWidth) {
				queue.add(new TextureRegion(texture, x, y, regionWidth, regionHeight));
			}
		}

		// set texture regions
		setRegions(idle, queue);
		setRegions(movement, queue);
		setRegions(attack, queue);
		setRegions(spell, queue);
		setRegions(sitChair, queue);
		setRegions(sitGround, queue);
		setRegions(attackBow, queue);
	}

	private void setRegions(TextureRegion[] regions, Queue<TextureRegion> queue) {
		for (int i = 0; i < regions.length / 2; i++)
			regions[i] = queue.remove();
	}

	private void fix(TextureRegion[] regions) {
		// flip v (y coordinate) on all texture regions
		for(int i = 0; i < regions.length / 2; i++)
			regions[i].setRegion(regions[i].getRegionX(), atlasHeight - regions[i].getRegionY() - regions[i].getRegionHeight(), regionWidth, regionHeight);

		// set mirroring texture regions in the correct order
		for(int i = 0; i < regions.length / 4; i++){
			TextureRegion tr1 = new TextureRegion(regions[i]);
			tr1.flip(true, false);
			regions[regions.length - regions.length / 4 + i] = tr1;

			TextureRegion tr2 = new TextureRegion(regions[regions.length / 4 + i]);
			tr2.flip(true, false);
			regions[regions.length / 2 + i] = tr2;
		}
	}

	private void render(int race, int gender, int hairstyle, int haircolor) {
		frameBuffer.begin();
		spriteBatch.begin();
		projection.setToOrtho(0, atlasWidth, atlasHeight, 0, 0, 100);
		spriteBatch.setProjectionMatrix(projection);

		drawHairBottom(gender, hairstyle, haircolor);

		drawCharacter(race, gender);

		drawHairTop(gender, hairstyle, haircolor);

		spriteBatch.end();

		ShapeRenderer shapeRenderer = new ShapeRenderer();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setProjectionMatrix(projection);

//		for (int x = 0; x < atlasWidth; x += regionWidth)
//			shapeRenderer.line(x, 0, x, atlasHeight);
//
//		for (int y = 0; y < atlasHeight; y += regionHeight)
//			shapeRenderer.line(0, y, atlasWidth, y);

		shapeRenderer.end();
		shapeRenderer.dispose();

		frameBuffer.end();
		frameBuffer.dispose();

		// fix texture region coordinates
		fix(idle);
		fix(movement);
		fix(attack);
		fix(spell);
		fix(sitChair);
		fix(sitGround);
		fix(attackBow);
	}

	private void drawHairBottom(int gender, int hairstyle, int haircolor) {
		int hair = 101 + (hairstyle - 1) * 40 + haircolor * 4;
		if (gender == 0) {
			Texture hair1 = Assets.gfx(10, hair);
			Texture hair2 = Assets.gfx(10, hair + 2);
			drawHair(gender, hair1, hair2);
		} else {
			Texture hair1 = Assets.gfx(9, hair);
			Texture hair2 = Assets.gfx(9, hair + 2);
			drawHair(gender, hair1, hair2);
		}
	}

	private void drawHairTop(int gender, int hairstyle, int haircolor) {
		int hair = 101 + (hairstyle - 1) * 40 + haircolor * 4;
		if (gender == 0) {
			Texture hair1 = Assets.gfx(10, hair + 1);
			Texture hair2 = Assets.gfx(10, hair + 3);
			drawHair(gender, hair1, hair2);
		} else {
			Texture hair1 = Assets.gfx(9, hair + 1);
			Texture hair2 = Assets.gfx(9, hair + 3);
			drawHair(gender, hair1, hair2);
		}
	}

	private void drawHair(Texture hair, TextureRegion[][] src, TextureRegion dst, int xOffset, int yOffset) {
		xOffset += regionWidth / 2 - src[0][0].getRegionWidth() / 2;
		yOffset += regionHeight / 2 - src[0][0].getRegionHeight() / 2;
		spriteBatch.draw(hair, dst.getRegionX() + xOffset, dst.getRegionY() + yOffset);
	}

	private void drawHair(int gender, Texture hair1, Texture hair2) {
		if(gender == 1) {
			drawHair(hair1, aIdle, idle[0], -6, 17);
			drawHair(hair2, aIdle, idle[1], -6, 17);

			for(int i = 0; i < 4; i++) // move down
				drawHair(hair1, aMovement, movement[i], -1, 19);

			for(int i = 4; i < 8; i++) // move left
				drawHair(hair2, aMovement, movement[i], -2, 19);

			drawHair(hair1, aAttack, attack[0], -1, 17);
			drawHair(hair1, aAttack, attack[1], -7, 13);

			drawHair(hair2, aAttack, attack[2], -1, 17);
			drawHair(hair2, aAttack, attack[3], -5, 16);

			drawHair(hair1, aSpell ,spell[0], -6, 17);
			drawHair(hair2, aSpell, spell[1], -6, 17);

			drawHair(hair1, aSitChair, sitChair[0], 0, 11);
			drawHair(hair2, aSitChair, sitChair[1], 0, 11);

			drawHair(hair1, aSitGround, sitGround[0], 0, 2);
			drawHair(hair2, aSitGround, sitGround[1], 2, 2);

			drawHair(hair1, aAttackBow, attackBow[0], 2, 17);
			drawHair(hair2, aAttackBow, attackBow[1], 0, 17);
		} else {
			drawHair(hair1, aIdle, idle[0], -6, 16);
			drawHair(hair2, aIdle, idle[1], -6, 16);

			for(int i = 0; i < 4; i++) // move down
				drawHair(hair1, aMovement, movement[i], -2, 18);

			for(int i = 4; i < 8; i++) // move left
				drawHair(hair2, aMovement, movement[i], -2, 18);

			drawHair(hair1, aAttack, attack[0], -2, 16);
			drawHair(hair1, aAttack, attack[1], -6, 11);

			drawHair(hair2, aAttack, attack[2], -2, 16);
			drawHair(hair2, aAttack, attack[3], -6, 15);

			drawHair(hair1, aSpell ,spell[0], -6, 16);
			drawHair(hair2, aSpell, spell[1], -6, 16);

			drawHair(hair1, aSitChair, sitChair[0], -1, 12);
			drawHair(hair2, aSitChair, sitChair[1], 0, 12);

			drawHair(hair1, aSitGround, sitGround[0], -1, 3);
			drawHair(hair2, aSitGround, sitGround[1], 1, 3);

			drawHair(hair1, aAttackBow, attackBow[0], 3, 17);
			drawHair(hair2, aAttackBow, attackBow[1], 1, 16);
		}
	}

	private void drawCharacter(TextureRegion[][] src, TextureRegion[] dst, int race, int gender) {
		int midX = regionWidth / 2 - src[0][0].getRegionWidth() / 2;
		int midY = regionHeight / 2 - src[0][0].getRegionHeight() / 2;
		int w = src[race].length / 2 * gender;

		for (int i = 0; i < dst.length / 2; i++)
			spriteBatch.draw(src[race][w + i], dst[i].getRegionX() + midX, dst[i].getRegionY() + midY);
	}

	private void drawCharacter(int race, int gender) {
		drawCharacter(aIdle, idle, race, gender);
		drawCharacter(aMovement, movement, race, gender);
		drawCharacter(aAttack, attack, race, gender);
		drawCharacter(aSpell, spell, race, gender);
		drawCharacter(aSitChair, sitChair, race, gender);
		drawCharacter(aSitGround, sitGround, race, gender);
		drawCharacter(aSitGround, sitGround, race, gender);
		drawCharacter(aAttackBow, attackBow, race, gender);
	}
}
