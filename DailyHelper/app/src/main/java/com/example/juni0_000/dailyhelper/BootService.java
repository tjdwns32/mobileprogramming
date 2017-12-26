package com.example.juni0_000.dailyhelper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Administrator on 2017-12-14.
 */

public class BootService extends Service {
    private static SQLiteDatabase database;
    private final String DATABASE_NAME = "helperDatabase";
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(), "Daily Helper Setting..", Toast.LENGTH_SHORT).show();
        initDatabase();
    }

    public IBinder onBind(Intent intetn) { return null; }

    public void initDatabase() {
        database = openOrCreateDatabase(DATABASE_NAME, Activity.MODE_PRIVATE, null);
        database.execSQL("create table if not exists schedule(" +
                "id integer primary key autoincrement, " +
                "title text not null, " +
                "priority integer not null," +
                "start_year integer not null, " +
                "start_month integer not null, " +
                "start_day  integer not null, " +
                "start_hour integer not null," +
                "start_min integer not null," +
                "end_year integer not null, " +
                "end_month integer not null, " +
                "end_day  integer not null, " +
                "end_hour integer not null," +
                "end_min integer not null," +
                "place text, " +
                "content text, " +
                "alarm_hour integer, " +
                "alarm_min integer, " +
                "alarm_ok integer not null)");
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        String id = "";
        Calendar calendar = Calendar.getInstance();
        //알람시간 calendar에 set해주기

        Cursor result = database.rawQuery("SELECT id, start_year, start_month, start_day," +
                        "alarm_hour, alarm_min from schedule " +
                        "where (start_year >= " + calendar.get(Calendar.YEAR) +
                        " and start_month >= " + (calendar.get(Calendar.MONTH)+1) +
                        " and start_day >= " + calendar.get(Calendar.DAY_OF_MONTH) +
                        " and alarm_hour >= " + calendar.get(Calendar.HOUR) +") or " +
                        " (start_year > " + calendar.get(Calendar.YEAR) + ") or " +
                        " (start_year >= " + calendar.get(Calendar.YEAR) +
                        " and start_month > " + (calendar.get(Calendar.MONTH)+1) +")" , null);

        for(int i = 0; i < result.getCount(); i++) {
            result.moveToNext();
            id = id + result.getInt(0) + ",";
            int[] alarmSetting = {result.getInt(1), result.getInt(2),result.getInt(3),result.getInt(4),result.getInt(5)};
            new AlarmHATT(getApplicationContext()).setAlarm(alarmSetting, result.getInt(0));
        }

        stopSelf();
        return START_NOT_STICKY;
    }
    public class AlarmHATT {
        private Context context;

        public AlarmHATT(Context context) {
            this.context = context;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void setAlarm(int[] alarmSetting, int scheduleID) {
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            Bundle bundle = new Bundle();
            bundle.putInt("scheduleID", scheduleID);
            intent.putExtras(bundle);
            PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.YEAR, alarmSetting[0]);
            calendar.set(Calendar.MONTH, alarmSetting[1]-1);
            calendar.set(Calendar.DATE, alarmSetting[2]);
            calendar.set(Calendar.HOUR_OF_DAY, alarmSetting[3]);
            calendar.set(Calendar.MINUTE, alarmSetting[4]);
            calendar.set(Calendar.SECOND, 00);
            //알람 예약

            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
    }
}
