package upload.controller;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class QualityGetSet {

	public static void setQuality(Activity a, int q) {
		if (q > 100) {
			q=100;
		}
		if (q <= 0) {
			q=1;
		}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(a.getApplicationContext());
		Editor editor = prefs.edit();
		editor.putInt("quality", q);
		editor.commit();
	}

	public static int getQuality(Activity a) {
		SharedPreferences prefs =PreferenceManager.getDefaultSharedPreferences(a.getApplicationContext());
		return prefs.getInt("quality", -1);

	}

}
