package com.example.juni0_000.a2013136038_psj_assignment2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileOutputStream;
import java.io.IOException;

public class add_Bookmark extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bookmark);

        final EditText nameText = (EditText)findViewById(R.id.nametext);
        final EditText urlText = (EditText)findViewById(R.id.urltext);


        Button addButton = (Button)findViewById(R.id.addbutton);
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String nText = nameText.getText().toString();
                String uText = urlText.getText().toString();
                if(!uText.startsWith("http")){uText = "http://"+uText;}
                //Uri파싱에서 인터넷 어플을 찾을 수 있게 url설정
                try{
                    FileOutputStream fos1 = openFileOutput(("bm_name.txt"), Context.MODE_APPEND);
                    FileOutputStream fos2 = openFileOutput("bm_url.txt", Context.MODE_APPEND);
                    //기존 파일에 정보 추가
                    fos1.write((nText+",").getBytes());
                    fos2.write((uText+",").getBytes());
                    fos1.close();
                    fos2.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra("INPUT_TEXT", nText+","+uText);
                setResult(RESULT_OK, intent);
                //main activity의 onActivityResult의 data로 이름과 url 전송
                //같은이름으로 파일을 만들어서 거기에 url저장
                finish();
            }
        });
    }

}
