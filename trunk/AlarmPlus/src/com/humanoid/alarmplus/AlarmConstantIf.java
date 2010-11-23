/**
 * 
 */
package com.humanoid.alarmplus;

import java.io.File;

import android.os.Environment;

/**
 * @author min
 *
 */
public interface AlarmConstantIf {

	final String ALARM_FILE_PATH = "/humanoid/alarm";
	
	/**
	 * 녹음파일명
	 */
	final String ALARM_REC_FILE_NAME = "alarm_rec.mp4";
	
	/**
	 * 녹음파일 전체 경로(파일명 포함)
	 */
	final String ALARM_REC_FILE_FULL_PATH = Environment.getExternalStorageDirectory() + File.separator + ALARM_FILE_PATH + File.separator + ALARM_REC_FILE_NAME;
	
}