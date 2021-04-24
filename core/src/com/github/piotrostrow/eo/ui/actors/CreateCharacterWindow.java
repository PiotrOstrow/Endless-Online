package com.github.piotrostrow.eo.ui.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.github.piotrostrow.eo.Main;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.character.CharacterState;
import com.github.piotrostrow.eo.character.Direction;
import com.github.piotrostrow.eo.graphics.PlayerTextureAtlas;
import com.github.piotrostrow.eo.graphics.PlayerTextureAtlasFactory;
import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.packets.character.CharacterCreatePacket;

public class CreateCharacterWindow extends WidgetGroup implements Disposable {

	private int gender;
	private int hairStyle;
	private int hairColor;
	private int race;

	private PlayerTextureAtlas characterAtlas;
	private final Image characterImage;

	public CreateCharacterWindow() {
		Texture backgroundTexture = Assets.gfx(1, 120);
		Texture buttons = Assets.gfx(1, 115);
		Texture iconTexture = Assets.gfx(1, 122);

		TextureRegion[][] buttonsTR = TextureRegion.split(buttons, buttons.getWidth() / 2, buttons.getHeight() / 10);
		TextureRegion[][] icons = TextureRegion.split(iconTexture, iconTexture.getWidth() / 20, iconTexture.getHeight() / 3);

		TextureRegion arrowUp = new TextureRegion(iconTexture, 184, 38, 21, 19);
		TextureRegion arrowDown = new TextureRegion(iconTexture, 205, 38, 21, 19);

		TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
		textFieldStyle.font = Assets.getFont("fonts/ms_ss/14.fnt");
		textFieldStyle.fontColor = Color.valueOf("#dcc8b4");
		textFieldStyle.cursor = Assets.getTextCursor(textFieldStyle.font, Color.valueOf("#7d5321"));
		textFieldStyle.selection = Assets.getTextSelector(textFieldStyle.font, Color.valueOf("#7d5321"));

		Button.ButtonStyle okButtonStyle = new Button.ButtonStyle();
		okButtonStyle.up = new TextureRegionDrawable(buttonsTR[4][0]);
		okButtonStyle.down = okButtonStyle.over = new TextureRegionDrawable(buttonsTR[4][1]);

		Button.ButtonStyle cancelButtonStyle = new Button.ButtonStyle();
		cancelButtonStyle.up = new TextureRegionDrawable(buttonsTR[1][0]);
		cancelButtonStyle.down = cancelButtonStyle.over = new TextureRegionDrawable(buttonsTR[1][1]);

		Button.ButtonStyle genderButtonStyle = new Button.ButtonStyle();
		genderButtonStyle.up = new DoubleTRDrawable(icons[2][gender], arrowUp, 2, 0);
		genderButtonStyle.down = genderButtonStyle.over = new DoubleTRDrawable(icons[2][gender], arrowDown, 2, 0);

		Button.ButtonStyle hairStyleButtonStyle = new Button.ButtonStyle();
		hairStyleButtonStyle.up = new DoubleTRDrawable(icons[1][hairStyle], arrowUp, 2, -1);
		hairStyleButtonStyle.down = hairStyleButtonStyle.over = new DoubleTRDrawable(icons[1][hairStyle], arrowDown, 2, -1);

		Button.ButtonStyle hairColorButtonStyle = new Button.ButtonStyle();
		hairColorButtonStyle.up = new DoubleTRDrawable(icons[0][hairColor], arrowUp, 2, -1);
		hairColorButtonStyle.down = hairColorButtonStyle.over = new DoubleTRDrawable(icons[0][hairColor], arrowDown, 2, -1);

		Button.ButtonStyle raceButtonStyle = new Button.ButtonStyle();
		raceButtonStyle.up = new DoubleTRDrawable(icons[2][race + 2], arrowUp, 2, -1);
		raceButtonStyle.down = raceButtonStyle.over = new DoubleTRDrawable(icons[2][race + 2], arrowDown, 2, -1);

		Image backgroundImage = new Image(backgroundTexture);

		characterImage = new Image(iconTexture);
		characterImage.setPosition(220, 50);

		TextField nameTextField = new TextField("", textFieldStyle);
		nameTextField.setPosition(89, 168);
		nameTextField.setMaxLength(12);

		Button cancelButton = new Button(cancelButtonStyle);
		cancelButton.setPosition(250, 16);

		Button okButton = new Button(okButtonStyle);
		okButton.setPosition(157, 16);

		Button genderButton = new Button(genderButtonStyle);
		genderButton.setPosition(170, 137);

		Button hairStyleButton = new Button(hairStyleButtonStyle);
		hairStyleButton.setPosition(170, 110);

		Button hairColorButton = new Button(hairColorButtonStyle);
		hairColorButton.setPosition(170, 83);

		Button raceButton = new Button(raceButtonStyle);
		raceButton.setPosition(170, 56);

		WidgetGroup inner = new WidgetGroup();
		inner.addActor(backgroundImage);
		inner.addActor(characterImage);
		inner.addActor(genderButton);
		inner.addActor(hairStyleButton);
		inner.addActor(hairColorButton);
		inner.addActor(raceButton);
		inner.addActor(nameTextField);
		inner.addActor(cancelButton);
		inner.addActor(okButton);

		inner.setPosition(Gdx.graphics.getWidth() / 2 - Math.round(backgroundImage.getWidth() / 2), Gdx.graphics.getHeight() / 2 - Math.round(backgroundImage.getHeight() / 2));

		addActor(inner);
		setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		ClickListener arrowClickListener = new ClickListener(-1) {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(event.getTarget() == genderButton) {
					gender = (gender + 1) % 2;
					((DoubleTRDrawable)genderButtonStyle.up).first = icons[2][gender];
					((DoubleTRDrawable)genderButtonStyle.down).first = icons[2][gender];
				} else if(event.getTarget() == hairStyleButton) {
					if(event.getButton() == Input.Buttons.LEFT)
						hairStyle = (hairStyle + 1) % 20;
					if(event.getButton() == Input.Buttons.RIGHT)
						hairStyle = (20 + hairStyle - 1) % 20;
					((DoubleTRDrawable)hairStyleButtonStyle.up).first = icons[1][hairStyle];
					((DoubleTRDrawable)hairStyleButtonStyle.down).first = icons[1][hairStyle];
				} else if(event.getTarget() == hairColorButton) {
					if(event.getButton() == Input.Buttons.LEFT)
						hairColor = (hairColor + 1) % 9;
					else if(event.getButton() == Input.Buttons.RIGHT)
						hairColor = (9 + hairColor - 1) % 9;
					((DoubleTRDrawable)hairColorButtonStyle.up).first = icons[0][hairColor];
					((DoubleTRDrawable)hairColorButtonStyle.down).first = icons[0][hairColor];
				} else { // race
					if(event.getButton() == Input.Buttons.LEFT)
						race = (race + 1) % 6;
					else if(event.getButton() == Input.Buttons.RIGHT)
						race = (6 + race - 1) % 6;
					((DoubleTRDrawable)raceButtonStyle.up).first = icons[2][race + 2];
					((DoubleTRDrawable)raceButtonStyle.down).first = icons[2][race + 2];
				}

				if(event.getButton() == Input.Buttons.LEFT || event.getButton() == Input.Buttons.RIGHT)
					renderCharacterImage();
			}
		};

		genderButton.addListener(arrowClickListener);
		hairStyleButton.addListener(arrowClickListener);
		hairColorButton.addListener(arrowClickListener);
		raceButton.addListener(arrowClickListener);

		cancelButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				CreateCharacterWindow.this.remove();
			}
		});

		okButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Packet packet = new CharacterCreatePacket(nameTextField.getText(), gender, hairStyle + 1, hairColor, race);
				Main.client.sendEncodedPacket(packet);
			}
		});

		renderCharacterImage();
	}

	private void renderCharacterImage() {
		if(characterAtlas != null)
			characterAtlas.dispose();

		characterAtlas = PlayerTextureAtlasFactory.create(race, gender, hairStyle + 1, hairColor, 0, 0);
		characterImage.setDrawable(new TextureRegionDrawable(characterAtlas.get(CharacterState.IDLE, Direction.DOWN, 0)));
		characterImage.pack();
	}

	@Override
	public void dispose() {
		if(characterAtlas != null)
			characterAtlas.dispose();
	}

	private static class DoubleTRDrawable extends BaseDrawable {

		TextureRegion first;
		TextureRegion second;

		final float xOffset;
		final float yOffset;

		public DoubleTRDrawable(TextureRegion first, TextureRegion second, float xOffset, float yOffset) {
			this.first = first;
			this.second = second;
			this.xOffset = xOffset;
			this.yOffset = yOffset;
			setMinWidth(first.getRegionWidth() + second.getRegionWidth() + xOffset);
			setMinHeight(Math.max(first.getRegionHeight(), second.getRegionHeight()));
		}

		@Override
		public void draw(Batch batch, float x, float y, float width, float height) {
			batch.draw(first, x, y);
			batch.draw(second, x + first.getRegionWidth() + xOffset, y + yOffset);
		}
	}
}
