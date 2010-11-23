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

	private String path = "";
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
	private Chronometer chronometer;

	View.OnClickListener mResetListener = new OnClickListener() {
		public void onClick(View v) {
			chronometer.setBase(SystemClock.elapsedRealtime());
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rec_layout);
		
		UtilFile.makeAlarmDir();

		micImage_green = BitmapFactory.decodeResource(getResources(), R.drawable.mic_128);
		micImage_red = BitmapFactory.decodeResource(getResources(), R.drawable.mic_red_128);

		btnRec = (Button) findViewById(R.id.btnRec);
		btnStop = (Button) findViewById(R.id.btnStop);
		btnListen = (Button) findViewById(R.id.btnListen);
		btnClosed = (Button) findViewById(R.id.btnClosed);
		imageView = (ImageView)findViewById(R.id.mic_128);
		chronometer = (Chronometer) findViewById(R.id.chronometer);

		setBtnListener();
	}

	/**
	 * 버튼 리스너 적용
	 */
	private void setBtnListener() {
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
				try {
					startAlarm();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		btnClosed.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		

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
		//			mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),afd.getLength());


	}


	/**
	 * 
	 * @throws java.io.IOException
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	private void startAlarm() throws java.io.IOException, IllegalArgumentException,	IllegalStateException {
		final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		// do not play alarms if stream volume is 0
		// (typically because ringer mode is silent).
		if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
			try {
				mMediaPlayer.setDataSource("/sdcard/humanoid/alarm/alarm_rec.mp4");
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mMediaPlayer.setLooping(false);
				mMediaPlayer.prepare();
				mMediaPlayer.start();

				mMediaPlayer.stop();
				mMediaPlayer.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param isStart
	 */
	private void imageChange(boolean isStart) {
		if(isStart) {
			imageView.setImageBitmap(micImage_red);
		}
		else {
			imageView.setImageBitmap(micImage_green);
		}
	}

	/**
	 * 녹음시작
	 */
	private void startRec(){
		if(recorder == null){
			recorder = new MediaRecorder();
		}

		if(DEBUG_MODE){
			Log.d("TEST", "START_REC");
			Log.d("Build.MODEL", Build.MODEL);
			Log.d("getExternalStorageState", android.os.Environment.getExternalStorageState()+"");
		}

//		File recFile = Environment.getExternalStorageDirectory();

		try {
			if(DEBUG_MODE){
				Log.d("PATH", path);
			}
			path = getFilePath();

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

	/**
	 * 녹음파일 저장장소를 리턴함
	 * @author lunar999
	 */
	private String getFilePath(){
		String filePath = "";
		String state = android.os.Environment.getExternalStorageState();
		if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
			filePath = getFilesDir().getAbsolutePath() + File.separator + UtilFile.recpath ;				
		} else {
			//sdcard
			filePath = UtilFile.getSdCardAlarmPath(UtilFile.recpath);

		}

		return filePath;
	}


}