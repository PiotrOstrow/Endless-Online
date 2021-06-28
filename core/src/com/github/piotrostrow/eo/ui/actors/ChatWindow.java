package com.github.piotrostrow.eo.ui.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.github.piotrostrow.eo.assets.Assets;

public class ChatWindow extends WidgetGroup {

	private final Image backgroundImage;

	private final ScrollPane scrollPane;
	private final Table table;

	private final Label.LabelStyle blackLabelStyle;

	private final TextureRegion[] icons;

	public ChatWindow() {
		Texture backgroundTexture = Assets.gfx(2, 128);

		Texture iconsTexture = Assets.gfx(2, 132);
		TextureRegion[][] icons2d = TextureRegion.split(iconsTexture, iconsTexture.getWidth(), iconsTexture.getHeight() / 24);

		icons = new TextureRegion[icons2d.length];
		for(int i = 0; i < icons.length; i++)
			icons[i] = icons2d[i][0];

		blackLabelStyle = new Label.LabelStyle();
		blackLabelStyle.font = Assets.getFont("fonts/ms_ss/14.fnt");
		blackLabelStyle.fontColor = Color.BLACK;

		backgroundImage = new Image(backgroundTexture);
		addActor(backgroundImage);

		table = new Table();
		table.setSize(backgroundImage.getWidth() - 30, backgroundImage.getHeight() - 24);

		scrollPane = new ScrollPane(table);
		scrollPane.setBounds(6, 22, table.getWidth(), table.getHeight());
		scrollPane.setScrollingDisabled(true, false);
		scrollPane.setOverscroll(false, false);

		addActor(scrollPane);

		setSize(backgroundTexture.getWidth(), backgroundImage.getHeight());
	}

	public void addMessage(String username, String message) {
		addMessage(username, message, 0);
	}

	public void addMessage(String username, String message, int iconIndex) {
		Label nameLabel = new Label(username, blackLabelStyle);
		Label messageLabel = new Label(message, blackLabelStyle);

		messageLabel.setWrap(true);

		Table innerTable = new Table();

		if(iconIndex >= 0) {
			Image icon = new Image(icons[iconIndex]);
			innerTable.add(icon).align(Align.topLeft).padRight(3);
		} else {
			innerTable.padLeft(3 + icons[0].getRegionWidth());
		}

		innerTable.add(nameLabel).padRight(7).align(Align.topLeft);
		// padded on the right cus it'd cut letters, the amount or the size of the table could be adjusted for this
		innerTable.add(messageLabel).growX().align(Align.topLeft).padRight(10);

		table.add(innerTable).align(Align.topLeft).growX().spaceBottom(1).row();
		table.align(Align.topLeft);
		table.layout();

		scrollPane.layout();
		scrollPane.setScrollY(Float.MAX_VALUE);
	}
}
