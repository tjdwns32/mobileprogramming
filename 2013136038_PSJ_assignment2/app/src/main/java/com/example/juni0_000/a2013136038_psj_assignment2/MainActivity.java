package com.example.juni0_000.a2013136038_psj_assignment2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity implements ActionMode.Callback, AdapterView.OnItemLongClickListener {
    ActionMode mActionMode;

    final static int GET_STRING = 0;
    static int delPosition;//삭제할 아이템의 순서번호

    String[] nStrings;
    String[] uStrings;
    ArrayList<String> names = new ArrayList<String>();//북마크이름 정보 리스트
    ArrayList<String>urls = new ArrayList<String>();//url 정보 리스트

    private ListView m_ListView;
    private ArrayAdapter<String> m_Adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            //어플리케이션의 내장되어있는 파일을 오픈해서 정보를 불러옵니다.
            //나중에 삽입,삭제가 되도록 정보를 ArrayList객체에 저장합니다
            try {
                FileInputStream fis1 = openFileInput("bm_name.txt");
                byte[] buffer1 = new byte[fis1.available()];
                fis1.read(buffer1);
                String nString = new String(buffer1);
                nStrings = nString.split(",");
                for (int i = 0; i <= nStrings.length-1; i++) {
                    names.add(nStrings[i]);
                }
                FileInputStream fis = openFileInput("bm_url.txt");
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                String uString = new String(buffer);
                uStrings = uString.split(",");
                for (int i = 0; i <= uStrings.length-1; i++) {
                    urls.add(uStrings[i]);
                }
            } catch (IOException e) {
                e.printStackTrace();
        }
        setContentView(R.layout.activity_main);
        //AdapterView 설정
        m_Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        m_ListView = (ListView)findViewById(R.id.list);
        m_ListView.setAdapter(m_Adapter);
        m_ListView.setOnItemClickListener(onClickListItem);
        m_ListView.setOnItemLongClickListener(this);
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if(mActionMode != null){
            return false;
        }
        //액션모드 시작
        mActionMode = this.startSupportActionMode(this);
        delPosition = position;
        return true;
    }

    private AdapterView.OnItemClickListener onClickListItem = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls.get(position)));
                //position 정보를 이용해 인터넷에 연결할 url설정
                if(intent != null){
                    if(intent.resolveActivity(getPackageManager())!=null){startActivity(intent);}
                }//해당 정보와 관련된 어플리케이션이 실행가능하면 실행합니다
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bookmarkmenu,menu);//인플레이터를 이용해 메뉴옵션표시
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        //메뉴 옵션이 하나밖에 없으므로 switch문 대신 if문 하나로 이벤트 처리
        if(item.getItemId() == R.id.menu_add) {
            Intent intent = new Intent(MainActivity.this,add_Bookmark.class);
            startActivityForResult(intent,GET_STRING);
            //호출한 엑티비티에서 결과를 얻어온다.
            return true;
        }else
            return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GET_STRING) {
            if(resultCode == RESULT_OK) {
                String namenurl = data.getStringExtra("INPUT_TEXT");
                //이름과 url 정보 추출
                String[] spString = namenurl.split(",");
                String name = spString[0];
                String url = spString[1];
                m_Adapter.add(name);
                //리스트뷰에 추가
                urls.add(url);
                //url 리스트에 추가
            }
        }
    }
    @Override
    public boolean onCreateActionMode(ActionMode mode,Menu menu){
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.deleteactionmenu,menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode,Menu menu){
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode,MenuItem item){
        if(item.getItemId() == R.id.menu_delete){
            m_Adapter.remove((String)m_ListView.getAdapter().getItem(delPosition));
            //아이템을 어댑터를 통해 삭제한다.
            urls.remove(delPosition);
            //아이템의 url도 목록에서 삭제한다.
            String nString = names.toString();
            String uString = urls.toString();
            if(m_Adapter.getCount()==0){
                deleteFile("bm_name.txt");
                deleteFile("bm_url.txt");
                //삭제하고 남은 아이템의 개수가 0일때, 파일을 삭제한다.
                // 아이템을 추가하거나 onCreate호출시 다시 생긴다.
            }else {
                nString = nString.substring(1, nString.length() - 1) + ",";
                nString = nString.replaceAll(" ", "");
                uString = uString.substring(1, uString.length() - 1) + ",";
                uString = uString.replaceAll(" ", "");
                nStrings = nString.split(",");
                uStrings = uString.split(",");
                //아이템을 삭제하고 남은 아이템,url을 다음 동작에 지장이없도록 다듬는다.
                try {
                    FileOutputStream fos1 = openFileOutput("bm_name.txt", Context.MODE_PRIVATE);
                    FileOutputStream fos2 = openFileOutput("bm_url.txt", Context.MODE_PRIVATE);
                    fos1.write(nString.getBytes());
                    fos2.write(uString.getBytes());
                    fos1.close();
                    fos2.close();
                    //다듬어진 아이템, url을 (바뀐내용) 파일에 다시 저장한다.
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mode.finish();
            return true;
        }else return false;
    }
    @Override
    public void onDestroyActionMode(ActionMode mode){
        mActionMode = null;
    }

}
