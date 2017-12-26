package com.example.juni0_000.dailyhelper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ScheduleUpdateActivity extends AppCompatActivity {
    private SQLiteDatabase database;
    private final String DATABASE_NAME = "helperDatabase";
    private static Context mContext;

    private EditText title, place, content;
    private TextView startTime, endTime, alarmTime;
    private RatingBar priority;
    private ToggleButton alarmOK;
    private Button cancel, add;
    private TimeSetDialog timeSetDialog;
    private AlarmTimeSetDialog AlarmtimeSetDialog;
    private int[] startTimeSet = new int[5];
    private int[] endTimeSet = new int[5];
    private int[] alarmTimeSet = new int[3];

    private int id;
    private Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_add);
        id = getIntent().getIntExtra("scheduleID",0);

        initView();
        c = findRecord();
        setInfo(c);

        mContext = this;

        setScheduleTimeListener();
        setCancelButton();
        setAddButton();
        setAlarmTimeListener();
        setAlarmOKButton();
    }

    public void initView() {
        title = (EditText)findViewById(R.id.TITLE);
        place = (EditText)findViewById(R.id.PLACE);
        content = (EditText)findViewById(R.id.CONTENT);

        startTime = (TextView)findViewById(R.id.START_TIME);
        endTime = (TextView)findViewById(R.id.END_TIME);
        alarmTime = (TextView)findViewById(R.id.ALARM_OFFSET);

        priority = (RatingBar)findViewById(R.id.PRIORITY);

        alarmOK = (ToggleButton)findViewById(R.id.ALARM_OK);

        cancel = (Button)findViewById(R.id.CANCEL);
        add = (Button)findViewById(R.id.ADD);
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

    public void setAlarmOKButton() {
        alarmOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!alarmOK.isChecked()) Toast.makeText(getApplicationContext(), "알람을 주지않습니다.", Toast.LENGTH_SHORT).show();
                else Toast.makeText(getApplicationContext(), "알람을 줍니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setAlarmTimeListener() {
        alarmTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmtimeSetDialog = new AlarmTimeSetDialog(mContext,1);
                AlarmtimeSetDialog.show();
            }
        });
    }

    public void setCancelButton() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setAddButton() {
        add.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if(!checkInput()) return;
                Log.i("add","saving schedule");
                StringBuilder sql = new StringBuilder("update schedule ");
                sql.append("set title = '"+ title.getText().toString()+"'");
                sql.append(", priority = " + ((int)(priority.getRating())));
                sql.append(", start_year = " + startTimeSet[0]);
                sql.append(", start_month = " + startTimeSet[1]);
                sql.append(", start_day = " + startTimeSet[2]);
                sql.append(", start_hour = " + startTimeSet[3]);
                sql.append(", start_min = " + startTimeSet[4]);
                sql.append(", end_year = " + endTimeSet[0]);
                sql.append(", end_month = " + endTimeSet[1]);
                sql.append(", end_day = " + endTimeSet[2]);
                sql.append(", end_hour = " + endTimeSet[3]);
                sql.append(", end_min = " + endTimeSet[4]);
                sql.append(", place = '"+ place.getText().toString()+"'");
                sql.append(", content = '"+ content.getText().toString()+"'");
                if(!alarmOK.isChecked()) {
                    sql.append(", alarm_hour = " + 0);
                    sql.append(", alarm_min = " + 0);
                    sql.append(", alarm_ok = " + 0);
                }
                else {
                    sql.append(", alarm_hour = " + alarmTimeSet[0]);
                    sql.append(", alarm_min = " + alarmTimeSet[1]);
                    sql.append(", alarm_ok = " + 1);
                }
                sql.append(" where id = " + id + ";");
                database.execSQL(sql.toString());
                Toast.makeText(getApplicationContext(), " 스케쥴 수정!", Toast.LENGTH_LONG).show();

                if(alarmOK.isChecked()) {
                    int[] alarmSetting = {startTimeSet[0], startTimeSet[1], startTimeSet[2], alarmTimeSet[0], alarmTimeSet[1]};
                    ((MainActivity)MainActivity.mContext).setAlarm(alarmSetting, id);
                }

                MainActivity.showSchedules(startTimeSet[0], startTimeSet[1], startTimeSet[2]);
                ((MainActivity)MainActivity.mContext).initCalendar();
                finish();
            }
        });
    }

    public boolean checkInput() {

        if(title.getText().toString().length() < 1) {
            Toast.makeText(getApplicationContext(), "제목을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(alarmOK.isChecked()) {
            if(alarmTime.getText().toString().equals("알람 시간 지정")) {
                Toast.makeText(getApplicationContext(), "알람 시간을 지정하지 않았습니다.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        for(int j =0;j<5;j++) {
            if (startTimeSet[j] < endTimeSet[j]){
                return true;
            }
            if (startTimeSet[j] > endTimeSet[j]){
                Toast.makeText(getApplicationContext(),"시작시간은 종료시간보다 늦을 수 없습니다.",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public void setScheduleTimeListener() {
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSetDialog = new TimeSetDialog(mContext, 2);
                Log.i("startdate","1");
                timeSetDialog.show();
                Log.i("startdate","2");
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSetDialog = new TimeSetDialog(mContext, 3);
                timeSetDialog.show();
            }
        });
    }
    public void setStartTime(int[] timeSetting) {
        String month = timeSetting[1] < 10 ? "/" + "0" + timeSetting[1] + "/" : "/" + timeSetting[1] + "/";
        String day = timeSetting[2] < 10 ? "0" + timeSetting[2] + " " : timeSetting[2] + " ";
        String hour = timeSetting[3] < 10 ? "0" + timeSetting[3] + ":" : timeSetting[3] + ":";
        String min = timeSetting[4] < 10 ? "0" + timeSetting[4] : timeSetting[4] + "";
        startTime.setText(timeSetting[0] + month + day + hour + min);

        startTimeSet = timeSetting;
    }

    public void setEndTime(int[] timeSetting) {
        String month = timeSetting[1] < 10 ? "/" + "0" + timeSetting[1] + "/" : "/" + timeSetting[1] + "/";
        String day = timeSetting[2] < 10 ? "0" + timeSetting[2] + " " : timeSetting[2] + " ";
        String hour = timeSetting[3] < 10 ? "0" + timeSetting[3] + ":" : timeSetting[3] + ":";
        String min = timeSetting[4] < 10 ? "0" + timeSetting[4] : timeSetting[4] + "";
        endTime.setText(timeSetting[0] + month + day + hour + min);

        endTimeSet = timeSetting;
    }
    public void setAlarmTime(int[] timeSetting) {
        String month = timeSetting[0] < 10 ? "0" + timeSetting[0]+":" : timeSetting[0]+":";
        String day = timeSetting[1] < 10 ? "0" + timeSetting[1] : timeSetting[1]+"";
        alarmTime.setText("알람시간 " + month+day);

        alarmTimeSet = timeSetting;
    }
}
