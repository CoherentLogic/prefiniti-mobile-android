package com.prefiniti.android.checkin;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class AppSettings extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
