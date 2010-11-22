package com.humanoid.alarmplus;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;

import com.humanoid.alarmplus.util.UtilFile;
import com.humanoid.alarmplus.SetAlarm;
import com.humanoid.alarmplus.Alarms;


public class RecActivity extends Activity implements AlarmConstantIf{

//	private static final String ALARM_REC_POSTFIX = ".mp4";
	private static final String SD2 = "sd";
	private static final String ALARM_REC_PREFIX = "alarm_rec.mp4";
	private static final String SAMSUNG_GALAXYS = "SHW-M110S";
    private static final float IN_CALL_VOLUME = 0.125f;
    
    
	private static final boolean DEBUG_MODE = true;
	
	private Button btnRec;
	private Button btnStop;
	
	private Button btnListen;
	private Button btnClosed;
	
	private MediaRecorder recorder;
	
	private Bitmap micImage_green;
	private Bitmap micImage_red;
	
	private ImageView imageView;
	
	private MediaPlayer mMediaPlayer;
	
	Chronometer chronometer;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rec_layout);
		micImage_green = BitmapFactory.decodeResource(getResources(), R.drawable.mic_128);
		micImage_red = BitmapFactory.decodeResource(getResources(), R.drawable.mic_red_128);

		btnRec = (Button) findViewById(R.id.btnRec);
		btnStop = (Button) findViewById(R.id.btnStop);
		
		btnListen = (Button) findViewById(R.id.btnListen);
		btnClosed = (Button) findViewById(R.id.btnClosed);
		
		imageView = (ImageView)findViewById(R.id.mic_128);
		chronometer = (Chronometer) findViewById(R.id.chronometer);
		
		btnStop.setEnabled(false);

		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnErrorListener(new OnErrorListener() {
	         public boolean onError(MediaPlayer mp, int what, int extra) {
	             Log.d("","Error occurred while playing audio.");
	             mp.stop();
	             mp.release();
	             mMediaPlayer = null;
	             return true;
	         }
	     });
		
		mMediaPlayer.setVolume(IN_CALL_VOLUME, IN_CALL_VOLUME);
//		mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),afd.getLength());
		try {
			mMediaPlayer.setDataSource("/sdcard/humanoid/alarm/alarm_rec.mp4");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
				
				imageChange(true);

			}
		});

		btnStop.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				btnStop.setEnabled(false);
				chronometer.stop();
				stopRec();
				btnRec.setEnabled(true);
				
				imageChange(false);
			}
		});
		
		btnListen.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				//play
//				try {
//					startAlarm();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
			}
		});
		
		btnClosed.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});
	}
	
	
	private void startAlarm()
	throws java.io.IOException, IllegalArgumentException,
	IllegalStateException {
		final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		// do not play alarms if stream volume is 0
		// (typically because ringer mode is silent).
		if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
			mMediaPlayer.setLooping(false);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			
			mMediaPlayer.stop();
            mMediaPlayer.release();
		}
	}
	private void imageChange(boolean isStart) {
		if(isStart) {
			imageView.setImageBitmap(micImage_red);
		}
		else {
			imageView.setImageBitmap(micImage_green);
		}
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
				//path = getFilesDir().getAbsolutePath() + File.separator + ALARM_REC_PREFIX ;
				path = getFilesDir().getAbsolutePath() + File.separator + UtilFile.recpath ;	 // 10.11.22 update redmars				
			} else {//sdcard
				
				//path = UtilFile.getSdCardAlarmPath(ALARM_REC_PREFIX);
				path = UtilFile.getSdCardAlarmPath(UtilFile.recpath);		 // 10.11.22 update redmars

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