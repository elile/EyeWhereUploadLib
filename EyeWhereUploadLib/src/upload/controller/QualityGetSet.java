package upload.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class QualityGetSet {

	public static void setQuality(Context c, int q) {
		if (q > 100) {
			q=100;
		}
		if (q <= 0) {
			q=1;
		}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
		Editor editor = prefs.edit();
		editor.putInt("quality", q);
		editor.commit();
	}

	public static int getQuality(Context c) {
		SharedPreferences prefs =PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
		return prefs.getInt("quality", -1);

	}

}
