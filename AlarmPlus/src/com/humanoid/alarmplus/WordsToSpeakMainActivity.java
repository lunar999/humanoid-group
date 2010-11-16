package com.humanoid.alarmplus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

import com.humanoid.alarmplus.util.UtilFile;

public class WordsToSpeakMainActivity extends Activity implements OnInitListener, OnUtteranceCompletedListener {
	private EditText words = null;
	private Button  speakBtn  = null;
	private Button  saveBtn  = null;
	private Button  closeBtn = null;
	private static final int REQ_TTS_STATUS_CHECK = 0;
	private static final String TAG = "TTS Demo";
	private TextToSpeech mTts;
	private int uttCount = 0;
	private int lastUtterance = -1;
	private HashMap<String, String>  params = new HashMap<String, String>();

	private String soundFilename = null;
	private File soundFile = null;

	private static final String TTS_FILE_NAME = "alarm_tts.wav";
	
	private static final int DIALOG_LIST = 1;
	
	/** 액티비티 최초 생성 시에 호출됨 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tts_layout);
		
		words = (EditText)findViewById(R.id.wordsToSpeak);
		words.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DIALOG_LIST);
				
				return true;
			}
		});
		
		
		speakBtn  = (Button)findViewById(R.id.speak);
		speakBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				mTts.speak(words.getText().toString(), TextToSpeech.QUEUE_ADD, null);
			}
		});
		
		saveBtn  = (Button)findViewById(R.id.save);
		saveBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				soundFilename = getFilePath();
				soundFile = new File(soundFilename);
				if (soundFile.exists())
					soundFile.delete();
				
				if(mTts.synthesizeToFile(words.getText().toString(), null, soundFilename)
						== TextToSpeech.SUCCESS) {
					Toast.makeText(getBaseContext(),
							"Sound file created",
							Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(getBaseContext(),
							"Oops! Sound file not created",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		closeBtn  = (Button)findViewById(R.id.close);
		closeBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				finish();
			}
		});
	
	// TTS가 존재하는지와 사용 가능한지 검사
	Intent checkIntent = new Intent();
	checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
	startActivityForResult(checkIntent, REQ_TTS_STATUS_CHECK);
	}
	
	private String getFilePath() {
		String path = "";
		String state = android.os.Environment.getExternalStorageState();
		if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
			path = getFilesDir().getAbsolutePath() + File.separator + TTS_FILE_NAME;
		} else {//sdcard
			path = UtilFile.getSdCardAlarmPath(TTS_FILE_NAME);
		}
		
		return path;
	}
	
	@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_LIST:
            return new AlertDialog.Builder(WordsToSpeakMainActivity.this)
                .setTitle("음성 메세지 선택")
                .setItems(R.array.alarm_tts_sample_message_values, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String[] items = getResources().getStringArray(R.array.alarm_tts_sample_message_values);
                        if (which != 0) // 직접 입력이 아니면
                        	words.setText(items[which]);
                    }
                })
                .create();
        }
        
        return null;
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQ_TTS_STATUS_CHECK) {
			switch (resultCode) {
				case  TextToSpeech.Engine.CHECK_VOICE_DATA_PASS:
					// TTS가 존재하며 실행 중이면
					mTts = new TextToSpeech(this, this);
					Log.v(TAG, "Pico가 성공적으로 설치됐어요");
					break;
				case  TextToSpeech.Engine.CHECK_VOICE_DATA_BAD_DATA:
				case  TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_DATA:
				case  TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_VOLUME:
					// 데이터가 없어서 설치해야 되면
					Log.v(TAG, "언어 요소 필요: " + resultCode);
					Intent installIntent = new Intent();
					installIntent.setAction(
					TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
					startActivity(installIntent);
					break;
				case  TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL:
				default:
					Log.e(TAG, "실패했어요. TTS가 없는 것 같아요");
		}
		}
		else {
		// 그 외의 경우에 대한 처리
		}
	}
	
	//@Override
	public void onClick(View view) {
		StringTokenizer st = new StringTokenizer(words.getText().toString(),",.");
		while (st.hasMoreTokens()) {
			params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, String.valueOf(uttCount++));
			mTts.speak(st.nextToken(), TextToSpeech.QUEUE_ADD, params);
		}
	}

	//@Override
	public void onInit(int status) {
		// TTS 엔진을 완성했으니 이제 버튼을 가용화하자.
		if( status == TextToSpeech.SUCCESS) {
			mTts.setLanguage(Locale.US);
			speakBtn.setEnabled(true);
			saveBtn.setEnabled(true);
			mTts.setOnUtteranceCompletedListener(this);
		}
	}

	//@Override
	public void onUtteranceCompleted(String uttId) {
		Log.v(TAG, "어터런스 Id별 읽기 완료 메시지: " + uttId);
		lastUtterance = Integer.parseInt(uttId);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		// 포커스가 없으면, 음성 중지
		if( mTts != null)
		mTts.stop();
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mTts.shutdown();
	}
}
