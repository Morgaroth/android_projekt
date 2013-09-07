package com.jaje.cloudfullsync.binding;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;

import com.jaje.cloudfullsync.App;

public enum SyncDirect {
	OnlyUpload, Bidirect, OnlyDownload;

	private Drawable icon = null;
	private static final String DRAWABLE = "drawable/";

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
