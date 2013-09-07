package com.jaje.cloudfullsync.binding;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.jaje.cloudfullsync.App;
import com.jaje.cloudfullsync.R;
import com.jaje.cloudfullsync.fileexplorer.SelectFolderActivity;

public class BindingPreferences extends PreferenceActivity {

	public static final String BINDING_INDEX = "bindindex";
	private Binding bind;
	private Preference localPathPref;
	private Preference cloudPathPref;
	private CheckBoxPreference activePref;
	private ListPreference frequencyPref;

	private static final int SET_LOCAL_PATH = 1;
	private static final int SET_CLOUD_PATH = 2;

	// private Preference

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences_layout);

		bind = App.getBindings().bindingsList.get(getIntent().getIntExtra(
				BINDING_INDEX, -1));

		activePref = (CheckBoxPreference) findPreference(getString(R.string.preference_active));
		activePref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						Log.d("MyApp",
								"preference is set to " + newValue.toString());
						bind.setActive((Boolean) newValue);
						return true;
					}
				});
		localPathPref = findPreference(getString(R.string.preference_local_path));
		localPathPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						Intent i = new Intent(BindingPreferences.this,
								SelectFolderActivity.class);
						startActivityForResult(i, SET_LOCAL_PATH);
						return true;
					}
				});
		cloudPathPref = findPreference(getString(R.string.preference_cloud_path));
		frequencyPref = (ListPreference) findPreference(getString(R.string.preference_frequency));
		frequencyPref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						Log.d("ListPreference",
								"list changed to " + newValue.toString());
						bind.setFrequency(newValue.toString());
						frequencyPref.setValue(newValue.toString());
						frequencyPref.setSummary(frequencyPref.getEntry());
						return true;
					}
				});
		getPreferences();
	}

	@Override
	protected void onDestroy() {
		setResult(1);
		super.onDestroy();
	}

	private void getPreferences() {
		localPathPref.setSummary(bind.localPath);
		cloudPathPref.setSummary(bind.cloudPath);
		activePref.setChecked(bind.isActive());
		frequencyPref.setValue(bind.getFrequency());
		frequencyPref.setSummary(frequencyPref.getEntry());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case SET_LOCAL_PATH:
				bind.localPath = data
						.getStringExtra(SelectFolderActivity.RESULT);
				localPathPref.setSummary(bind.localPath);
				break;
			case SET_CLOUD_PATH:
				bind.cloudPath = data
						.getStringExtra(SelectFolderActivity.RESULT);
				cloudPathPref.setSummary(bind.cloudPath);
				break;
			default:
				break;
			}
		}
	}
}
