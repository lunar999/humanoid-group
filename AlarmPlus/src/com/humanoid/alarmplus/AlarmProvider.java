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
 
 
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import com.humanoid.alarmplus.AlarmConstantIf;
import java.io.File;

public class AlarmProvider extends ContentProvider {
    private SQLiteOpenHelper mOpenHelper;

    private static final int ALARMS = 1;
    private static final int ALARMS_ID = 2;
    private static final UriMatcher sURLMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
    	
//    	Uri authorities 수정함 <김병우>
        sURLMatcher.addURI("com.humanoid.alarmplus", "alarm", ALARMS);
        sURLMatcher.addURI("com.humanoid.alarmplus", "alarm/#", ALARMS_ID);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "alarms";
        private static final int DATABASE_VERSION = 6;  // 10.11.17 column 추가로 5 에서 6으로 up redmars

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE alarms (" +
                       "_id INTEGER PRIMARY KEY," +
                       "hour INTEGER, " +
                       "minutes INTEGER, " +
                       "daysofweek INTEGER, " +
                       "alarmtime INTEGER, " +
                       "enabled INTEGER, " +
                       "vibrate INTEGER, " +
                       "message TEXT, " +
                       "alert TEXT, " +                         
            		   "effect TEXT, " +  // 10.11.03 add redmars            
                       "recpath TEXT, " +                       
                       "ttspath TEXT);");  // 10.11.15 add redmars 

            // insert default alarms
            String insertMe = "INSERT INTO alarms " +
            "(hour, minutes, daysofweek, alarmtime, enabled, vibrate, message, alert, effect, recpath, ttspath) " +  // 10.11.03 add redmars          
                    "VALUES ";
            db.execSQL(insertMe + "(7, 0, 127, 0, 0, 1, '', '','0', 'humanoid/alarm/alarm_rec.mp4', 'humanoid/alarm/alarm_tts.wav');"); // 10.11.03 add redmars
            //db.execSQL(insertMe + "(8, 30, 31, 0, 0, 1, '', '','02');"); // 10.11.03 add redmars
            //db.execSQL(insertMe + "(9, 00, 0, 0, 0, 1, '', '','03');");  // 10.11.03 add redmars
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {        	        	
            if (Log.LOGV) Log.v(
                    "Upgrading alarms database from version " +
                    oldVersion + " to " + currentVersion +
                    ", which will add colum");
            //db.execSQL("DROP TABLE IF EXISTS alarms");
            //onCreate(db);            
            
            /* 기존 data 를  남기기 위해 table 을  drop 하지 않고 column 만 추가 10.11.17 redmars
             * 추가 컬럼에 기존 파일 경로 update
             */
            db.execSQL("ALTER TABLE alarms ADD COLUMN recpath TEXT");
            db.execSQL("ALTER TABLE alarms ADD COLUMN ttspath TEXT");
            db.execSQL("UPDATE alarms SET recpath = " + 
            		   "\"humanoid/alarm/alarm_rec.mp4\"" + ", ttspath = " +
            		   "\"humanoid/alarm/alarm_tts.wav\"");
        }
    }

    public AlarmProvider() {
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri url, String[] projectionIn, String selection,
            String[] selectionArgs, String sort) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // Generate the body of the query
        int match = sURLMatcher.match(url);
        switch (match) {
            case ALARMS:
                qb.setTables("alarms");
                break;
            case ALARMS_ID:
                qb.setTables("alarms");
                qb.appendWhere("_id=");
                qb.appendWhere(url.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URL " + url);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor ret = qb.query(db, projectionIn, selection, selectionArgs,
                              null, null, sort);

        if (ret == null) {
            if (Log.LOGV) Log.v("Alarms.query: failed");
        } else {
            ret.setNotificationUri(getContext().getContentResolver(), url);
        }

        return ret;
    }

    @Override
    public String getType(Uri url) {
        int match = sURLMatcher.match(url);
        switch (match) {
            case ALARMS:
                return "vnd.android.cursor.dir/alarms";
            case ALARMS_ID:
                return "vnd.android.cursor.item/alarms";
            default:
                throw new IllegalArgumentException("Unknown URL");
        }
    }

    @Override
    public int update(Uri url, ContentValues values, String where, String[] whereArgs) {
        int count;
        long rowId = 0;
        int match = sURLMatcher.match(url);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (match) {
            case ALARMS_ID: {
                String segment = url.getPathSegments().get(1);
                rowId = Long.parseLong(segment);
                count = db.update("alarms", values, "_id=" + rowId, null);
                break;
            }
            default: {
                throw new UnsupportedOperationException(
                        "Cannot update URL: " + url);
            }
        }
        if (Log.LOGV) Log.v("*** notifyChange() rowId: " + rowId + " url " + url);
        getContext().getContentResolver().notifyChange(url, null);
        return count;
    }

    @Override
    public Uri insert(Uri url, ContentValues initialValues) {
        if (sURLMatcher.match(url) != ALARMS) {
            throw new IllegalArgumentException("Cannot insert into URL: " + url);
        }

        ContentValues values;
        if (initialValues != null)
            values = new ContentValues(initialValues);
        else
            values = new ContentValues();

        if (!values.containsKey(Alarm.Columns.HOUR))
            values.put(Alarm.Columns.HOUR, 0);

        if (!values.containsKey(Alarm.Columns.MINUTES))
            values.put(Alarm.Columns.MINUTES, 0);

        if (!values.containsKey(Alarm.Columns.DAYS_OF_WEEK))
            values.put(Alarm.Columns.DAYS_OF_WEEK, 0);

        if (!values.containsKey(Alarm.Columns.ALARM_TIME))
            values.put(Alarm.Columns.ALARM_TIME, 0);

        if (!values.containsKey(Alarm.Columns.ENABLED))
            values.put(Alarm.Columns.ENABLED, 0);

        if (!values.containsKey(Alarm.Columns.VIBRATE))
            values.put(Alarm.Columns.VIBRATE, 1);

        if (!values.containsKey(Alarm.Columns.MESSAGE))
            values.put(Alarm.Columns.MESSAGE, "");

        if (!values.containsKey(Alarm.Columns.ALERT))
            values.put(Alarm.Columns.ALERT, "");

        if (!values.containsKey(Alarm.Columns.EFFECT))   // 10.11.03 add redmars
            values.put(Alarm.Columns.EFFECT, "");        
        
        if (!values.containsKey(Alarm.Columns.RECPATH))   // 10.11.11 add redmars
            values.put(Alarm.Columns.RECPATH, "");        
        
        if (!values.containsKey(Alarm.Columns.TTSPATH))   // 10.11.11 add redmars
            values.put(Alarm.Columns.TTSPATH, "");        
        
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert("alarms", Alarm.Columns.MESSAGE, values);
        if (rowId < 0) {
            throw new SQLException("Failed to insert row into " + url);
        }
        if (Log.LOGV) Log.v("Added alarm rowId = " + rowId);

        Uri newUrl = ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, rowId);
        getContext().getContentResolver().notifyChange(newUrl, null);
        return newUrl;
    }

    public int delete(Uri url, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        long rowId = 0;
        switch (sURLMatcher.match(url)) {
            case ALARMS:
                count = db.delete("alarms", where, whereArgs);
                break;
            case ALARMS_ID:
                String segment = url.getPathSegments().get(1);
                rowId = Long.parseLong(segment);
                if (TextUtils.isEmpty(where)) {
                    where = "_id=" + segment;
                } else {
                    where = "_id=" + segment + " AND (" + where + ")";
                }
                count = db.delete("alarms", where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete from URL: " + url);
        }

        getContext().getContentResolver().notifyChange(url, null);
        return count;
    }
}
