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

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 2017-12-05.
 */

public class ScheduleAddActivity extends AppCompatActivity {
    private SQLiteDatabase database;
    private final String DATABASE_NAME = "helperDatabase";
    private static Context mContext;
    private final String[] dbCoulmns = {
            "id", "title", "priority",
            "start_year", "start_month", "start_day", "start_hour", "start_min",
            "end_year", "end_month", "end_day", "end_hour","end_min",
            "place", "content",
            "alarm_hour","alarm_min", "alarm_ok"};

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_add);

        initView();
        mContext = this;
        database = openOrCreateDatabase(DATABASE_NAME, Activity.MODE_PRIVATE, null);
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

        initDate();//사용자가 시작, 종료시간을 따로 저장하지 않으면 저장하는 시간을 기본값으로 가진다.

        setCancelButton();
        setAddButton();
        setScheduleTimeListener();
        setAlarmTimeListener();
        setAlarmOKButton();
    }

    public void initDate(){
        SimpleDateFormat curFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String formattedDate = curFormat.format(Calendar.getInstance().getTime());
        Log.i("date",Calendar.getInstance().getTime().toString());
        String dateAndTime[] = formattedDate.split(" ");
        String dateInfo[] = dateAndTime[0].split("/");
        String timeInfo[] = dateAndTime[1].split(":");

        startTimeSet[0] = Integer.parseInt(dateInfo[0]);
        startTimeSet[1] = Integer.parseInt(dateInfo[1]);
        startTimeSet[2] = Integer.parseInt(dateInfo[2]);
        startTimeSet[3] = Integer.parseInt(timeInfo[0]);
        startTimeSet[4] = Integer.parseInt(timeInfo[1]);

        endTimeSet[0] = Integer.parseInt(dateInfo[0]);
        endTimeSet[1] = Integer.parseInt(dateInfo[1]);
        endTimeSet[2] = Integer.parseInt(dateInfo[2]);
        endTimeSet[3] = Integer.parseInt(timeInfo[0]);
        endTimeSet[4] = Integer.parseInt(timeInfo[1]);

        startTime.setText(formattedDate);
        endTime.setText(formattedDate);
    }

    public void setCancelButton() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
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
                StringBuilder sql = new StringBuilder("insert into schedule(");
                for(int i = 1; i < dbCoulmns.length; i++) {
                    sql.append(dbCoulmns[i]);
                    if(i < dbCoulmns.length - 1) sql.append(",");
                }
                sql.append(") values(");
                sql.append("'" + title.getText().toString()+"',");
                sql.append(((int)(priority.getRating())) + ",");
                sql.append(startTimeSet[0] +"," + startTimeSet[1] +"," + startTimeSet[2] + "," + startTimeSet[3] +"," + startTimeSet[4] +",");
                sql.append(endTimeSet[0] +"," + endTimeSet[1] +"," + endTimeSet[2] + "," + endTimeSet[3] +"," + endTimeSet[4] +",");
                sql.append("'" + place.getText().toString() + "',");
                sql.append("'" + content.getText().toString() + "',");
                if(!alarmOK.isChecked()) {
                    sql.append(0);
                    sql.append(",");
                    sql.append(0);
                    sql.append(",");
                    sql.append(0);
                }
                else {
                    sql.append(alarmTimeSet[0]);
                    sql.append(",");
                    sql.append(alarmTimeSet[1]);
                    sql.append(",");
                    sql.append(1);
                }
                sql.append(")");
                database.execSQL(sql.toString());
                Toast.makeText(getApplicationContext(), " 스케쥴 추가!", Toast.LENGTH_LONG).show();

                if(alarmOK.isChecked()) {
                    Cursor result = database.rawQuery("SELECT id from schedule ", null);
                    result.moveToLast();
                    int id = result.getInt(0);
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
                timeSetDialog = new TimeSetDialog(mContext, 0);
                timeSetDialog.show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSetDialog = new TimeSetDialog(mContext, 1);
                timeSetDialog.show();
            }
        });
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
                AlarmtimeSetDialog = new AlarmTimeSetDialog(mContext,0);
                AlarmtimeSetDialog.show();
            }
        });
    }

    public void setStartTime(int[] timeSetting) {
        String month = timeSetting[1] < 10 ? "/" + "0" + timeSetting[1]+"/" : "/" + timeSetting[1]+"/";
        String day = timeSetting[2] < 10 ? "0" + timeSetting[2]+" " : timeSetting[2]+" ";
        String hour = timeSetting[3] < 10 ? "0" + timeSetting[3]+":" :timeSetting[3]+":";
        String min = timeSetting[4] < 10 ? "0" + timeSetting[4] : timeSetting[4] +"";
        startTime.setText(timeSetting[0] + month + day + hour + min);

        startTimeSet = timeSetting;
    }

    public void setEndTime(int[] timeSetting) {
        String month = timeSetting[1] < 10 ? "/" + "0" + timeSetting[1]+"/" : "/" + timeSetting[1]+"/";
        String day = timeSetting[2] < 10 ? "0" + timeSetting[2]+" " : timeSetting[2]+" ";
        String hour = timeSetting[3] < 10 ? "0" + timeSetting[3]+":" :timeSetting[3]+":";
        String min = timeSetting[4] < 10 ? "0" + timeSetting[4] : timeSetting[4] +"";
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
