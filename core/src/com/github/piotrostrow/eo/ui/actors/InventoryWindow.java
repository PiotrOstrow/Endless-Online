package com.github.piotrostrow.eo.ui.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.game.GameScreen;

public class InventoryWindow extends WidgetGroup {

	private final GameScreen gameScreen;

	public InventoryWindow(GameScreen gameScreen, PaperdollWindow paperdollWindow) {
		this.gameScreen = gameScreen;

		Texture backgroundTexture = Assets.gfx(2, 144);
		Texture atlas = Assets.gfx(2, 127);

		TextureRegion cellDefaultTextureRegion = new TextureRegion(atlas, 0, 163, 23, 23);
		TextureRegion cellHoverTexutreRegion = new TextureRegion(atlas, 0, 186, 23, 23);
		TextureRegion cellDisabledTextureRegion = new TextureRegion(atlas, 0, 209, 23, 23);

		Button.ButtonStyle paperdollButtonStyle = new Button.ButtonStyle();
		paperdollButtonStyle.up = new TextureRegionDrawable(new TextureRegion(atlas, 38, 385, 88, 19));
		paperdollButtonStyle.down = paperdollButtonStyle.over = new TextureRegionDrawable(new TextureRegion(atlas, 126, 385, 88, 19));

		Button.ButtonStyle dropButtonStyle = new Button.ButtonStyle();
		dropButtonStyle.up = new TextureRegionDrawable(new TextureRegion(atlas, 0, 15, 38, 37));
		dropButtonStyle.down = dropButtonStyle.over = new TextureRegionDrawable(new TextureRegion(atlas, 0, 52, 38, 37));

		Button.ButtonStyle junkButtonStyle = new Button.ButtonStyle();
		junkButtonStyle.up = new TextureRegionDrawable(new TextureRegion(atlas, 0, 89, 38, 37));
		junkButtonStyle.down = junkButtonStyle.over = new TextureRegionDrawable(new TextureRegion(atlas, 0, 126, 38, 37));

		Button paperdollButton = new Button(paperdollButtonStyle);
		paperdollButton.setPosition(385, 91);

		Button dropButton = new Button(dropButtonStyle);
		dropButton.setPosition(389, 14);

		Button junkButton = new Button(junkButtonStyle);
		junkButton.setPosition(431, 14);

		Image backgroundImage = new Image(backgroundTexture);
		addActor(backgroundImage);

		setSize(backgroundTexture.getWidth(), backgroundImage.getHeight());

		Table table = new Table();
		table.setPosition(13, 6);

		for(int y = 0; y < 4; y++) {
			for(int x = 0; x < 14; x++) {
				Image image = new Image(cellDefaultTextureRegion);
				table.add(image).width(23).height(23).padRight(3).padBottom(3);

				image.addListener(new ClickListener() {
					@Override
					public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
						super.enter(event, x, y, pointer, fromActor);
						image.setDrawable(new TextureRegionDrawable(cellHoverTexutreRegion));
					}

					@Override
					public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
						super.exit(event, x, y, pointer, toActor);
						image.setDrawable(new TextureRegionDrawable(cellDefaultTextureRegion));
					}
				});
			}
			table.row();
		}

		table.layout();
		table.pack();
		addActor(table);

		addActor(paperdollButton);
		addActor(dropButton);
		addActor(junkButton);

		paperdollButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				paperdollWindow.setVisible(!paperdollWindow.isVisible());
			}
		});
	}
}
