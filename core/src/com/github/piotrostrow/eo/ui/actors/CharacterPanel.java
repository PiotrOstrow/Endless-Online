package com.github.piotrostrow.eo.ui.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class CharacterPanel extends WidgetGroup {

	public Image characterImage;
	public Button loginButton;
	public Button deleteButton;
	public Label nameLabel;

	private String name;

	public void reset(){
		characterImage.setDrawable(null);
		setName("");
	}

	public void setName(String name){
		this.name = name;
		if(name.length() > 14){
			String f = name.substring(0, 12) + "...";
			nameLabel.setText(f);
		}else{
			nameLabel.setText(name);
		}
	}

	@Override
	public String getName() {
		return name;
	}
}