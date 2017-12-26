package com.example.juni0_000.dailyhelper;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddTableActivity extends AppCompatActivity {
    // db에 저장될 값들
    String name = "";   // 강의명
    String place = "";  // 강의실
    String teacher =""; // 교수
    int color = 0;      // 색상
    String note = "";   // 메모

    // 입력텍스트 선언
    EditText editText;      //강의명
    EditText p_editText;    // 장소
    EditText t_editText;    // 교수
    EditText n_editText;    // 메모
    //색상 버튼 선언
    Button button10;
    Button button9;
    Button button8;
    Button button7;
    Button button6;
    Button button5;
    Button button2;

    int position[]; // MainActivity에서 뷰의 position을 받아오기 위한 배열
    int num;    // Main에서 받아온 position의 개수
    int dbpos[];    // db에서의 position 값들을 저장하기 위한 배열
    int count;      // db의 position 개수

    //DB 생성에 필요한 변수
    private MySQLiteOpenHelper helper;
    String dbName = "st_file.db";
    int dbVersion = 1; // 데이터베이스 버전
    private SQLiteDatabase db;
    String tag = "SQLite"; // Log 에 사용할 tag

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_table);
        //입력테스트 선언
        editText = (EditText)findViewById(R.id.editText);
        p_editText = (EditText)findViewById(R.id.editText2);
        t_editText = (EditText)findViewById(R.id.editText3);
        n_editText = (EditText)findViewById(R.id.editText4);
        // 버튼 선언
        button10 = (Button)findViewById(R.id.button10);
        button9 = (Button)findViewById(R.id.button9);
        button8 = (Button)findViewById(R.id.button8);
        button7 = (Button)findViewById(R.id.button7);
        button6 = (Button)findViewById(R.id.button6);
        button5 = (Button)findViewById(R.id.button5);
        button2 = (Button)findViewById(R.id.button2);
        // 메인액티비티로 부터 값을 받아옴
        Intent intent_in = getIntent();
        num = intent_in.getIntExtra("num" ,0);
        position = new int[num];
        position = intent_in.getIntArrayExtra("pos");   // MainActivity에서 위치를 가진 배열 가져와 저장
        // 수정/ 보기 버튼 눌렀을 때 오는 값들
        Intent intent_changed = getIntent();
        editText.setText(intent_changed.getStringExtra("name"));
        p_editText.setText(intent_changed.getStringExtra("place"));
        t_editText.setText(intent_changed.getStringExtra("teacher"));
        n_editText.setText(intent_changed.getStringExtra("note"));
        color = intent_changed.getIntExtra("color",1);

        // 액티비티 종료 후 메인액티비티에서 해야될 기능 때문에 선언
        Intent intent_out = new Intent();
        setResult(RESULT_OK, intent_out);

        // DB 생성 or 오픈
        helper = new MySQLiteOpenHelper(
                this,  // 현재 화면의 제어권자
                dbName,// db 이름
                null,  // 커서팩토리-null : 표준커서가 사용됨
                dbVersion);       // 버전

        try {
            db = helper.getWritableDatabase(); // 읽고 쓸수 있는 DB
        } catch (SQLiteException e) {
            e.printStackTrace();
            Log.e(tag, "데이터베이스를 얻어올 수 없음");
            finish(); // 액티비티 종료
        }
        // 수정할때 색상값을 받아와 PICK이 위치하게 하는 함수
        colorpick();


    }
    // DB 삽입
    void insert (int position) {
        db.execSQL("insert into mytable (position, name, place, teacher, color, note) values("+position+",'"+name+"','"+place+"','"+teacher+"',"+color+",'"+note+"');");
    }
    // DB 삭제
    void delete(int position){
        db.execSQL("delete from mytable where position="+position+";");
    }

    public void onclick(View view){
        name =  editText.getText().toString();      // 강의명 입력값
        place = p_editText.getText().toString();    // 강의실 입력값
        teacher = t_editText.getText().toString();  // 교수 입력값
        note = n_editText.getText().toString();     // 메모 입력값

        if(color != 0xFFFF0000 && color != 0xFFFFCC00 && color != 0xFF00FF00 && color != 0xFF00CCFF && color != 0xFF0000FF && color != 0xFFFF00FF && color != 0xFF000000  ) {
            Toast.makeText(this, "색상을 반드시 선택해주세요", Toast.LENGTH_LONG).show();
        }
        else{
            // DB 삭제 후 추가
            for(int i=0 ; i<num ; i++){
                delete(position[i]);
                insert(position[i]);
            }
            Toast.makeText(this, "저장 완료!", Toast.LENGTH_LONG).show();
            finish();   //액티비티 종료
        }

    }
    // 색상을 고르는 버튼에서 초기 화면 설정
    public void colorpick(){
        if(color == 0xFFFF0000){
            button10.setText("pick");
            button9.setText("");
            button8.setText("");
            button7.setText("");
            button6.setText("");
            button5.setText("");
            button2.setText("");
            button10.setTextColor(0xFFFFFFFF);
            color = 0xFFFF0000;
        }
        else if( color == 0xFFFFCC00){
            button10.setText("");
            button9.setText("pick");
            button8.setText("");
            button7.setText("");
            button6.setText("");
            button5.setText("");
            button2.setText("");
            button9.setTextColor(0xFFFFFFFF);
            color = 0xFFFFCC00;
        }
        else if( color == 0xFF00FF00){
            button10.setText("");
            button9.setText("");
            button8.setText("pick");
            button7.setText("");
            button6.setText("");
            button5.setText("");
            button2.setText("");
            button8.setTextColor(0xFFFFFFFF);
            color = 0xFF00FF00;
        }
        else if( color ==0xFF00CCFF ){
            button10.setText("");
            button9.setText("");
            button8.setText("");
            button7.setText("pick");
            button6.setText("");
            button5.setText("");
            button2.setText("");
            button7.setTextColor(0xFFFFFFFF);
            color = 0xFF00CCFF;
        }
        else if( color == 0xFF0000FF){
            button10.setText("");
            button9.setText("");
            button8.setText("");
            button7.setText("");
            button6.setText("pick");
            button5.setText("");
            button2.setText("");
            button6.setTextColor(0xFFFFFFFF);
            color = 0xFF0000FF;
        }
        else if(color == 0xFFFF00FF){
            button10.setText("");
            button9.setText("");
            button8.setText("");
            button7.setText("");
            button6.setText("");
            button5.setText("pick");
            button2.setText("");
            button5.setTextColor(0xFFFFFFFF);
            color = 0xFFFF00FF;
        }
        else if(color == 0xFF000000){
            button10.setText("");
            button9.setText("");
            button8.setText("");
            button7.setText("");
            button6.setText("");
            button5.setText("");
            button2.setText("pick");
            button2.setTextColor(0xFFFFFFFF);
            color = 0xFF000000;
        }
    }
    // 색상 버튼 클릭시 해당 버튼에 PICK 글자가 나오게 하고 해당 RGB 값을 저장하는 함수
    public void onclick_color(View view){
        int id = view.getId();
        switch (id){
            case R.id.button10 :
                button10.setText("pick");
                button9.setText("");
                button8.setText("");
                button7.setText("");
                button6.setText("");
                button5.setText("");
                button2.setText("");
                button10.setTextColor(0xFFFFFFFF);
                color = 0xFFFF0000;
                break;
            case R.id.button9 :
                button10.setText("");
                button9.setText("pick");
                button8.setText("");
                button7.setText("");
                button6.setText("");
                button5.setText("");
                button2.setText("");
                button9.setTextColor(0xFFFFFFFF);
                color = 0xFFFFCC00;
                break;
            case R.id.button8 :
                button10.setText("");
                button9.setText("");
                button8.setText("pick");
                button7.setText("");
                button6.setText("");
                button5.setText("");
                button2.setText("");
                button8.setTextColor(0xFFFFFFFF);
                color = 0xFF00FF00;
                break;
            case R.id.button7 :
                button10.setText("");
                button9.setText("");
                button8.setText("");
                button7.setText("pick");
                button6.setText("");
                button5.setText("");
                button2.setText("");
                button7.setTextColor(0xFFFFFFFF);
                color = 0xFF00CCFF;
                break;
            case R.id.button6 :
                button10.setText("");
                button9.setText("");
                button8.setText("");
                button7.setText("");
                button6.setText("pick");
                button5.setText("");
                button2.setText("");
                button6.setTextColor(0xFFFFFFFF);
                color = 0xFF0000FF;
                break;
            case R.id.button5 :
                button10.setText("");
                button9.setText("");
                button8.setText("");
                button7.setText("");
                button6.setText("");
                button5.setText("pick");
                button2.setText("");
                button5.setTextColor(0xFFFFFFFF);
                color = 0xFFFF00FF;
                break;
            case R.id.button2 :
                button10.setText("");
                button9.setText("");
                button8.setText("");
                button7.setText("");
                button6.setText("");
                button5.setText("");
                button2.setText("pick");
                button2.setTextColor(0xFFFFFFFF);
                color = 0xFF000000;
                break;

        }
    }
}