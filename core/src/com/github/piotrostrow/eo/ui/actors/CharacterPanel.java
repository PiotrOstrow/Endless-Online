package com.github.piotrostrow.eo.ui.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import com.github.piotrostrow.eo.character.CharacterState;
import com.github.piotrostrow.eo.character.Direction;
import com.github.piotrostrow.eo.graphics.PlayerTextureAtlas;
import com.github.piotrostrow.eo.graphics.PlayerTextureAtlasFactory;
import com.github.piotrostrow.eo.net.packets.login.LoginReplyPacket;

/**
 * Character panel in CharacterSelectStage
 */
public class CharacterPanel extends WidgetGroup implements Disposable {

	private final Image characterImage;
	public final Button loginButton;
	public final Button deleteButton;
	private final Label nameLabel;

	private LoginReplyPacket.Character characterData;

	private PlayerTextureAtlas playerTextureAtlas;

	private TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable();

	public CharacterPanel(Texture characterPanelTexture, Label.LabelStyle labelStyle, Button.ButtonStyle loginButtonStyle, Button.ButtonStyle deleteButtonStyle) {
		Image background = new Image(characterPanelTexture);
		background.setPosition(0, 0);
		addActor(background);
		setSize(characterPanelTexture.getWidth(), characterPanelTexture.getHeight());

		nameLabel = new Label("", labelStyle);
		nameLabel.setSize(89, 22);
		nameLabel.setPosition(162, 75);
		nameLabel.setAlignment(Align.center);
		nameLabel.setWrap(false);
		addActor(nameLabel);

		loginButton = new Button(loginButtonStyle);
		loginButton.setPosition(161 , 38);
		addActor(loginButton);

		deleteButton = new Button(deleteButtonStyle);
		deleteButton.setPosition(161, 10);
		addActor(deleteButton);

		characterImage = new Image();
		characterImage.setSize(100, 106);
		characterImage.setPosition(27, 9);
		characterImage.setScaling(Scaling.none);
		addActor(characterImage);

		addListener(new ClickListener(){
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				super.enter(event, x, y, pointer, fromActor);
				if(playerTextureAtlas != null)
					textureRegionDrawable.setRegion(playerTextureAtlas.get(CharacterState.IDLE, Direction.DOWN, 0));
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				super.exit(event, x, y, pointer, toActor);
				if(!isOver() && playerTextureAtlas != null)
					textureRegionDrawable.setRegion(playerTextureAtlas.get(CharacterState.SIT_GROUND, Direction.DOWN, 0));
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				if(!isOver() && playerTextureAtlas != null)
					textureRegionDrawable.setRegion(playerTextureAtlas.get(CharacterState.SIT_GROUND, Direction.DOWN, 0));
			}
		});
	}

	public void reset() {
		characterImage.setDrawable(null);
		setNameLabel("");
		characterData = null;
	}

	public void setCharacter(LoginReplyPacket.Character characterData) {
		this.characterData = characterData;
		setNameLabel(characterData.name);

		if(playerTextureAtlas != null)
			playerTextureAtlas.dispose();

		playerTextureAtlas = PlayerTextureAtlasFactory.create(characterData);
		// TODO: after implementing enter in login window, check what happens when mouse is already over the panel
		textureRegionDrawable.setRegion(playerTextureAtlas.get(CharacterState.SIT_GROUND, Direction.DOWN, 0));
		characterImage.setDrawable(textureRegionDrawable);
	}

	private void setNameLabel(String name) {
		if (name.length() > 14) {
			String f = name.substring(0, 12) + "...";
			nameLabel.setText(f);
		} else {
			nameLabel.setText(name);
		}
	}

	public boolean hasCharacter() {
		return characterData != null;
	}

	public int getCharacterID() {
		if(characterData == null)
			return 0;
		return characterData.id;
	}

	@Override
	public void dispose() {
		if(playerTextureAtlas != null) {
			characterImage.setDrawable(null);
			playerTextureAtlas.dispose();
		}
	}
}