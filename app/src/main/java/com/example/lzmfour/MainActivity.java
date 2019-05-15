package com.example.lzmfour;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.*;

public class MainActivity extends AppCompatActivity {

    private List<MusicInfo> musicInfoList=new ArrayList<MusicInfo>();
    private ListAdapter listAdapter;
    private ListView listView;
    private Service Music;
    private MediaPlayer mediaPlayer;
    private  MusicInfo musicInfo;

    private ImageView btn_Previous;
    private  ImageView brn_next;
    private ImageView btn_play;

    private TextView listTitle;
    private  TextView playingName;
    private SeekBar musicseekBar;

    private  MusicManager musicManager=null;
    private boolean isPlaying=false;

    //长按
   private String Play_Music="播放音乐";
    private String Pause_Music="暂停音乐";

    private Handler handler=new Handler();
    private Runnable updateThread=new Runnable() {
        @Override
        public void run() {
            if (musicManager!=null){
                try
                {
                    if (musicManager.isPlaying())
                    {
                        int Duration=musicManager.getDuration();
                        int currentPos=musicManager.getCurrentPosition();
                        musicseekBar.setMax(Duration);
                        musicseekBar.setProgress(currentPos);
                        int prg_sec=currentPos/1000;
                        int max_sec=Duration/1000;
                        if(prg_sec==max_sec)
                        {
                            musicManager.PlayNext();
                            updateState();
                        }
                    }

                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            handler.post(updateThread);
        }
    };

    private void updateState() {
        //改变曲目位置
        int index=musicManager.getCurrentIndex();
     listAdapter.setFocuseItemPos(musicManager.getCurrentIndex());
        //改变曲目名字和播放，暂停按钮图片
        String currentMusicName=musicInfoList.get(index).getMusic_title();
        playingName.setText(currentMusicName);
        btn_play.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.pause));
        isPlaying=true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listTitle=findViewById(R.id.music_list_title);
        playingName=findViewById(R.id.music_name);
        musicseekBar=findViewById(R.id.seek_bar);
        btn_Previous=findViewById(R.id.btn_previous);
        btn_play=findViewById(R.id.btn_play);
        brn_next=findViewById(R.id.btn_next);

        musicManager=new MusicManager(MainActivity.this);

        musicInfoList=musicManager.getMusicInfoList();
        String title=getResources().getString(R.string.title_string).toString();
        title=title+"(总数："+musicManager.getTotalmusic()+")";
        listTitle.setText(title);

        //将音乐数据和MainActivity上下文环境传入适配器中
        listAdapter=new ListAdapter(musicInfoList, MainActivity.this);
        listView=findViewById(R.id.music_list);
        listView.setAdapter(listAdapter);
        //注册上下文菜单，（长按）
        registerForContextMenu(listView);

        //管理线程
        handler.post(updateThread);

        //listview中的每一个Item对象的长按点击事件 onCreateContextMenu
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                 musicInfo= (MusicInfo) listAdapter.getItem(i);
                 //后面有个改写长按后显示菜单事件
                listView.showContextMenu();
                return true;
            }
        });
        //列表项点击后音乐播放
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               musicManager.Resume();
                musicManager.setCurrentIndex(i);
              listAdapter.setFocuseItemPos(i);
              musicManager.Play();
               updateState();

            }
        });

        //前一首
        btn_Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //重载进度条长度
                musicManager.Resume();
                musicManager.PlayPrevious();
                //更换适配器音乐的字体颜色和图片
                listAdapter.setFocuseItemPos(musicManager.getCurrentIndex());
                //更换播放菜单的图片
                updateState();

            }
        });

        //播放
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying==true)
                {
                    btn_play.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.play));
                    isPlaying=false;
                    musicManager.Pause();
                    return;
                }
                if(isPlaying==false)
                {
                    if (musicManager.getCurrentIndex()==-1)
                    {
                        musicManager.setCurrentIndex(0);
                        listAdapter.setFocuseItemPos(0);
                        musicManager.Play();
                        updateState();
                    }
                    btn_play.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.pause));
                    isPlaying=true;
                    musicManager.Resume();
                }
            }
        });

        brn_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                musicManager.PlayNext();
                listAdapter.setFocuseItemPos(musicManager.getCurrentIndex());
                updateState();
            }
        });
        musicseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(musicManager!=null)
                    try {
                        musicManager.seekTo(seekBar.getProgress());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

            }
        });
    }

    //对菜单选项关于和退出事件监听
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean retValue=super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_item,menu);
        return retValue;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.item_about)
        {
            StringBuilder msgBuilder=new StringBuilder();
            msgBuilder.append("ImageViewer V1.0.0\n");
            msgBuilder.append("作者：8000116240廖泽铭\n");
            msgBuilder.append("南昌大学软件学院168班\n");
            String title="关于";
            new AlertDialog.Builder(MainActivity.this).setIcon(R.drawable.note)
                    .setTitle(title)
                    .setMessage(msgBuilder.toString())
                    .setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).create().show();
        }

            if(item.getItemId()==R.id.item_exit)
            {
                onBackPressed();
             }
            //随机播放
            if(item.getItemId()==R.id.random)
            {
                musicManager.Resume();
                musicManager.RandomPlay();
                listAdapter.setFocuseItemPos(musicManager.getCurrentIndex());
                updateState();

            }
//            单曲循环
            if(item.getItemId()==R.id.circle)
            {
                //单曲循环
                musicManager.CirclePlay();
                //改变适配器中正在播放的的音乐颜色和图标
                listAdapter.setFocuseItemPos(musicManager.getCurrentIndex());
                updateState();
            }
            if(item.getItemId()==R.id.row)
            {
                   //顺序播放
                musicManager.RowPlay();
                //改变适配器中正在播放的的音乐颜色和图标
                listAdapter.setFocuseItemPos(musicManager.getCurrentIndex());
                updateState();
            }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        String title="提示";
        new AlertDialog.Builder(MainActivity.this).setIcon(R.drawable.note)
                .setTitle(title)
                .setMessage("确定退出吗？")
                .setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        musicManager.Release();
                        finish();
                    }
                }).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();
    }

    //重载：改写长按弹出的菜单内容
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0,0,0,"显示详情");
        menu.add(0,1,1,Play_Music);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case 0:
                StringBuilder msgBuilder=new StringBuilder();
                msgBuilder.append("文件名："+musicInfo.getMusic_name()+"\n");
                msgBuilder.append("演唱人："+musicInfo.getMusic_artist()+"\n");
                msgBuilder.append("文件路径："+musicInfo.getMusic_path()+"\n");
                DetailDialog detailDialog=new DetailDialog(this,msgBuilder.toString());
                detailDialog.show();
                break;
            case 1:
                //暂停状态
                if (isPlaying==true)
                {
                    btn_play.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.play));
                    isPlaying=false;
                    musicManager.Pause();
                    //怎么使长按后菜单项改变
                    Play_Music="播放音乐";
                    break;
                }
                //播放状态
                else if(isPlaying==false)
                {
                    if (musicManager.getCurrentIndex()==-1)
                    {
                        musicManager.setCurrentIndex(0);
                        listAdapter.setFocuseItemPos(0);
                        musicManager.Play();
                        updateState();
                    }
                    btn_play.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.pause));
                    isPlaying=true;
                    musicManager.Resume();
                    Play_Music="暂停音乐";
                    break;
                }
            default:
                break;

        }
        return super.onContextItemSelected(item);
    }
}
