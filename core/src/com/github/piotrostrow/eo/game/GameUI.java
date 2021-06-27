package com.github.piotrostrow.eo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.ui.actors.ChatBar;
import com.github.piotrostrow.eo.ui.actors.ChatWindow;
import com.github.piotrostrow.eo.ui.actors.Inventory;

public class GameUI extends Stage {

	private Actor currentComponent;

	private final ChatBar chatBar;

	private final Inventory inventory;
	private final ChatWindow chatWindow;

	public GameUI() {
		super(new ScreenViewport());

		inventory = new Inventory();
		addComponent(inventory);

		chatWindow = new ChatWindow();
		addComponent(chatWindow);

		chatBar = new ChatBar();
		addActor(chatBar);

		Texture buttonAtlasTexture = Assets.gfx(2, 125);
		TextureRegion[][] buttonsTR = TextureRegion.split(buttonAtlasTexture, buttonAtlasTexture.getWidth() / 2, buttonAtlasTexture.getHeight() / 11);

		for(int i = 0; i < 11; i++) {
			final int index = i;

			Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
			buttonStyle.up = new TextureRegionDrawable(buttonsTR[i][0]);
			buttonStyle.over = buttonStyle.down = new TextureRegionDrawable(buttonsTR[i][1]);

			Button button = new Button(buttonStyle);
			button.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					switch(index) {
						case 0: showComponent(inventory); break;
						case 4: showComponent(chatWindow); break;
						default: showComponent(null); break;
					}
				}
			});

			button.setPosition(4, 4 + button.getHeight() * 10 - button.getHeight() * i);
			addActor(button);
		}
	}

	@Override
	public boolean keyDown(int keyCode) {
		boolean handled = super.keyUp(keyCode);

		switch(keyCode){
			case Input.Keys.ENTER:
				chatBar.onEnterPressed();
				return true;
			case Input.Keys.ESCAPE:
				chatBar.onEscapePressed();
				return true;
		}

		if(!handled) {
			switch(keyCode){
				case Input.Keys.B:
				case Input.Keys.I:
					showComponent(inventory);
					break;
				default: return false;
			}
		}

		return true;
	}

	private void addComponent(Actor actor) {
		addActor(actor);
		actor.setVisible(false);
		actor.setPosition(Gdx.graphics.getWidth() / 2 - actor.getWidth() / 2, 32);
	}

	private void showComponent(Actor component) {
		if(currentComponent != null)
			currentComponent.setVisible(false);

		if(currentComponent == component || component == null) {
			currentComponent = null;
		} else {
			component.setVisible(true);
			currentComponent = component;
		}

	}
}