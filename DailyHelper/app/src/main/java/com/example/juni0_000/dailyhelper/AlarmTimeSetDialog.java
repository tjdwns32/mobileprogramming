package com.example.juni0_000.dailyhelper;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

/**
 * Created by Administrator on 2017-12-15.
 */

public class AlarmTimeSetDialog extends Dialog {
    private Button complete, cancel;
    private TimePicker timePicker;
    Context mContext;
    int alarmFlag;

    public int[] timeSetting = new int[2];

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_time_set_dialog);
        initView();
        setButton();
    }

    public void initView() {
        complete = (Button)findViewById(R.id.COMPLETE);
        cancel = (Button)findViewById(R.id.CANCEL);
        timePicker = (TimePicker)findViewById(R.id.TIME);
    }

    public void setButton() {
        complete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                setTimeInfo();
                if(alarmFlag == 0)
                    ((ScheduleAddActivity)mContext).setAlarmTime(timeSetting);
                else
                    ((ScheduleUpdateActivity)mContext).setAlarmTime(timeSetting);
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    public AlarmTimeSetDialog(Context context,int alarmFlag) {
        super(context, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);
        mContext = context;
        this.alarmFlag = alarmFlag;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setTimeInfo() {
        timeSetting[0] = timePicker.getHour();
        timeSetting[1] = timePicker.getMinute();
    }
}


