package com.example.juni0_000.dailyhelper;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Administrator on 2017-11-10.
 */

// 하루의 데이터를 저장하는 클래스
class DayInfo {
    private int year, month, day;
    private int monthOffset;
    private boolean isHolly;

    public DayInfo(int year, int month, int day, int monthOffset, boolean isHolly) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.monthOffset = monthOffset;
        this.isHolly = isHolly;
    }

    public int getDay() { return day; }
    public int getMonth() { return month; }
    public int getYear() { return year; }
    public int getMonthOffset() { return monthOffset; }
    public String getYMD() { return year + "/" + month + "/" + day; }
    public boolean getIsHolly() { return isHolly; }
}

// 달력 생성 클래스
public class CalendarGenerator {
    private int todayPosition;
    private int year, month;
    private Calendar cal;
    private ArrayList<DayInfo> dayInfo;

    // year_offset : 년도 오프셋 받아옴. -1 이면 작년, 1 이면 내년
    // month_offset : 월 오프셋 받아옴. -1이면 저번달, 1 이면 다음달
    public CalendarGenerator(int offset) {
        todayPosition = 7;
        dayInfo = new ArrayList<>();
        cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, offset);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH)+1;
        generateCalendar();
    }

    public int getDay(int index) {
        return dayInfo.get(index).getDay();
    }
    public int getMonth(int index) {
        return dayInfo.get(index).getMonth();
    }
    public int getYear(int index) {
        return dayInfo.get(index).getYear();
    }
    public String getInfo() {
        return year + "년 " + month + "월";
    }
    public boolean getIsHolly(int index) {
        return dayInfo.get(index).getIsHolly();
    }

    public void generateCalendar() {
        int week = cal.get(Calendar.DAY_OF_WEEK);
        int today = cal.get(Calendar.DATE);
        int start = week - (today - 1)%7;// 1일의 요일 구하기.
        if(start <= 0) start += 7;
        int max = cal.getActualMaximum(Calendar.DATE);

        // 이번달의 1일~마지막일, 다음달 1일부터 남은 칸만큼 리스트에 삽입
        int tmpMonth = month;
        int monthOffset = 0;
        for(int i = start-1, day = 1; i < 42; i++, day++) {
            if(day > max) {
                day = 1;
                tmpMonth++;
                monthOffset = 1;
            }
            dayInfo.add(new DayInfo(year, tmpMonth, day, monthOffset, false));
            if(day < today && monthOffset == 0) todayPosition++;
        }

        // 저번달 요일 남은것 리스트에 삽입(앞에)
        cal.add(Calendar.MONTH, -1);
        int prev = cal.getActualMaximum(Calendar.DATE);
        for(int j = start-2; j >= 0 ; j--) {
            dayInfo.add(0, new DayInfo(year, month-1, prev--, -1, false));
            todayPosition++;
        }

        // 요일 삽입
        // 리스트의 처음부터 7개 요소가 요일정보가 됨
        for(int i = 6; i >= 0; i--) dayInfo.add(0, new DayInfo(0, 0, i, 0, false));
    }

    public ArrayList<DayInfo> getDayInfo() { return dayInfo; }
    public int getTodayPosition() { return todayPosition; }
}
