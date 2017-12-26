package com.example.juni0_000.dailyhelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CalendarViewPage extends Fragment {
    private int calendarOffset;
    private static CalendarGenerator generator;
    private static CalendarAdapter mCalendarAdapter;
    private static ArrayList<DayInfo> mDayList;
    private static RelativeLayout layout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public CalendarViewPage() {
        calendarOffset = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        calendarOffset += getArguments().getInt("offset");
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_calendar, container, false);

        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        // 달력 사이즈는 화면 가로크기 기준 정사각형.
        int size = dm.widthPixels;

        generator = new CalendarGenerator(calendarOffset);
        // 달력 리스트 가져옴
        mDayList = generator.getDayInfo();

        // 어댑터에 달력과 화면크기 전달해서 어댑터 생성
        mCalendarAdapter = new CalendarAdapter(getActivity(), R.layout.day, mDayList, generator.getTodayPosition(), size, size);
        // 어댑터를 통해 달력 뿌리기

        ((GridView)layout.findViewById(R.id.CALENDAR)).setAdapter(mCalendarAdapter);
        ((TextView)layout.findViewById(R.id.INFO)).setText(generator.getInfo());
        return layout;
    }
}