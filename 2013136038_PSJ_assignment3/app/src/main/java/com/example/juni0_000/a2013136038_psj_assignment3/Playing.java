package com.example.juni0_000.a2013136038_psj_assignment3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;

public class Playing extends AppCompatActivity implements View.OnClickListener{
    File files[]  = MainActivity.files;
    //메인 액티비티에서 얻어온 파일 정보

    int position =0;
    int cnt;
    TextView title;

    MusicService mService;//서비스 객체
    boolean mBound = false;//서비스와 바운드 여부

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        cnt =1; //재생, 일시정지 버튼 전환 플래그
        boolean isPlaying = getIntent().getBooleanExtra("isPlaying",true);
        position = getIntent().getIntExtra("playing",0);//호출한 인텐트에서 파일의 순서를 추출

        title = (TextView)findViewById(R.id.title);
        title.setText(files[position].getName().replace(".mp3",""));

        ImageButton bPre = (ImageButton)findViewById(R.id.previous);
        ImageButton bPlay = (ImageButton)findViewById(R.id.play);
        ImageButton bNext = (ImageButton)findViewById(R.id.next);
        bPre.setOnClickListener(this);
        bPlay.setOnClickListener(this);
        bNext.setOnClickListener(this);

        if(isPlaying){
            bPlay.setImageResource(R.drawable.pause);
        }else{
            bPlay.setImageResource(R.drawable.play);
            cnt =2;
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        // 연결할 Service를 위한 Intent 객체 생성
        Intent intent = new Intent(this, MusicService.class);

        // Service에 연결하기 위해 bindService 호출, 생성한 intent 객체와 구현한 ServiceConnection의 객체를 전달
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection(){
        //bindService를 사용하기 위해 서비스와 연결을 모니터링하는 클래스의 객체 생성
        //안드로이드 시스템이 사용하기 위해 정의한다.
        MusicService.MusicBinder binder;
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            binder = (MusicService.MusicBinder)service;
            mService = binder.getService();
            //현재 진행중인 서비스의 객체를 받아옵니다.
            mBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name){
            mBound = false;
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //서비스 종료버튼과 리스트 버튼을 메뉴옵션에서 보이도록함
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playing_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this,MainActivity.class);
        switch(item.getItemId()){
            case R.id.musiclist://음악목록을 보여줍니다.
                startActivity(intent);
                finish();//호출한 액티비티는 종료
                return true;
            case R.id.finish:
                Intent intent_service = new Intent(this,MusicService.class);
                stopService(intent_service);
                //서비스를 종료한다.
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view){
        if(mBound) {
            switch (view.getId()) {
                case R.id.previous:
                    if (position <= 0) {
                        position = files.length - 1;
                        //처음 곡이면 마지막곡으로
                    } else {
                        position--;
                    }
                    title.setText(files[position].getName());
                    mService.setFileData(position);
                    mService.play();
                    break;
                case R.id.play:
                    ImageButton ib = (ImageButton) findViewById(R.id.play);
                    if (cnt == 2) {
                        ib.setImageResource(R.drawable.pause);
                        mService.play();
                        cnt = 1;
                    } else {
                        ib.setImageResource(R.drawable.play);
                        mService.pause();
                        cnt = 2;
                    }
                    break;
                case R.id.next:
                    if (position >= files.length - 1) {
                        position = 0;
                        //마지막 곡이면 처음 곡으로
                    } else {
                        position++;
                    }
                    title.setText(files[position].getName().replace(".mp3",""));
                    mService.setFileData(position);
                    mService.play();
                    break;
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onStop(){
        super.onStop();
        mService.makeNotification(position);
        unbindService(mConnection);
        //종료할때 서비스와의 연결을 해제한다.
    }
}
