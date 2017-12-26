package com.example.juni0_000.dailyhelper;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * Created by Administrator on 2017-12-15.
 */

public class TimeSetDialog extends Dialog {
    private TextView textView;
    private Button complete, cancel;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private int timeFlag;
    Context mContext;

    public int[] timeSetting = new int[5];

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_set_dialog);
        initView();
        setButton();
    }

    public void initView() {
        textView = (TextView)findViewById(R.id.TEXTVIEW);
        complete = (Button)findViewById(R.id.COMPLETE);
        cancel = (Button)findViewById(R.id.CANCEL);
        datePicker = (DatePicker)findViewById(R.id.DATE);
        timePicker = (TimePicker)findViewById(R.id.TIME);

        if(timeFlag == 0) textView.setText("시작날짜");
        else textView.setText("종료날짜");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setTimeInfo() {
        timeSetting[0] = datePicker.getYear();
        timeSetting[1] = datePicker.getMonth()+1;
        timeSetting[2] = datePicker.getDayOfMonth();
        timeSetting[3]= timePicker.getHour();
        timeSetting[4] = timePicker.getMinute();
    }

    public void setButton() {
        complete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                setTimeInfo();
                switch(timeFlag){
                    case 0:
                        ((ScheduleAddActivity)mContext).setStartTime(timeSetting);
                        break;
                    case 1:
                        ((ScheduleAddActivity)mContext).setEndTime(timeSetting);
                        break;
                    case 2:
                        ((ScheduleUpdateActivity)mContext).setStartTime(timeSetting);
                        break;
                    case 3:
                        ((ScheduleUpdateActivity)mContext).setEndTime(timeSetting);
                        break;
                    default:
                        break;
                }
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
    public TimeSetDialog(Context context, int timeFlag) {
        super(context, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);
        mContext = context;
        this.timeFlag = timeFlag;
    }

}

