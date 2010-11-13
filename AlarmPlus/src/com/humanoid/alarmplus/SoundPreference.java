package com.humanoid.alarmplus;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class SoundPreference extends ListPreference {

	public SoundPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		setEntries(context.getResources().getTextArray(R.array.sound_choice));
		setEntryValues(context.getResources().getTextArray(R.array.sound_choice));
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {

		}
		else {

		}
	}

	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		CharSequence[] entries = getEntries();
		CharSequence[] entryValues = getEntryValues();

		//        builder.setMultiChoiceItems(
				//                entries, mDaysOfWeek.getBooleanArray(),
		//                new DialogInterface.OnMultiChoiceClickListener() {
		//                    public void onClick(DialogInterface dialog, int which,
		//                            boolean isChecked) {
		//                        mNewDaysOfWeek.set(which, isChecked);
		//                    }
		//                });

		builder.setSingleChoiceItems(entries, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
	}

}
