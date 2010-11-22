package com.humanoid.alarmplus;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class TTS implements OnInitListener, OnUtteranceCompletedListener {

	private static final String TAG = "TTS Demo";
	private TextToSpeech mTts;
	private int uttCount = 0;
	private int lastUtterance = -1;
	private HashMap<String, String>  params = new HashMap<String, String>();

	private String soundFilename = null;
	private File soundFile = null;

	private static final String TTS_FILE_NAME = "alarm_tts.wav";
	private static final int DIALOG_LIST = 1;
	
	public TTS (Context context) {
		mTts = new TextToSpeech(context, this);
	}
		
	private String getFilePath() {
//		String path = "";
//		String state = android.os.Environment.getExternalStorageState();
//		if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
//			path = getFilesDir().getAbsolutePath() + File.separator + TTS_FILE_NAME;
//		} else {//sdcard
//			path = UtilFile.getSdCardAlarmPath(TTS_FILE_NAME);
//		}
		String path = "/sdcard/alarm_tts.wav";	// 임시로, getFileDir에서 에러때문에
		
		return path;
	}
	

	public String saveVoiceAlarmMessage(String text) {
		
		soundFilename = getFilePath();
		soundFile = new File(soundFilename);
		if (soundFile.exists())
			soundFile.delete();
		
		if(mTts.synthesizeToFile(text, null, soundFilename)
				== TextToSpeech.SUCCESS) {
			Log.v(TAG, ">>>>> Sound file created : " + text);
		}
		else {
			Log.v(TAG, ">>>>> Sound file not created : " + text);
		}
		
		return soundFilename;
	}
	
/*	
	// 2010.11.20 added by ahn, 오늘의 온도 조회
	private String getWeatherData() {
		WeatherData weatherInfo = new WeatherData();
		
		return weatherInfo.getTemp();
	}
	
	// 2010.11.20 added by ahn, 오늘의 온도로 음성 알람 메세지 구성
	private String getWeatherMessage(String message) {
		String temperature = getWeatherData();
//		if (temperature.equals(null))
			temperature = "5";
		
		return message.replace("%t", temperature); // Today\'s temperature will %t degrees Celsius.

	}
*/	

	private String getCurrentTime() {
		Calendar cal = Calendar.getInstance( );
		
		return cal.get(Calendar.HOUR_OF_DAY) + " " + cal.get(Calendar.MINUTE);
	}
	

	public String getCurrentTimeMessage() {
		String timeMessage = "It's " + getCurrentTime() + ".";	// It's 12 40
				
		return timeMessage; // It's 6 30.
	}
	

	//@Override
	public void onInit(int status) {
		if( status == TextToSpeech.SUCCESS) {
			mTts.setLanguage(Locale.US);
			mTts.setOnUtteranceCompletedListener(this);
		}
	}

	//@Override
	public void onUtteranceCompleted(String uttId) {
		Log.v(TAG, "onUtteranceCompleted : " + uttId);
		lastUtterance = Integer.parseInt(uttId);
	}

	public void onPause()
	{
		// 포커스가 없으면, 음성 중지
		if( mTts != null)
		mTts.stop();
	}
	
	public void onDestroy()
	{
		mTts.shutdown();
	}
}
