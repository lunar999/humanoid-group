package com.humanoid.alarmplus;

import java.io.File;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class RecActivity extends Activity {
	
	private Button btnRec;
	private Button btnStop;
	private MediaRecorder recorder;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec_layout);
        
        btnRec = (Button) findViewById(R.id.btnRec);
    	btnStop = (Button) findViewById(R.id.btnStop);
    	
    	btnStop.setEnabled(false);
    	
    	btnRec.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				btnRec.setEnabled(false);
				startRec();
				btnStop.setEnabled(true);
				
			}
		});
    	
    	btnStop.setOnClickListener(new View.OnClickListener() {
    		
    		public void onClick(View v) {
    			btnStop.setEnabled(false);
    			stopRec();
    			btnRec.setEnabled(true);
    		}
    	});
        
        
    }

    private void startRec(){
    	if(recorder == null){
        	recorder = new MediaRecorder();
        }
    	
    	Log.d("TEST", "START_REC");
    	File recFile;
    	try {
			recFile = Environment.getExternalStorageDirectory();
			Log.d("PATH", recFile.getAbsolutePath());
			String path = recFile.getAbsolutePath() + "/sd/test_" + System.currentTimeMillis() + ".mp4";
			
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
    		Log.d("TEST", "STOP_REC");
    	}
    }
    
    
}