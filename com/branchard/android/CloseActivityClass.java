package com.branchard.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

public class CloseActivityClass {

	public static List<Activity> activityList = new ArrayList<Activity>();

	@SuppressWarnings("deprecation")
	public static void exitClient(Context ctx) {
		// πÿ±’À˘”–Activity
		for (int i = 0; i < activityList.size(); i++) {
			if (null != activityList.get(i)) {
				activityList.get(i).finish();
			}
		}
		ActivityManager activityMgr = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		activityMgr.restartPackage(ctx.getPackageName());
		System.exit(0);
	}

}
