package com.humanoid.alarmplus.util;

import android.util.Log;
public class LogUtil {
	public static final String TAG = "AlarmPlus";
	public static final boolean DEBUG_MODE = false; 

	private static String prettyArray(String[] array) {
		if (array.length == 0) {
			return "[]";
		}

		StringBuilder sb = new StringBuilder("[");
		int len = array.length-1;
		for (int i = 0; i < len; i++) {
			sb.append(array[i]);
			sb.append(", ");
		}
		sb.append(array[len]);
		sb.append("]");

		return sb.toString();
	}

	private static String logFormat(String format, Object... args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof String[]) {
				args[i] = prettyArray((String[])args[i]);
			}
		}
		String s = String.format(format, args);
		s = "[" + Thread.currentThread().getId() + "] " + s;
		return s;
	}

	public static void debug(String format, Object... args) {
		if(DEBUG_MODE){
			Log.d(TAG, logFormat(format, args));
		}
	}

	public static void warn(String format, Object... args) {
		if(DEBUG_MODE){
			Log.w(TAG, logFormat(format, args));
		}
	}

	public static void error(String format, Object... args) {
		if(DEBUG_MODE){
			Log.e(TAG, logFormat(format, args));
		}
	}

	public static void log(int level, String tag, String msg) {
		Thread              currentThread;
		StackTraceElement[] stackTrace;

		try {
			if ((currentThread = Thread.currentThread()) == null) {
				return;
			}
			if ((stackTrace = currentThread.getStackTrace()) == null) {
				return;
			}
			if (stackTrace.length < 4) {
				return;
			}
			msg = "[" + stackTrace[3].getMethodName() + "] " + msg;
		} finally {
			Log.println(level, tag, msg);
		}
	}
}
