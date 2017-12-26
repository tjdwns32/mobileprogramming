package com.example.juni0_000.dailyhelper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class TimeTableActivity extends AppCompatActivity {
    GridView GridSchedule;  // 그리드 뷰 선언
    Boolean clickitem [] = new Boolean[72];     // 해당 포지션의 뷰가 클릭 됐는지 안됐는지 판단하는 배열
    ScheduleAdapter adapter;    // 어뎁터 클래스 선언
    int [] pos; // 선택된 뷰들의 position을 저장하기 위한 변수
    int num = 0;    // 선택된 뷰들의 개수
    int color;  // 표에 색상의 주기 위한 변수

    // db에서 받아오는 값들
    int situation[];      // 뷰 위치
    String name[];    // 강의명
    String place[];   // 강의실
    String teacher[];  // 교수
    int dbcolor[];      // 색상
    String note[];    // 메모

    int count;  //DB에 삽입되어있는 쿼리 개수

    // DB 생성되 관련된 변수
    private MySQLiteOpenHelper helper;
    String dbName = "st_file.db";
    int dbVersion = 1; // 데이터베이스 버전
    private SQLiteDatabase db;
    String tag = "SQLite"; // Log 에 사용할 tag

    int result = 0; // startactivityforresult 구분변수
    int width;
    int height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        // 다 클릭 안되있는 상태로 만들어줌
        for(int i = 0 ; i < 72 ; i++){
            clickitem[i] = false;
        }
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();

        width = dm.widthPixels;

        height = dm.heightPixels;
        // 그리드 뷰 선언
        GridSchedule = (GridView)findViewById(R.id.gridview);
        // 어뎁터 클래스 생성
        adapter = new ScheduleAdapter(getApplicationContext(), width, height);
        // 그리드 뷰를 어뎁터에 연결
        GridSchedule.setAdapter(adapter);

        // DB에서 받아오는 값을 넣기위한 배열
        situation = new int[72];
        name = new String[72];
        place = new String[72];
        teacher = new String[72];
        dbcolor = new int[72];
        note = new String[72];

        // DB 열기 or 생성
        helper = new MySQLiteOpenHelper(
                this,  // 현재 화면의 제어권자
                dbName,// db 이름
                null,  // 커서팩토리-null : 표준커서가 사용됨
                dbVersion);       // 버전

        try {
            db = helper.getWritableDatabase(); // 읽고 쓸수 있는 DB
            //db = helper.getReadableDatabase(); // 읽기 전용 DB select문
        } catch (SQLiteException e) {
            e.printStackTrace();
            Log.e(tag, "데이터베이스를 얻어올 수 없음");
            finish(); // 액티비티 종료
        }
        // 처음 앱을 실행할 때 디비에 있는 값들을 화면에 띄워줌
        select();   // db에서 값 가져오기
        for(int i = 0 ; i<count ; i++){
            adapter.mWeekTitleIds[situation[i]] = name[i];  // 강의명
            adapter.colorarray[situation[i]] = dbcolor[i];  // 색상
        }

        // 아이템 클릭 리스너
        GridSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭이 안되어 있을 때
                if( position > 5 || position%6 != 0 ){
                    if(clickitem[position] == false) {
                        clickitem[position] = true; // 클릭되있다고 바꿔줌
                        color = 0xFF999999;          // 회색
                        view.setBackgroundColor(color);         // 배경색 바꿔줌
                    }
                    else if(clickitem[position] == true){
                        clickitem[position] = false;    // 클릭이 안되어 있다고 바꿔줌
                        color = 0xFFFFFFFF;
                        int i=0;
                        // 색상주기
                        while(i<count){
                            // 해당 위치에 디비가 있을 때
                            if(position == situation[i]){
                                color = dbcolor[i];
                                break;
                            }
                            // 없을 때
                            else{
                                color = 0xFFFFFFFF;              // 흰색
                            }
                            i++;
                        }
                        view.setBackgroundColor(color);  // 배경색 바꿔줌
                    }
                }

            }
        });

    }
    // 디비에서 값 가져오기
    void select() {
        Cursor c = db.rawQuery("select * from mytable;", null);
        Cursor d = db.rawQuery("select * from mytable;", null);
        count = 0;
        int i = 0;
        while(d.moveToNext()){
            count++;    // db에 들어있는 쿼리 개수
        }
        while(c.moveToNext()) {
            situation[i] = c.getInt(1);      // 뷰 위치
            name[i] = c.getString(2);    // 강의명
            place[i] = c.getString(3);   // 강의실
            teacher[i] = c.getString(4);  // 교수
            dbcolor[i] = c.getInt(5);  // 표에 색상의 주기 위한 변수
            note[i] = c.getString(6);    // 메모
            i++;
        }

    }
    // 삭제
    void delete(int position){
        db.execSQL("delete from mytable where position="+position+";");
    }
    // 배열에 postion 값을 넣어주는 함수
    public void positionarray(){
        int count=0;
        num=0;
        // 선택된 뷰의 개수 파악
        for(int j = 0 ; j<72 ; j++){
            if(clickitem[j] == true){
                num++;
            }
        }
        pos = new int[num]; // 선택된 뷰만큼의 크기를 가진 배열 생성
        // 배열에 position 값 저장
        for(int k=0 ; k<72; k++){
            if(clickitem[k] == true){
                pos[count] = k;
                count++;
            }
        }
    }
    // 시간표 새로만들기 클릭 메소드
    public void add_onclick(View view){
        int i=0;
        positionarray();    // 배열 생성 및 position 값 넣기
        while(i<72){
            // 클릭된 아이템이 하나라도 있으면 add 액티비티 실행
            if(clickitem[i] == true){
                // pos( 위치값) 와 num(선택된 셀의 개수)을 보냄
                Intent intent = new Intent(getApplicationContext(), AddTableActivity.class);
                intent.putExtra("pos",pos);
                intent.putExtra("num",num);
                for(int j=0 ; j<num ;j++){
                    clickitem[pos[j]] = false;
                }
                startActivityForResult(intent,result); // 값을 받아오는 용으로 시작
                break;
            }
            // 클릭된 아이템이 끝까지 하나라도 없으면 토스 메세지 출력후 반복문 나감
            if(clickitem[i] == false && i == 54){
                Toast.makeText(this, "아이템을 하나 이상 선택해주세요!",Toast.LENGTH_LONG).show();
                break;
            }
            i++; // 증가
        }

    }
    // 시간표 수정/ 보기 클릭 메소드
    public void change_onclick(View view){
        positionarray();    // 배열 생성 및 position 값 넣기
        int i=0;
        int j=0;
        // 클릭된게 하나가 아니면 메세지 출력
        if( num != 1){
            Toast.makeText(this, "수정할 하나를 선택해주세요!", Toast.LENGTH_LONG).show();
        }
        // 클릭된게 하나면 AddActivity 실행 및 디비에서 받아온 값 넘겨주기
        else if ( num == 1){
            Intent intent = new Intent(getApplicationContext(), AddTableActivity.class);
            intent.putExtra("pos",pos);
            intent.putExtra("num",num);
            while(i<72){
                if(clickitem[i] == true){
                    while(j<count){
                        if(i == situation[j]){
                            intent.putExtra("name",name[j]);
                            intent.putExtra("place",place[j]);
                            intent.putExtra("teacher",teacher[j]);
                            intent.putExtra("color",dbcolor[j]);
                            intent.putExtra("note",note[j]);
                            break;
                        }
                        j++;
                    }
                }
                i++;
            }
            for(int k=0 ; k<num ;k++){
                clickitem[pos[k]] = false;
            }
            startActivityForResult(intent,result); // 값을 받아오는 용으로 시작
        }
    }
    // 시간표 삭제 클릭 메소드
    public void delete_onclick(View view){
        for(int i=0 ; i<72 ; i++){
            // 선택된 해당 아이템의 텍스트를 없에고 어뎁터 갱신
            if(clickitem[i] == true){
                adapter.mWeekTitleIds[i] = "";  // 텍스트 없에기
                adapter.colorarray[i] = 0xFFFFFFFF;
                clickitem[i] = false;   // 다시 클릭 안되게 바꿔줌
                delete(i);     // 디비 삭제
                select();   // 바뀐 디비 정보를 배열들에게 넣어줌
                num =0;     //삭제 후 선택된 개수 초기화
                GridSchedule.setAdapter(adapter);   // 어뎁터 갱신

            }
        }
    }
    // 서브 액티비티로 부터 값을 받아오는 메소드
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == result){
            if(resultCode == RESULT_OK){
                select();   // db에서 값 가져오기
                for(int i = 0 ; i<count ; i++){
                    adapter.mWeekTitleIds[situation[i]] = name[i];
                    adapter.colorarray[situation[i]] = dbcolor[i];
                    clickitem[situation[i]] = false;                // 다시 클릭안되있다고 해주고
                }
                num=0;
                GridSchedule.setAdapter(adapter);   // 어뎁터 갱신

            }
        }
    }
}

class ScheduleAdapter extends BaseAdapter {

    Context mContext;
    int w;
    int h;
    int count = 72; // 뷰의 개수
    // 초기 시간표에 들어가는 글자
    String[] mWeekTitleIds ={
            "","월","화","수","목","금",
            "9~10","","","","","",
            "10~11","","","","","",
            "11~12","","","","","",
            "12~1","","","","","",
            "1~2","","","","","",
            "2~3","","","","","",
            "3~4","","","","","",
            "4~5","","","","","",
            "5~6","","","","","",
            "6~7","","","","","",
            "7~8","","","","","",

    };
    int[] colorarray ={
            0,0,0,0,0,0,
            0,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
            0,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
            0,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
            0,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
            0,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
            0,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
            0,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
            0,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
            0,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
            0,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
            0,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
    };
    // 생성자 함수
    public ScheduleAdapter(Context context, int width, int height){

        mContext = context;
        w = width;
        h = height;
    }
    // 뷰의 개수 리턴
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return count;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }
    // 뷰 설정
    @Override
    public View getView(int position, View oldView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v=null;
        if(oldView == null)
        {
            v = new TextView(mContext);
            //뷰 크기 ( 230, 190)
            v.setLayoutParams(new GridView.LayoutParams(w/7 , h/17));
            ((TextView)v).setGravity(Gravity.CENTER);   // 글자 가운데 정렬
            ((TextView)v).setText(mWeekTitleIds[position]); // 데이터를 뷰에 넣어줌
            ((TextView)v).setTextSize(12); // 글자크기
            ((TextView)v).setTextColor((position < 6 || position%6 == 0) ? 0xFF000000 : 0xFFFFFFFF);     // 흰색

            v.setBackgroundColor(colorarray[position]);  //바탕 색상
        }


        return v;
    }

}







