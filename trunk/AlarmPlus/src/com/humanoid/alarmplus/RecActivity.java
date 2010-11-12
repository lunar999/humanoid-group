package com.humanoid.alarmplus;

import java.io.File;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;

public class RecActivity extends Activity {

	private static final String ALARM_REC_POSTFIX = ".mp4";
	private static final String SD2 = "sd";
	private static final String ALARM_REC_PREFIX = "/alarm_rec_";
	private static final String SAMSUNG_GALAXYS = "SHW-M110S";
	
	private static final boolean DEBUG_MODE = true;
	
	private Button btnRec;
	private Button btnStop;
	private MediaRecorder recorder;
	Chronometer chronometer;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rec_layout);

		btnRec = (Button) findViewById(R.id.btnRec);
		btnStop = (Button) findViewById(R.id.btnStop);
		
		chronometer = (Chronometer) findViewById(R.id.chronometer);
		

		btnStop.setEnabled(false);

		btnRec.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				btnRec.setEnabled(false);
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						chronometer.setBase(SystemClock.elapsedRealtime());
						chronometer.start();
					}
				});
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						startRec();
						
					}
				});
				
				btnStop.setEnabled(true);

			}
		});

		btnStop.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				btnStop.setEnabled(false);
				chronometer.stop();
				stopRec();
				btnRec.setEnabled(true);
			}
		});


	}
	
	View.OnClickListener mResetListener = new OnClickListener() {
        public void onClick(View v) {
            chronometer.setBase(SystemClock.elapsedRealtime());
        }
    };


	private void startRec(){
		if(recorder == null){
			recorder = new MediaRecorder();
		}
		
		if(DEBUG_MODE){
			Log.d("TEST", "START_REC");
			Log.d("Build.MODEL", Build.MODEL);
			Log.d("getExternalStorageState", android.os.Environment.getExternalStorageState()+"");
		}
		
		File recFile = Environment.getExternalStorageDirectory();
		String path = "";
		try {
			
			String state = android.os.Environment.getExternalStorageState();
			if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
				path = getFilesDir().getAbsolutePath() + File.separator + ALARM_REC_PREFIX + System.currentTimeMillis() + ALARM_REC_POSTFIX;
			} else {
				path = recFile.getAbsolutePath() + ALARM_REC_PREFIX + System.currentTimeMillis() + ALARM_REC_POSTFIX;
				if(SAMSUNG_GALAXYS.equals(Build.MODEL)){
					File sd2 = new File(recFile.getAbsolutePath()+ File.separator + SD2);
					if(sd2.exists()){
						path = recFile.getAbsolutePath() + File.separator + SD2  + ALARM_REC_PREFIX + System.currentTimeMillis() + ALARM_REC_POSTFIX;
					} else {
						path = recFile.getAbsolutePath() + ALARM_REC_PREFIX + System.currentTimeMillis() + ALARM_REC_POSTFIX;
					}
				}
			}
			if(DEBUG_MODE){
				Log.d("PATH", path);
			}
			
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			recorder.setOutputFile(path);

			recorder.prepare();
			recorder.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void stopRec(){
		if(recorder != null){
			recorder.stop();
			recorder.release();
			recorder = null;
			
			if(DEBUG_MODE){
				Log.d("TEST", "STOP_REC");
			}
		}
	}


}