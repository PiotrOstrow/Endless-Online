package com.github.piotrostrow.eo.ui.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.piotrostrow.eo.Main;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.packets.login.WelcomeRequestPacket;
import com.github.piotrostrow.eo.net.structs.LoginScreenCharacterData;
import com.github.piotrostrow.eo.shaders.GfxShader;
import com.github.piotrostrow.eo.ui.actors.CharacterPanel;

public class CharacterSelectStage extends Stage {

	private CharacterPanel[] characterPanels = new CharacterPanel[3];
	public final Button createButton, cancelButton;

//	private CreateCharacterWindow createCharacterWindow;
	private WidgetGroup ccwContainer;

	private final Table table; // container

	public CharacterSelectStage(){
		super(new ScreenViewport());
		getBatch().setShader(new GfxShader());

		Texture characterPanelTexture = Assets.gfx(1, 111);
		Texture buttons1 = Assets.gfx(1, 114);
		Texture buttons2 = Assets.gfx(1, 115);
		TextureRegion[][] buttons1TR = TextureRegion.split(buttons1, buttons1.getWidth() / 2, buttons1.getHeight() / 4);
		TextureRegion[][] buttons2TR = TextureRegion.split(buttons2, buttons2.getWidth() / 2, buttons2.getHeight() / 10);

		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = Assets.getFont("fonts/Arial/14.fnt");
		labelStyle.fontColor = Color.WHITE;

		Button.ButtonStyle createButtonStyle = new Button.ButtonStyle();
		createButtonStyle.up = new TextureRegionDrawable(buttons1TR[0][0]);
		createButtonStyle.down = createButtonStyle.over = new TextureRegionDrawable(buttons1TR[0][1]);

		Button.ButtonStyle cancelButtonStyle = new Button.ButtonStyle();
		cancelButtonStyle.up = new TextureRegionDrawable(buttons1TR[1][0]);
		cancelButtonStyle.down = cancelButtonStyle.over = new TextureRegionDrawable(buttons1TR[1][1]);

		Button.ButtonStyle loginButtonStyle = new Button.ButtonStyle();
		loginButtonStyle.up = new TextureRegionDrawable(buttons2TR[2][0]);
		loginButtonStyle.down = loginButtonStyle.over = new TextureRegionDrawable(buttons2TR[2][1]);

		Button.ButtonStyle deleteButtonStyle = new Button.ButtonStyle();
		deleteButtonStyle.up = new TextureRegionDrawable(buttons2TR[3][0]);
		deleteButtonStyle.down = deleteButtonStyle.over = new TextureRegionDrawable(buttons2TR[3][1]);

		table = new Table();
		table.setWidth(characterPanelTexture.getWidth());
		addActor(table);

		for(int i = 0; i < characterPanels.length; i++){
			final CharacterPanel characterPanel = characterPanels[i] = new CharacterPanel(characterPanelTexture, labelStyle, loginButtonStyle, deleteButtonStyle);

			ClickListener panelClickListener = new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if(characterPanel.hasCharacter()) {
						int characterID = characterPanel.getCharacterID();
						if (event.getTarget() == characterPanel.loginButton) {
							Packet packet = new WelcomeRequestPacket(characterID);
							Main.client.sendEncodedPacket(packet);
						} else {
							//TODO: delete dialog
						}
					}
				}
			};

			characterPanels[i].loginButton.addListener(panelClickListener);
			characterPanels[i].deleteButton.addListener(panelClickListener);

			table.add(characterPanels[i]).size(characterPanelTexture.getWidth(), characterPanelTexture.getHeight()).spaceBottom(8);
			table.row();
		}

		table.pack();

		ClickListener buttonClickListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(event.getTarget() == cancelButton) {
					Main.client.disconnect();
				} else {
					//TODO: create dialog
				}
			}
		};

		createButton = new Button(createButtonStyle);
		createButton.addListener(buttonClickListener);
		addActor(createButton);

		cancelButton = new Button(cancelButtonStyle);
		cancelButton.addListener(buttonClickListener);
		addActor(cancelButton);

		table.setY(50 + createButton.getHeight() + 10);

		ccwContainer = new WidgetGroup();
		ccwContainer.setVisible(false);

		setActorPositions();
	}

	// TODO: temporary
	public void setCharacters(LoginScreenCharacterData[] characters) {
		for(CharacterPanel characterPanel : characterPanels)
			characterPanel.reset();


		for(int i = 0; i < characters.length && i < characterPanels.length; i++){
			characterPanels[i].setCharacter(characters[i]);
		}
	}

	private void setActorPositions(){
		table.setX(Gdx.graphics.getWidth() - table.getWidth() - 50);

		createButton.setPosition(table.getX(), 50);
		cancelButton.setPosition(table.getX() + createButton.getWidth(), 50);

		ccwContainer.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void resized(int width, int height) {
		getViewport().update(width, height, true);
		setActorPositions();
	}

	public void hideCreateCharacterWindow(){
		ccwContainer.setVisible(false);
	}

	@Override
	public void dispose() {
		// batch was set to use a custom shader that we have to dispose
		getBatch().getShader().dispose();
		super.dispose();
	}
}
