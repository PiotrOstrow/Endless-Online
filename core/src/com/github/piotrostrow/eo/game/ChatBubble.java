package com.github.piotrostrow.eo.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.graphics.ChatBubblePatch;

public class ChatBubble {

	private static final int MIN_VISIBLE_TIME = 2000;
	private static final int MAX_VISIBLE_TIME = 10000;

	private final Label label;

	private long shownAt;
	private long duration;

	public ChatBubble() {
		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = Assets.getFont("fonts/ms_ss/14.fnt");
		labelStyle.fontColor = Color.valueOf("#101010");

		labelStyle.background = new ChatBubblePatch();
		label = new Label("", labelStyle) {
			@Override
			public float getMaxWidth() {
				return 158;
			}

			@Override
			public float getPrefWidth() {
				setWrap(false);
				float width = super.getPrefWidth();
				setWrap(true);
				return Math.min(getMaxWidth(), width);
			}
		};

		label.setAlignment(Align.top);
		label.setWrap(true);
	}

	public void show(String message) {
		shownAt = System.currentTimeMillis();
		duration = Math.min(MAX_VISIBLE_TIME, MIN_VISIBLE_TIME + message.length() * 65);

		label.setText(message);

		// packing twice makes sizing work correctly for whatever reason
		label.pack();
		label.pack();
	}

	public boolean isVisible() {
		return shownAt + duration > System.currentTimeMillis();
	}

	public void render(Batch batch, int x, int y) {
		label.setPosition(x + 32 - Math.round(label.getWidth() / 2), y);
		label.draw(batch, 1.0f);
	}
}
