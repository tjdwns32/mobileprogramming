package com.example.juni0_000.dailyhelper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

// 달력을 뿌려주는 역할.

public class CalendarAdapter extends BaseAdapter {
    private int todayPosition;
    private int width, height;
    private ArrayList<DayInfo> mDayList;
    private Context mContext;
    private int mResource;
    private LayoutInflater mLiInflater;
    private TextView focusTextView;

    /**
     * Adpater 생성자
     *
     * @param context      컨텍스트
     * @param textResource 레이아웃 리소스
     * @param dayList      날짜정보가 들어있는 리스트
     */
    public CalendarAdapter(Context context, int textResource, ArrayList<DayInfo> dayList, int todayPosition, int width, int height) {
        this.width = width;
        this.height = height;
        this.mContext = context;
        this.mDayList = dayList;
        this.mResource = textResource;
        this.mLiInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.todayPosition = todayPosition;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mDayList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mDayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String[] months = {"일", "월", "화", "수", "목", "금", "토"};
        DayInfo day = mDayList.get(position);

        final DayViewHolder dayViewHolder;

        if (convertView == null) {
            convertView = mLiInflater.inflate(mResource, null);

            convertView.setLayoutParams(new GridView.LayoutParams(width / 7, height / 7));

            dayViewHolder = new DayViewHolder();
            dayViewHolder.tvDay = (TextView) convertView.findViewById(R.id.day_cell_tv_day);
            convertView.setTag(dayViewHolder);
        } else {
            dayViewHolder = (DayViewHolder) convertView.getTag();
        }

        if (day != null) {
            if (position < 7) {
                dayViewHolder.tvDay.setText(months[position]);
                dayViewHolder.tvDay.setBackgroundColor(Color.WHITE);
                switch (position) {
                    case 0:
                        dayViewHolder.tvDay.setTextColor(Color.RED);
                        break;
                    case 6:
                        dayViewHolder.tvDay.setTextColor(Color.BLUE);
                        break;
                    default:
                        dayViewHolder.tvDay.setTextColor(Color.BLACK);
                }
            } else {
                dayViewHolder.tvDay.setText(day.getDay() + " ");
                dayViewHolder.tvDay.setId(position);

                // 오늘 날짜에 해당하는 버튼
                if (position == todayPosition) {
                    focusTextView = dayViewHolder.tvDay;
                    focusTextView.setBackground(mContext.getDrawable(R.drawable.onclick));
                    focusTextView.setTextColor(Color.rgb(100, 200, 230));
                } else if (day.getMonthOffset() != 0) {
                    dayViewHolder.tvDay.setTextColor(Color.GRAY);
                } else if (position % 7 == 0) {
                    dayViewHolder.tvDay.setTextColor(Color.RED);
                } else if (position % 7 == 6) {
                    dayViewHolder.tvDay.setTextColor(Color.BLUE);
                } else {
                    dayViewHolder.tvDay.setTextColor(Color.BLACK);
                }
                final DayInfo info = mDayList.get(dayViewHolder.tvDay.getId());
                dayViewHolder.tvDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        focusTextView.setBackgroundColor(Color.WHITE);
                        focusTextView = dayViewHolder.tvDay;
                        focusTextView.setBackground(mContext.getDrawable(R.drawable.onclick));
                        ((MainActivity) MainActivity.mContext).showSchedules(info.getYear(), info.getMonth(), info.getDay());
                    }
                });
                Cursor c = ((MainActivity) MainActivity.mContext).searchSchdules(info.getYear(), info.getMonth(), info.getDay());
                if (c.getCount() != 0) dayViewHolder.tvDay.setTextColor(Color.rgb(0, 255, 0));
            }
        }
        return convertView;
    }

    public class DayViewHolder {
        public TextView tvDay;
    }
}

