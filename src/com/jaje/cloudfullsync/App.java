package com.jaje.cloudfullsync;

import android.app.Application;
import android.content.Context;

import com.jaje.cloudfullsync.binding.Bindings;
import com.jaje.cloudfullsync.db.DBAdapter;

public class App extends Application {

	private static Context mContext;
	private static Bindings bindings;

	public static Bindings getBindings() {
		return bindings;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		bindings = new Bindings();
		new DBAdapter(this).open().readBindingsInto(bindings).close();
	}

	public static Context getContext() {
		return mContext;
	}
}