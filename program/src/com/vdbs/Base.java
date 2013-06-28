package com.vdbs;

import java.awt.*; 
import javax.swing.*;

public class Base {
	private static Bi bi;
	private static UserInterface ui;

	public static void main(String[] args) {
		Base.bi = new Bi();
		Base.ui = new UserInterface();
		ui.createBaseStruct();
		new IndexPage();
	}

	public Bi getBi() {
		return Base.bi;
	}

	public UserInterface getUi() {
		return Base.ui;
	}

	
}