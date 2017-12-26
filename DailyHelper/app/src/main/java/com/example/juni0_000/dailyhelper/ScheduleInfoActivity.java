package com.example.juni0_000.dailyhelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by Administrator on 2017-12-05.
 */

public class ScheduleInfoActivity extends AppCompatActivity {
    private int id;
    private static SQLiteDatabase database;
    private final String DATABASE_NAME = "helperDatabase";
    private Vibrator vibrator;
    private int isVibrating = 0;
    private final long[] vibPattern = {0, 100, 1000};

    private TextView title, place, content;
    private TextView startTime, endTime, alarmTime;
    private RatingBar priority;
    private ToggleButton alarmOK;
    private int[] startTimeSet = new int[5], endTimeSet = new int[5], alarmTimeSet = new int[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_info);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);

        initView();
        Cursor c = findRecord();
        setInfo(c);

        if(intent.getBooleanExtra("vib", false)) {
            startVib();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_schedule_info, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.EXIT:
                if(isVibrating == 1) vibrator.cancel();
                isVibrating = 0;
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initView() {
        title = (TextView)findViewById(R.id.TITLE);
        place = (TextView)findViewById(R.id.PLACE);
        content = (TextView)findViewById(R.id.CONTENT);

        startTime = (TextView)findViewById(R.id.START_TIME);
        endTime = (TextView)findViewById(R.id.END_TIME);
        alarmTime = (TextView)findViewById(R.id.ALARM_OFFSET);

        priority = (RatingBar)findViewById(R.id.PRIORITY);

        alarmOK = (ToggleButton)findViewById(R.id.ALARM_OK);
    }

    public Cursor findRecord() {
        database = openOrCreateDatabase(DATABASE_NAME, Activity.MODE_PRIVATE, null);
        return database.rawQuery("select * from schedule " +
                "where id = " + id,null);
    }

    public void setInfo(Cursor c) {
        c.moveToNext();
        title.setText(c.getString(1));
        priority.setRating((float)c.getInt(2));
        startTimeSet[0]= c.getInt(3);
        startTimeSet[1]= c.getInt(4);
        startTimeSet[2]= c.getInt(5);
        startTimeSet[3]= c.getInt(6);
        startTimeSet[4]= c.getInt(7);
        setStartTime(startTimeSet);

        endTimeSet[0]= c.getInt(8);
        endTimeSet[1]= c.getInt(9);
        endTimeSet[2]= c.getInt(10);
        endTimeSet[3]= c.getInt(11);
        endTimeSet[4]= c.getInt(12);
        setEndTime(endTimeSet);

        place.setText(c.getString(13));
        content.setText(c.getString(14));

        alarmTimeSet[0] = c.getInt(15);
        alarmTimeSet[1] = c.getInt(16);
        alarmTimeSet[2] = c.getInt(17);
        if(alarmTimeSet[2] == 1){
            alarmOK.setChecked(true);
            setAlarmTime(alarmTimeSet);
        }
    }

    public void startVib() {
        isVibrating =1;
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(vibPattern, 0);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();

        if(isVibrating==1) {
            vibrator.cancel();
        }
    }

    public void onBackpressed() {
        if(isVibrating == 1) vibrator.cancel();
    }
    public void setStartTime(int[] timeSetting) {
        String month = timeSetting[1] < 10 ? "/" + "0" + timeSetting[1] + "/" : "/" + timeSetting[1] + "/";
        String day = timeSetting[2] < 10 ? "0" + timeSetting[2] + " " : timeSetting[2] + " ";
        String hour = timeSetting[3] < 10 ? "0" + timeSetting[3] + ":" : timeSetting[3] + ":";
        String min = timeSetting[4] < 10 ? "0" + timeSetting[4] : timeSetting[4] + "";
        startTime.setText(timeSetting[0] + month + day + hour + min);
    }

    public void setEndTime(int[] timeSetting) {
        String month = timeSetting[1] < 10 ? "/" + "0" + timeSetting[1] + "/" : "/" + timeSetting[1] + "/";
        String day = timeSetting[2] < 10 ? "0" + timeSetting[2] + " " : timeSetting[2] + " ";
        String hour = timeSetting[3] < 10 ? "0" + timeSetting[3] + ":" : timeSetting[3] + ":";
        String min = timeSetting[4] < 10 ? "0" + timeSetting[4] : timeSetting[4] + "";
        endTime.setText(timeSetting[0] + month + day + hour + min);
    }
    public void setAlarmTime(int[] timeSetting) {
        String hour = timeSetting[0] < 10 ? "0" + timeSetting[0]+":" : timeSetting[0]+":";
        String min = timeSetting[1] < 10 ? "0" + timeSetting[1] : timeSetting[1]+"";
        alarmTime.setText("알람시간 " + hour+min);
    }
}
