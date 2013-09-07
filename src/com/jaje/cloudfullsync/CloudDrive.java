package com.jaje.cloudfullsync;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;

public enum CloudDrive {
	Dropbox, GoogleDrive, Skydrive;

	private static final String DRAWABLE = "drawable/";
	private Drawable icon = null;

	@SuppressLint("DefaultLocale")
	public Drawable getIcon() {
		if (icon == null) {
			int id = App
					.getContext()
					.getResources()
					.getIdentifier(
							new StringBuilder(DRAWABLE).append(
									this.name().toLowerCase()).toString(),
							null, App.getContext().getPackageName());
			icon = App.getContext().getResources().getDrawable(id);
		}
		return icon;
	}
}
