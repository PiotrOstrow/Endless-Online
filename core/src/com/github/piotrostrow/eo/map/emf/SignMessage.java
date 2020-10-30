package com.github.piotrostrow.eo.map.emf;

public class SignMessage {

	public final int x, y;
	public final String title;
	public final String message;

	SignMessage(int x, int y, String title, String message){
		this.x = x;
		this.y = y;
		this.title = title;
		this.message = message;
	}
}
