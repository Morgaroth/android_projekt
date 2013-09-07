package com.jaje.cloudfullsync.binding;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.jaje.cloudfullsync.R;

public class AddBindingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_binding_layout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_binding, menu);
		return true;
	}

}
