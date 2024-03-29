/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.humanoid.alarmplus;

 
import java.io.File;
import java.util.HashMap;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.humanoid.alarmplus.util.UtilFile;

/**
 * Manages each alarm
 */
public class SetAlarm extends PreferenceActivity
        implements TimePickerDialog.OnTimeSetListener {

    private EditTextPreference mLabel;
    private Preference mTimePref;
    private AlarmPreference mAlarmPref;
    private CheckBoxPreference mVibratePref;
    private RepeatPreference mRepeatPref;
    private MenuItem mDeleteAlarmItem;
    private MenuItem mTestAlarmItem;
    private ListPreference mEffectPref;    // 10.11.03 add redmars    

    private int     mId;
    private boolean mEnabled;
    private int     mHour;
    private int     mMinutes;

    // TTS
//    private Preference mTtsPref;
//    private Preference mTtsMessagePref;
//    private Preference mTtsTestPref;
    
    private static final String TAG = "SetAlarm";
    private static final int REQ_TTS_STATUS_CHECK = 0;
    private int lastUtterance = -1;
    private int uttCount = 0;
    private HashMap<String, String>  params = new HashMap<String, String>();
    private TextToSpeech mTts;
    public static final String TTS_FILE_NAME = "/sdcard/alarm_tts.wav";
    private String allpath = "/sdcard/humanoid/alarm/";    // 10.11.16 add redmars    
    private static String regpath;    // 10.11.16 add redmars 
    private static String ttspath;    // 10.11.16 add redmars 

    
    /**
     * Set an alarm.  Requires an Alarms.ALARM_ID to be passed in as an
     * extra. FIXME: Pass an Alarm object like every other Activity.
     */
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.alarm_prefs);

        // TTS 
//        mTtsPref = findPreference("tts_preference");
//        mTtsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//			
//			@Override
//			public boolean onPreferenceClick(Preference preference) {
//				Toast.makeText(getBaseContext(), "TTS Preference Checked!!", Toast.LENGTH_SHORT).show();
//				return false;
//			}
//		});
//        
//        mTtsMessagePref = (EditTextPreference)findPreference("tts_message_preference");
////        mTtsMessagePref.setDefaultValue(mTtsMessagePref.getSummary());
//        mTtsMessagePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//			
//			@Override
//			public boolean onPreferenceClick(Preference preference) {
//
////				String ttsMessage = mTtsMessagePref.getSummary().toString();
////				Toast.makeText(getBaseContext(), ttsMessage, Toast.LENGTH_LONG).show();
//				
//				return false;
//			}
//		});
//        
//        mTtsMessagePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//			
//			@Override
//			public boolean onPreferenceChange(Preference preference, Object newValue) {
//				preference.setSummary((String) newValue);
//				saveTtsSoundFile((String)newValue);
//				
//				Toast.makeText(getBaseContext(), newValue.toString(), Toast.LENGTH_LONG).show();
//				return true;
//			}
//		});
//
//        mTtsTestPref = findPreference("tts_test_preference");
//        mTtsTestPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//			
//			@Override
//			public boolean onPreferenceClick(Preference preference) {
//				String ttsMessage = mTtsMessagePref.getSummary().toString();
//				
////				getSampleText();
////				speakMessage(ttsMessage);
//				lanunchTTSActivity(ttsMessage);
//				
////				Toast.makeText(getBaseContext(), ttsMessage, Toast.LENGTH_LONG).show();
//				return true;
//			}
//		});
        ////////////
        
        // Get each preference so we can retrieve the value later.
        mLabel = (EditTextPreference) findPreference("label");
        mLabel.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    public boolean onPreferenceChange(Preference p,
                            Object newValue) {
                        // Set the summary based on the new label.
                        p.setSummary((String) newValue);
                        return true;
                    }
                });
        mTimePref = findPreference("time");
        mAlarmPref = (AlarmPreference) findPreference("alarm");
        mVibratePref = (CheckBoxPreference) findPreference("vibrate");
        mRepeatPref = (RepeatPreference) findPreference("setRepeat");
        mEffectPref = (ListPreference)findPreference("effect");      // 10.11.03 add redmars  
        mEffectPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			
        	@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {

    			String[] alarm_effect_lists = getResources().getStringArray(R.array.alarm_effect_lists);
        		        		
//        		if("00".equals(newValue)) {//기본 벨소리
//        			mEffectPref.setSummary("기본 벨소리");
//        		}
//        		else if("01".equals(newValue)) {//날씨 효과음
//        			mEffectPref.setSummary("날씨 효과음");
//        		}
//        		else if("02".equals(newValue)) {//음성 녹음
//        			//Toast.makeText(getBaseContext(), "녹음을 수행 합니다.", Toast.LENGTH_LONG).show();
//        			mEffectPref.setSummary("음성 녹음");
//        			final Intent intent = new Intent(SetAlarm.this, RecActivity.class);
//        			startActivity(intent);
//        		}
//        		else if("03".equals(newValue)) {//TTS
//        			mEffectPref.setSummary("TTS(Text-To-Speech)");
//        			final Intent intent = new Intent(SetAlarm.this, WordsToSpeakMainActivity.class);
//        			startActivity(intent);
//        		}

    			// 2010.11.22 updated by redmars, 리소스의 리스트에서 summary 설정
    			String[] alarm_effect_code= getResources().getStringArray(R.array.alarm_effect_values);    	
    			
    			if (newValue.equals(alarm_effect_code[0])) {//default
    				
    			} else if (newValue.equals(alarm_effect_code[1])) {//weather
    				
    			} else if (newValue.equals(alarm_effect_code[2])) {//voice record
    				//regpath = allpath + "alarm_rec_" + mId + "_02.mp4";       // 10.11.17 add redmars    	
    				regpath = "alarm_rec_id_" + mId + "_" + newValue + ".mp4";       // 10.11.22 update redmars
    				regpath = UtilFile.getSdCardAlarmPath(regpath);
    				
        			startActivity(new Intent(SetAlarm.this, RecActivity.class));  
        			
//        			startVoiceRecognitionActivity();
        			
    			} else if (newValue.equals(alarm_effect_code[3])) {//TTS
        			//ttspath = allpath + "alarm_tts_" + mId + "_03.wav";       // 10.11.17 add redmars    
    				ttspath = "alarm_tts_id_" + mId + "_" + newValue + ".wav";       // 10.11.22 update redmars
    				ttspath = UtilFile.getSdCardAlarmPath(ttspath);
    				
        			startActivity(new Intent(SetAlarm.this, VoiceAlarmMessage.class));    				
    			} 
    			mEffectPref.setSummary(alarm_effect_lists[Integer.parseInt(newValue.toString())]);
//        		Toast.makeText(getBaseContext(), newValue.toString(), Toast.LENGTH_LONG).show();
        		Toast.makeText(getBaseContext(), alarm_effect_lists[Integer.parseInt(newValue.toString())], Toast.LENGTH_LONG).show();

        		return true;
			}
		});

        Intent i = getIntent();
        mId = i.getIntExtra(Alarms.ALARM_ID, -1);
        if (Log.LOGV) {
            Log.v("In SetAlarm, alarm id = " + mId);
        }

        /* load alarm details from database */
        Alarm alarm = Alarms.getAlarm(getContentResolver(), mId);
        mEnabled = alarm.enabled;
        mLabel.setText(alarm.label);
        mLabel.setSummary(alarm.label);
        mHour = alarm.hour;
        mMinutes = alarm.minutes;
        mRepeatPref.setDaysOfWeek(alarm.daysOfWeek);
        mVibratePref.setChecked(alarm.vibrate);
        // Give the alert uri to the preference.
        mAlarmPref.setAlert(alarm.alert);
        mEffectPref.setValue(alarm.effect); // 10.11.03 add redmars   
        String effectValue =  mEffectPref.getValue();
//        if (effectValue.equals("00")) {
//        	mEffectPref.setSummary("기본 벨소리");	
//        }
//        else if  (effectValue.equals("01")) {
//        	mEffectPref.setSummary("날씨 효과음");	
//        }
//        else if  (effectValue.equals("02")) {
//        	mEffectPref.setSummary("음성녹음");	
//        }
//        else {
//        	mEffectPref.setSummary("TTS(Text-To-Speech)");	
//        }
		// 2010.11.20 updated by ahn, 리소스의 리스트에서 summary 설정
		String[] alarm_effect_lists = getResources().getStringArray(R.array.alarm_effect_lists);
		mEffectPref.setSummary(alarm_effect_lists[Integer.parseInt(effectValue)]);

        updateTime();

        // We have to do this to get the save/cancel buttons to highlight on
        // their own.
        getListView().setItemsCanFocus(true);

        // Grab the content view so we can modify it.
        //FrameLayout content = (FrameLayout) getWindow().getDecorView().findViewById(com.android.internal.R.id.content);
        FrameLayout content = (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);
        // Get the main ListView and remove it from the content view.
        ListView lv = getListView();
        content.removeView(lv);

        // Create the new LinearLayout that will become the content view and
        // make it vertical.
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        // Have the ListView expand to fill the screen minus the save/cancel
        // buttons.
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
        lp.weight = 1;
        ll.addView(lv, lp);

        // Inflate the buttons onto the LinearLayout.
        View v = LayoutInflater.from(this).inflate(
                R.layout.save_cancel_alarm, ll);

        // Attach actions to each button.
        Button b = (Button) v.findViewById(R.id.alarm_save);
        b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    saveAlarm();
                    finish();
                }
        });
        b = (Button) v.findViewById(R.id.alarm_cancel);
        b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
        });

        // Replace the old content view with our new one.
        setContentView(ll);
    }
    
    /**
     * Fire an intent to start the speech recognition activity.
     */
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
//        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
        startActivity(intent);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mTimePref) {
            new TimePickerDialog(this, this, mHour, mMinutes,
                    DateFormat.is24HourFormat(this)).show();
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onBackPressed() {
        saveAlarm();
        finish();
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinutes = minute;
        updateTime();
        // If the time has been changed, enable the alarm.
        mEnabled = true;
    }

    private void updateTime() {
        if (Log.LOGV) {
            Log.v("updateTime " + mId);
        }
        mTimePref.setSummary(Alarms.formatTime(this, mHour, mMinutes,
                mRepeatPref.getDaysOfWeek()));
    }

    private void saveAlarm() {
        final String alert = mAlarmPref.getAlertString();
        Alarms.setAlarm(this, mId, mEnabled, mHour, mMinutes,
                mRepeatPref.getDaysOfWeek(), mVibratePref.isChecked(),
                mLabel.getText(), alert, mEffectPref.getValue(), regpath, ttspath);  // 10.11.19 add redmars

        if (mEnabled) {
            popAlarmSetToast(this, mHour, mMinutes,
                    mRepeatPref.getDaysOfWeek());
        }
    }

    /**
     * Write alarm out to persistent store and pops toast if alarm
     * enabled
     */
    private static void saveAlarm(
            Context context, int id, boolean enabled, int hour, int minute,
            Alarm.DaysOfWeek daysOfWeek, boolean vibrate, String label,
            String alert, String effect, String regpath, String ttspath, boolean popToast) {   // 10.11.03 add redmars
        if (Log.LOGV) Log.v("** saveAlarm " + id + " " + label + " " + enabled
                + " " + hour + " " + minute + " vibe " + "effect" + effect +
                "regpath" + regpath + "ttspath" + ttspath);
        
        // Fix alert string first
        Alarms.setAlarm(context, id, enabled, hour, minute, daysOfWeek, vibrate,
                label, alert, effect, regpath, ttspath);  // 10.11.19 add redmars

        if (enabled && popToast) {
            popAlarmSetToast(context, hour, minute, daysOfWeek);
        }
    }

    /**
     * Display a toast that tells the user how long until the alarm
     * goes off.  This helps prevent "am/pm" mistakes.
     */
    static void popAlarmSetToast(Context context, int hour, int minute,
                                 Alarm.DaysOfWeek daysOfWeek) {

        String toastText = formatToast(context, hour, minute, daysOfWeek);
        Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_LONG);
        ToastMaster.setToast(toast);
        toast.show();
    }

    /**
     * format "Alarm set for 2 days 7 hours and 53 minutes from
     * now"
     */
    static String formatToast(Context context, int hour, int minute,
                              Alarm.DaysOfWeek daysOfWeek) {
        long alarm = Alarms.calculateAlarm(hour, minute,
                                           daysOfWeek).getTimeInMillis();
        long delta = alarm - System.currentTimeMillis();;
        long hours = delta / (1000 * 60 * 60);
        long minutes = delta / (1000 * 60) % 60;
        long days = hours / 24;
        hours = hours % 24;

        String daySeq = (days == 0) ? "" :
                (days == 1) ? context.getString(R.string.day) :
                context.getString(R.string.days, Long.toString(days));

        String minSeq = (minutes == 0) ? "" :
                (minutes == 1) ? context.getString(R.string.minute) :
                context.getString(R.string.minutes, Long.toString(minutes));

        String hourSeq = (hours == 0) ? "" :
                (hours == 1) ? context.getString(R.string.hour) :
                context.getString(R.string.hours, Long.toString(hours));

        boolean dispDays = days > 0;
        boolean dispHour = hours > 0;
        boolean dispMinute = minutes > 0;

        int index = (dispDays ? 1 : 0) |
                    (dispHour ? 2 : 0) |
                    (dispMinute ? 4 : 0);

        String[] formats = context.getResources().getStringArray(R.array.alarm_set);
        return String.format(formats[index], daySeq, hourSeq, minSeq);
    }
 
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        mDeleteAlarmItem = menu.add(0, 0, 0, R.string.delete_alarm);
        mDeleteAlarmItem.setIcon(android.R.drawable.ic_menu_delete);

        if (AlarmClock.DEBUG) {
            mTestAlarmItem = menu.add(0, 0, 0, "TEST Alarm");
        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == mDeleteAlarmItem) {
            Alarms.deleteAlarm(this, mId);
            finish();
            return true;
        }
        if (AlarmClock.DEBUG) {
            if (item == mTestAlarmItem) {
                setTestAlarm();
                return true;
            }
        }

        return false;
    }


    /**
     * Test code: this is disabled for production build.  Sets
     * this alarm to go off on the next minute
     */
    void setTestAlarm() {

        // start with now
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        int nowHour = c.get(java.util.Calendar.HOUR_OF_DAY);
        int nowMinute = c.get(java.util.Calendar.MINUTE);

        int minutes = (nowMinute + 1) % 60;
        int hour = nowHour + (nowMinute == 0 ? 1 : 0);

        saveAlarm(this, mId, true, hour, minutes, mRepeatPref.getDaysOfWeek(),
                true, mLabel.getText(), mAlarmPref.getAlertString(), mEffectPref.getValue(), regpath, ttspath, true); // 10.11.16 add redmars
    }
     
    // TTS
	public void saveTtsSoundFile(String message) {

		File soundFile = new File(TTS_FILE_NAME);
//		File soundFile = new File("/sdcard/foo/bar");
	    if(! soundFile.isDirectory()) {
	    	soundFile.mkdirs();
	    }

		if (soundFile.exists())
			soundFile.delete();
		
		if(mTts.synthesizeToFile(message, null, TTS_FILE_NAME) == TextToSpeech.SUCCESS) {
			Toast.makeText(this, "Sound file created", Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(this, "Oops! Sound file not created", Toast.LENGTH_SHORT).show();
		}
	}
	
    private void lanunchTTSActivity(String message) {
    	/*
    	    	Intent intent = new Intent();
    	    	intent.setAction("com.humanoid.alarmplus.TTS");
    	    	intent.putExtra("TTS_MESSAGE", mTtsMessagePref.getSummary().toString());
    	    	startActivity(intent);	// ActivityNotFoundException
    	*/
    	Intent intent = new Intent(this, VoiceAlarmMessage.class);
	   	intent.putExtra("TTS_MESSAGE", message);
	   	startActivity(intent);
    	    	
    }

}
