package com.humanoid.alarmplus;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;

import com.humanoid.alarmplus.util.UtilFile;


public class RecActivity extends Activity implements AlarmConstantIf{

	//	private static final String ALARM_REC_POSTFIX = ".mp4";
	private static final String SD2 = "sd";
	private static final String ALARM_REC_PREFIX = "alarm_rec.mp4";
	private static final String SAMSUNG_GALAXYS = "SHW-M110S";
	private static final float IN_CALL_VOLUME = 0.125f;

	private String recFilePath = AlarmConstantIf.ALARM_REC_FILE_FULL_PATH;
	private static final boolean DEBUG_MODE = true;

	private Button btnRec;
	private Button btnStop;
	private Button btnListen;
	private Button btnClosed;
	private MediaRecorder recorder;
	private Bitmap img_green;
	private Bitmap img_red;
	private ImageView imageView;
	private MediaPlayer mediaPlayer;
	private Chronometer recChronometer;
	private Chronometer playChronometer;

	View.OnClickListener mResetListener = new OnClickListener() {
		public void onClick(View v) {
			recChronometer.setBase(SystemClock.elapsedRealtime());
		}
	};
	
	View.OnClickListener mResetListener2 = new OnClickListener() {
		public void onClick(View v) {
			playChronometer.setBase(SystemClock.elapsedRealtime());
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rec_layout);

		UtilFile.makeAlarmDir();

		img_green = BitmapFactory.decodeResource(getResources(), R.drawable.mic_128);
		img_red = BitmapFactory.decodeResource(getResources(), R.drawable.mic_red_128);

		btnRec = (Button) findViewById(R.id.btnRec);
		btnStop = (Button) findViewById(R.id.btnStop);
		btnListen = (Button) findViewById(R.id.btnListen);
		btnClosed = (Button) findViewById(R.id.btnClosed);
		imageView = (ImageView)findViewById(R.id.mic_128);
		recChronometer = (Chronometer) findViewById(R.id.recChronometer);
		playChronometer = (Chronometer) findViewById(R.id.playChronometer);
		
		playChronometer.setVisibility(View.INVISIBLE);

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
						recChronometer.setBase(SystemClock.elapsedRealtime());
						recChronometer.start();
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
				recChronometer.stop();
				stopRec();
				btnRec.setEnabled(true);

				imageChange(false);
			}
		});

		btnListen.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				//play
				try {
					playChronometer.setVisibility(View.VISIBLE);
					btnListen.setEnabled(false);
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							playChronometer.setBase(SystemClock.elapsedRealtime());
							playChronometer.start();
						}
					});
					
					playRecFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		btnClosed.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if(mediaPlayer.isPlaying()){
					mediaPlayer.stop();
					mediaPlayer.release();
				}
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnErrorListener(new OnErrorListener() {
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Log.d("","Error occurred while playing audio.");
				mp.stop();
				mp.release();
				mediaPlayer = null;
				return true;
			}
		});
		
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mediaplayer) {
				mediaplayer.stop();
				mediaplayer.release();
				btnListen.setEnabled(true);
				playChronometer.setVisibility(View.INVISIBLE);
			}
		});

		mediaPlayer.setVolume(IN_CALL_VOLUME, IN_CALL_VOLUME);

	}


	/**
	 * 녹음된 파일 재생 
	 */
	private void playRecFile() {
		final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

		try {
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mediaPlayer.setDataSource(recFilePath);
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mediaPlayer.setLooping(false);
				mediaPlayer.prepare();
				mediaPlayer.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param isStart
	 */
	private void imageChange(boolean isStart) {
		if(isStart) {
			imageView.setImageBitmap(img_red);
		}
		else {
			imageView.setImageBitmap(img_green);
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
				Log.d("PATH", recFilePath);
			}
			recFilePath = getFilePath();

			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			recorder.setOutputFile(recFilePath);

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
			filePath = getFilesDir().getAbsolutePath() + File.separator + AlarmConstantIf.ALARM_REC_FILE_NAME ;				
		} else {
			//sdcard
			filePath = UtilFile.getSdCardAlarmPath(AlarmConstantIf.ALARM_REC_FILE_NAME);

		}

		return filePath;
	}


}