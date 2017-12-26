package com.example.juni0_000.a2013136038_psj_assignment3;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;

import java.io.File;

public class MusicService extends Service {

    MediaPlayer mediaPlayer; // 음악파일을 재생하는 클래스 객체
    File files[] = MainActivity.files;
    public class MusicBinder extends Binder{
        MusicService getService(){return MusicService.this;}
    }

    private final IBinder mBinder = new MusicBinder();
    //사용자와 서비스사이의 상호작용을 도와주는 프로그래밍 인터페이스 객체

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)//노티피케이션을 사용하기위해 api버전을 맞춤
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        mediaPlayer = new MediaPlayer();//객체할당
        // onCreate 다음에 실행
        // intent: startService() 호출 시 넘기는 intent 객체
        // flags: service start 요청에 대한 부가 정보. 0, START_FLAG_REDELIVERY, START_FLAG_RETRY
        // startId: start 요청을 나타내는 unique integer id
        int position = intent.getIntExtra("playing",0);
        setFileData(position); //건네받은 순서의 파일 정보를 읽어온다.
        play();//노래를 재생한다.
        return START_REDELIVER_INTENT;
        //서비스가 종료되었다가 다시 시작할때, 마지막으로 건네받은 인텐트를 사용함 intent value를 유지해줌
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)//노티피케이션을 사용하기위해 api버전을 맞춤
    public void setFileData(int position){
        String filepath = files[0].getAbsolutePath();
        filepath = filepath.replace(files[0].getName(),"");
        filepath = filepath+files[position].getName();//재생할 파일의 위치를 가져온다.
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filepath);
            mediaPlayer.prepare();
            //mediaPlater세팅
        } catch (Exception e) {
            e.printStackTrace();
        }
        makeNotification(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void makeNotification(int position){
        //재생하는 파일의 정보를 보여주는 노티피케이션 생성
        Intent intent = new Intent(this, Playing.class);
        intent.putExtra("playing",position);
        intent.putExtra("isPlaying",mediaPlayer.isPlaying());
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Now Playing")
                .setContentText(files[position].getName().replace(".mp3",""))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .build();

        //  foregound service 설정해서 서비스가 강제로 종료되지 않게 한다.
        startForeground(123, noti);
    }
    @Override
    public void onDestroy(){
        //서비스가 종료될 때 연결된 노티피케이션과 자원을 해제한다.
        super.onDestroy();
        mediaPlayer.release();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    public void play(){mediaPlayer.start();}
    public void pause(){
        mediaPlayer.pause();
    }
}
