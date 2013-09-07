package com.jaje.cloudfullsync;

import android.os.Environment;

public class MyConfiguration {
	static String ROOT_PATH;

	public MyConfiguration() {
	}

	public static void setDefaultConfiguration() {
		ROOT_PATH = new StringBuilder(Environment.getExternalStorageDirectory()
				.getAbsolutePath()).append("Sync").toString();
	}

}
