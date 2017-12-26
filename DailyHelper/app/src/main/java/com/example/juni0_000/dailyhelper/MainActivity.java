package com.example.juni0_000.dailyhelper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements ActionMode.Callback, AdapterView.OnItemLongClickListener{
    private final String DATABASE_NAME = "helperDatabase";
    private static ListView scheduleList;
    private static ArrayAdapter scheduleAdapter;
    private static String[] titles;
    private static int[] ids;
    private static CalendarViewPager calendarViewPager;
    public static Context mContext;
    final static int MAX_PAGE = 1200;                         //View Pager의 총 페이지 갯수를 나타내는 변수 선언
    static int dayInfo[] = new int[4];

    private static SQLiteDatabase database;
    private final String[] dbCoulmns = {
            "id", "title", "priority",
            "start_year", "start_month", "start_day", "start_hour", "start_min",
            "end_year", "end_month", "end_day", "end_hour","end_min",
            "place", "content",
            "alarm_hour","alarm_min", "alarm_ok"};

    private final String[] dbTables = {"schedule"};

    ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        initCalendar();
        initDatabase();
        initScheduleList();
    }

    public void initCalendar() {
        calendarViewPager = (CalendarViewPager)findViewById(R.id.viewPager);        //Viewpager 선언 및 초기화
        calendarViewPager.setAdapter(new adapter(getSupportFragmentManager()));     //선언한 viewpager에 adapter를 연결
        calendarViewPager.setCurrentItem(MAX_PAGE/2);
    }

    private class adapter extends FragmentStatePagerAdapter {                    //adapter클래스
        public adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            CalendarViewPage calendarViewPage = new CalendarViewPage();
            Bundle offset = new Bundle(1);
            calendarViewPage.setArguments(offset);
            offset.putInt("offset",position - MAX_PAGE/2);
            return calendarViewPage;
        }

        @Override
        public int getCount() {
            return MAX_PAGE;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_calendar, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.WRITE:
                startActivity(new Intent(MainActivity.this, ScheduleAddActivity.class));
                return true;
            case R.id.TIMETABLE:
                startActivity(new Intent(MainActivity.this,TimeTableActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initScheduleList() {
        scheduleList = (ListView)findViewById(R.id.SCHEDULE_LIST);
        scheduleList.setOnItemClickListener(onClickListItem);
        scheduleList.setOnItemLongClickListener(this);
        Calendar calendar = Calendar.getInstance();

        showSchedules(calendar.get(Calendar.YEAR),(calendar.get(Calendar.MONTH)+1), calendar.get(Calendar.DAY_OF_MONTH));
    }
    // 리스트뷰 아이템(스케쥴 제목) 클릭 이벤트
    private AdapterView.OnItemClickListener onClickListItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, ScheduleInfoActivity.class);
            intent.putExtra("id", ids[position]);
            startActivity(intent);
        }
    };


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (mActionMode != null) {
            return false;
        }
        // 컨텍스트 액션 모드 시작
        mActionMode = this.startActionMode(this);
        view.setSelected(true);
        dayInfo[3] = position;
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setAlarm(int[] alarmSetting, int scheduleID) {

        new AlarmHATT(getApplicationContext()).setAlarm(alarmSetting, scheduleID);
    }

    public void initDatabase() {
        database = openOrCreateDatabase(DATABASE_NAME, Activity.MODE_PRIVATE, null);
        //database.execSQL("drop table schedule");
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
    // 저장된 스케쥴 어댑터에 뿌려주기

    public static Cursor searchSchdules(int year, int month, int day) {
        Cursor result = database.rawQuery("SELECT title, id, priority, " +
                "start_year, start_month, start_day, start_hour, start_min," +
                "end_year, end_month, end_day, end_hour, end_min from schedule " +
                "where (start_year <= " + year +
                " and start_month <= " + month +
                " and start_day <= " + day +
                " and end_year >= " + year +
                " and end_month >= " + month +
                " and end_day >= " + day +") or" +

                " (start_year <= " + year +
                " and start_month <= " + month +
                " and start_day <= " + day +
                " and end_year > " + year +") or" +

                " (end_year >= " + year +
                " and end_month >= " + month +
                " and end_day >= " + day +
                " and start_year < " + year +")", null);

        return result;
    }
    public static void showSchedules(int year, int month, int day) {
        Cursor c = searchSchdules(year, month, day);

        titles = new String[c.getCount()];
        ids = new int[c.getCount()];

        for(int i = 0; i < titles.length; i++) {
            c.moveToNext();
            String title = c.getString(0);
            int priority = c.getInt(2);
            int start_year = c.getInt(3);
            int start_month = c.getInt(4);
            int start_day = c.getInt(5);
            int start_hour = c.getInt(6);
            int start_min = c.getInt(7);
            int end_year = c.getInt(8);
            int end_month = c.getInt(9);
            int end_day = c.getInt(10);
            int end_hour = c.getInt(11);
            int end_min = c.getInt(12);

            String star = "";
            String start = getTimeYYMMDDHM(start_year,start_month,start_day,start_hour, start_min);
            String end = getTimeYYMMDDHM(end_year,end_month,end_day,end_hour, end_min);
            for(int p = 0; p < priority; p++) star += "★";
            titles[i] = title + " " + star + "\n" + start + " ~ " + end;

            ids[i] = c.getInt(1);
        }
        scheduleAdapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, titles);
        scheduleList.setAdapter(scheduleAdapter);
    }

    public static String getTimeYYMMDDHM(int year, int month, int day, int hour, int min) {
        String mo = month < 10 ? "/" + "0" + month + "/" : "/" + month + "/";
        String d = day < 10 ? "0" + day + " " :day + " ";
        String h = hour < 10 ? "0" + hour + ":" : hour + ":";
        String m = min < 10 ? "0" + min : min + "";

        return year + mo + d + h + m;
    }

    public static void setDayInfo(int year,int month,int day){
        dayInfo[0] = year;
        dayInfo[1] = month;
        dayInfo[2] = day;
    }

    public class AlarmHATT {
        private Context context;

        public AlarmHATT(Context context) {
            this.context = context;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void setAlarm(int[] alarmSetting, int scheduleID) {
            Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
            intent.putExtra("scheduleID",scheduleID);
            PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, scheduleID, intent, PendingIntent.FLAG_ONE_SHOT);

            releasePreviousAlarm(MainActivity.this,scheduleID,intent);

            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.YEAR, alarmSetting[0]);
            calendar.set(Calendar.MONTH, alarmSetting[1]-1);
            calendar.set(Calendar.DATE, alarmSetting[2]);
            calendar.set(Calendar.HOUR_OF_DAY, alarmSetting[3]);
            calendar.set(Calendar.MINUTE, alarmSetting[4]);
            calendar.set(Calendar.SECOND, 00);
            //알람 예약

            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            //예정된 시간에 알람이 배달되도록 설정
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////액션모드 코드////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
// MenuInflater 객체를 이용하여 컨텍스트 메뉴를 생성
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.actionmode_schedule_manage, menu);
        return true;
    }
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.schedule_update:
                Intent intent = new Intent(MainActivity.this,ScheduleUpdateActivity.class);
                intent.putExtra("scheduleID",ids[dayInfo[3]]);
                startActivity(intent);
                mode.finish();
                return true;
            case R.id.schedule_delete:
                database.execSQL("delete from schedule " +
                        "WHERE id = "+ ids[dayInfo[3]]+";");
                initCalendar();
                initScheduleList();
                mode.finish();
                return true;
            default:
                return false;
        }
    }
    public void releasePreviousAlarm(Context mContext, int requestCode, Intent intent){
        try {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, requestCode, intent,0);
            AlarmManager am=(AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
            am.cancel(pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 사용자가 컨텍스트 액션 모드를 빠져나갈 때 호출
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mActionMode = null;
    }
}