package com.github.piotrostrow.eo.ui.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.piotrostrow.eo.assets.Assets;

public class Dialog extends WidgetGroup {

	public enum EoButton {
		OK, CANCEL
	}

	private final static Dialog instance = new Dialog();

	private static void displayCommon(String title, String message, Stage stage) {
		instance.remove();
		instance.titleLabel.setText(title);
		instance.messageLabel.setText(message);

		instance.messageLabel.setWrap(true);
		instance.messageLabel.setWidth(249);
		instance.messageLabel.setDebug(false);
		instance.messageLabel.pack();
		instance.messageLabel.setWidth(249);
		instance.messageLabel.setPosition(21, 64 - (instance.messageLabel.getHeight() - 32));

		stage.addActor(instance);
	}

	public static void display(String title, String message, /*EoButton button,*/ Stage stage) {
		displayCommon(title, message, stage);
		instance.showProgressBar(false);

		instance.button.setStyle(instance.okButtonStyle);

//		switch(button) {
//			case OK: instance.button.setStyle(instance.okButtonStyle); break;
//			case CANCEL: instance.button.setStyle(instance.cancelButtonStyle); break;
//		}
	}

	public static void displayWithProgressBar(String title, String message, long time, Stage stage, Callback callback) {
		displayCommon(title, message, stage);
		instance.callback = callback;
		instance.startTime = System.currentTimeMillis();
		instance.time = time;
		instance.button.setStyle(instance.okButtonStyle);
		instance.showProgressBar(true);
	}

	private Callback callback;

	private final Label titleLabel;
	private final Label messageLabel;

	private final Button button;
	private final Button.ButtonStyle cancelButtonStyle;
	private final Button.ButtonStyle okButtonStyle;

	private final Image progressBarBackgroundImage;
	private final ProgressBar progressBar;

	private long startTime;
	private long time;

	private Dialog() {
		Texture backgroundTexture = Assets.gfx(1, 118);
		Texture progressBarTexture = Assets.gfx(1, 119);

		Texture buttons = Assets.gfx(1, 115);
		TextureRegion[][] buttonsTR = TextureRegion.split(buttons, buttons.getWidth() / 2, buttons.getHeight() / 10);

		Pixmap progressBarFillPixmap = new Pixmap(1, 8, Pixmap.Format.RGBA8888);
		progressBarFillPixmap.setColor(Color.valueOf("#b4dce6"));
		progressBarFillPixmap.fill();
		Texture progressBarFill = new Texture(progressBarFillPixmap);

		cancelButtonStyle = new Button.ButtonStyle();
		cancelButtonStyle.up = new TextureRegionDrawable(buttonsTR[1][0]);
		cancelButtonStyle.down = cancelButtonStyle.over = new TextureRegionDrawable(buttonsTR[1][1]);

		okButtonStyle = new Button.ButtonStyle();
		okButtonStyle.up = new TextureRegionDrawable(buttonsTR[4][0]);
		okButtonStyle.down = okButtonStyle.over = new TextureRegionDrawable(buttonsTR[4][1]);

		ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle();
		progressBarStyle.knob = progressBarStyle.knobBefore = new TextureRegionDrawable(progressBarFill);

		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = Assets.getFont("fonts/ms_ss/16.fnt");
		labelStyle.fontColor = Color.valueOf("#f0f0c8");

		titleLabel = new Label("", labelStyle);
		titleLabel.setPosition(60, 123);

		// position is set when setting the label text
		messageLabel = new Label("", labelStyle);

		button = new Button(cancelButtonStyle);
		button.setPosition(181, 15);

		Image backgroundImage = new Image(backgroundTexture);

		int progressBarX = 14;
		int progressBarY = 54;

		progressBarBackgroundImage = new Image(progressBarTexture);
		progressBarBackgroundImage.setPosition(progressBarX, progressBarY);

		progressBar = new ProgressBar(0, 1, 1.0f / progressBarTexture.getWidth(), false, progressBarStyle);
		progressBar.setPosition(progressBarX + 2, progressBarY + 2);
		progressBar.setValue(progressBar.getMaxValue());
		progressBar.setWidth(progressBarTexture.getWidth() - 4);

		WidgetGroup innerContainer = new WidgetGroup();
		innerContainer.setPosition(Gdx.graphics.getWidth() / 2 - Math.round(backgroundImage.getWidth() / 2), Gdx.graphics.getHeight() / 2 - Math.round(backgroundImage.getHeight() / 2));

		innerContainer.addActor(backgroundImage);
		innerContainer.addActor(titleLabel);
		innerContainer.addActor(messageLabel);
		innerContainer.addActor(button);
		innerContainer.addActor(progressBarBackgroundImage);
		innerContainer.addActor(progressBar);

		setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		addActor(innerContainer);

		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Dialog.this.remove();
				if(callback != null)
					callback.onClosed(false);
			}
		});
	}

	private void showProgressBar(boolean show) {
		progressBar.setVisible(show);
		progressBarBackgroundImage.setVisible(show);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if(progressBar.isVisible()) {
			long elapsed = System.currentTimeMillis() - startTime;
			float progress = (float) elapsed / (float) time;
			progressBar.setValue(progress);

			if (progress >= 1.0f) {
				remove();
				if (callback != null)
					callback.onClosed(true);
			}
		}

		super.draw(batch, parentAlpha);
	}

	public interface Callback {
		/**
		 * @param completed true if progressbar completed, false if canceled by user
		 */
		void onClosed(boolean completed);
	}
}
