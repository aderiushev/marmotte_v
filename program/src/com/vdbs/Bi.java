package com.vdbs;

import java.util.Date;
import java.text.SimpleDateFormat;

public class Bi {
	private Project p;
	private Importer i;

	public Project getProject() {
		if (this.p == null) {
			this.p = new Project();
		}

		return this.p;
	}

	public Importer getImporter() {
		if (this.i == null) {
			this.i = new Importer();
		}

		return this.i;
	}

	public static String getCurrentTime() {
    	SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    	return  df.format(new Date());
	}

}