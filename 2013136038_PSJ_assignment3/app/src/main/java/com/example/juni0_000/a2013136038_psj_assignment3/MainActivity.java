package com.example.juni0_000.a2013136038_psj_assignment3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener {

    File musicDir;

    public final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    static int isGranted;

    static File files[] = {};

    private ListView m_ListView;
    private ArrayAdapter<String> m_Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        isGranted = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (isGranted != PackageManager.PERMISSION_GRANTED) {
            // Manifest.permission.READ_EXTERNAL_STORAGE에 접근할 수 있는지 허가여부를 체크합니다.
            //허가는 PackageManager.PERMISSION_GRANTED
            //불허가는 PackageManager.PERMISSION_DENIED
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Toast.makeText(getApplicationContext(),"권한 설정은 '설정->어플리케이션' 에서할 수 있습니다",Toast.LENGTH_SHORT).show();
                //접근권한이 없을 때, 해당 접근의 허가가 필요한 이유또는 부가 설명을 하는 칸
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                //사용자에게 외부저장소 접근을 허용할지 물어보는 메시지를 보입니다.
            }
        } else {
            // READ_EXTERNAL_STORAGE 권한이 있는 것이므로
            // Public Directory에 접근해서 파일을 읽어온다.
            prepareAccessToMusicDirectory();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //리스트 뷰의 음악이 선택되면 제어를 해주는 액티비와 선택된 음악을 재생하는 서비스를 같이 시작함
        Intent intent_acticity = new Intent(this,Playing.class);
        intent_acticity.putExtra("playing",position);
        Intent intent_service = new Intent(this,MusicService.class);
        intent_service.putExtra("playing",position);
        //각각시작하는 액티비티와 서비스에 재생, 제어할 노래의 위치정보를 함께 보낸다.
        stopService(intent_service);
        startService(intent_service);
        startActivity(intent_acticity);
    }

    public void prepareAccessToMusicDirectory() {
        //저장소에서 파일정보를 불러와 저장하는 메서드
        ArrayList<String> musicList = new ArrayList<>();
        musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        //외부저장소에서 음악을 저장하는 파일의 정보를 가져온다(디렉토리 일수도, 파일일 수도 있다.)
        int num = 0;

        try {
            files = musicDir.listFiles();
            //디렉토리에 있는 파일(디렉토리 포함)들을 나타내는 File 객체들의 배열을 반환한다
            if(files != null) {
                 num=files.length;
                if (num == 0) {
                    Toast.makeText(getApplicationContext(),"재생가능한 음악이 없습니다.",Toast.LENGTH_SHORT).show();
                    //데이터가 없다면 더이상 실행하지 않는다.
                    return;
                } else{
                    for (int i = 0; i < num; i++) {
                        if(files[i].getName().endsWith(".mp3"))
                            musicList.add(files[i].getName().replace(".mp3",""));
                        //확장자명을 제외한 파일이름을 리스트에 저장합니다.
                    }
                    m_Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, musicList);
                    m_ListView = (ListView)findViewById(R.id.list);
                    m_ListView.setAdapter(m_Adapter);
                    m_ListView.setOnItemClickListener(this);
                    // 파일이름이 저장된 리스트를 어댑터에 연결에 리스트뷰를 만듭니다.
                }
            }
        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //파일접근 허가가 이루어진경우
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // READ_EXTERNAL_STORAGE 권한을 얻었으므로
                    // 관련 작업을 수행할 수 있다
                    prepareAccessToMusicDirectory();

                } else {
                    Toast.makeText(getApplicationContext(),"권한 설정은 '설정->어플리케이션' 에서할 수 있습니다",Toast.LENGTH_SHORT).show();
                    // 권한을 얻지 못 하였으므로 파일 읽기를 할 수 없다
                    // 적절히 대처한다
                }
                return;
            }
        }
    }


}
