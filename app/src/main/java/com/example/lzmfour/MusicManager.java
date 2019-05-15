package com.example.lzmfour;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//管理音乐类
public class MusicManager extends Service {

    private MediaPlayer mediaPlayer;
    private Context context;
    //拖动条长度
    private  int seelLength=0;
    //当前曲目位置
    private  int currentIndex=-1;
    //总音乐数
    private int totalmusic;
    //音乐集合
    private List<MusicInfo> musicInfoList=new ArrayList<MusicInfo>();
    private boolean is_circle=false;//是否循环
    private boolean random=false;//是否循环

    public void setIs_circle(boolean is_circle) {
        this.is_circle = is_circle;
    }

    public void setRandom(boolean random) {
        this.random = random;
    }

    public boolean isRandom() {
        return random;
    }

    public boolean isIs_circle() {
        return is_circle;
    }

    //Context:MainActivity传过来的上下文环境
    public MusicManager(Context context) {
        this.context = context;
        //解析音乐列表,刚开始一直不知道为什么totalmusic==0,y奥开启虚拟Cold boot now
        ResolveMusicList();
        //测试
        if(musicInfoList.size()>=1)
        {
            Log.e(null,"musicInfoList有数据");
        }
        else {
            Log.e(null,"musicInfoList没有数据");
        }
        //初始化演凑者
        InitPlayer();
    }

    public void InitPlayer() {
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void ResolveMusicList() {
        //查询条件
        //出现Cannot find local variable 'selection'问题
        String selection= MediaStore.Audio.Media.IS_MUSIC + "!=0";
        //按照显示名称排序
        String sortOrder=MediaStore.MediaColumns.DISPLAY_NAME + "";

        //查询列名
        String[] projection={
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
        };
        //利用ContentResolver得到用ContentProvider接口的开放数据
        ContentResolver contentResolver=context.getContentResolver();
        //返回查询的信息
        Cursor cursor=contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,sortOrder);
        if(cursor!=null)
        {
            //测试，显示返回有数据
            Log.e(null,"查询音乐有数据");
            //得到音乐文件总数,debug返回0
            totalmusic=cursor.getCount();
//            遍历cursor,并将其放入 musicInfoList中
            Log.e(null,"1");
            for (cursor.moveToFirst();cursor.isAfterLast()!=true;cursor.moveToNext())
            {
                Log.e(null,"2");
                MusicInfo musicInfo=new MusicInfo();
                musicInfo.setMusic_title(cursor.getString(0));
                musicInfo.setMusic_artist(cursor.getString(1));
                musicInfo.setMusic_name(cursor.getString(2));
                musicInfo.setMusic_path(cursor.getString(3));
                musicInfo.setMusci_duration(Integer.parseInt(cursor.getString(4)));
                musicInfoList.add(musicInfo);
            }
        }
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public int getTotalmusic() {
        return totalmusic;
    }


    public List<MusicInfo> getMusicInfoList(){
        return  musicInfoList;
    }

    public  void Release()
    {
        mediaPlayer.reset();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public void Pause()
    {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            seelLength=mediaPlayer.getCurrentPosition();
        }
    }

    public void Resume()
    {
        mediaPlayer.seekTo(seelLength);
        mediaPlayer.start();
    }
    //前一首
    public  void PlayPrevious()
    {
        if (random==false && is_circle==false) {
            if (currentIndex == 0) {
                currentIndex = musicInfoList.size() - 1;

            } else {
                currentIndex = currentIndex - 1;
            }
            seelLength = 0;
            if (mediaPlayer.isPlaying()) {
                Play();
            }
        }else if(random==true){
            RandomPlay();
        }else  if (is_circle==true)
        {
            CirclePlay();
        }

    }

    public void Play()
    {
        mediaPlayer.reset();
        Uri path=Uri.parse(musicInfoList.get(currentIndex).getMusic_path());
        try {
            mediaPlayer.setDataSource(String.valueOf(path));
            mediaPlayer.prepare();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        mediaPlayer.seekTo(seelLength);
        mediaPlayer.start();
    }

    void PlayNext()
    {
        if(!random && !is_circle)
        {
        currentIndex=currentIndex+1;
        if(currentIndex>=musicInfoList.size())
        {
            currentIndex=0;
        }
        seelLength=0;
        if (mediaPlayer.isPlaying())
        {
            Play();
        }
        }else if(random && !is_circle) {
            RandomPlay();
        }else  if (!random&&is_circle)
        {
            CirclePlay();
        }
    }
//随机播放
    public void RandomPlay()
    {
            random=true;
            is_circle=false;
        currentIndex=new Random().nextInt(totalmusic);
        seelLength=0;
            Play();
    }
    //循环播放
    public void CirclePlay()
    {
        //有点蠢
        int lastCurrentIndex=currentIndex;
        currentIndex=lastCurrentIndex;
            is_circle = true;
            random=false;
        seelLength=0;
            Play();
    }
//顺序播放
    public  void RowPlay()
    {
        is_circle=false;
        random=false;
        Play();
    }

    boolean isPlaying()
    {
        return mediaPlayer.isPlaying();
    }

    int getDuration()
    {
        return mediaPlayer.getDuration();
    }

    int getCurrentPosition()
    {
        return mediaPlayer.getCurrentPosition();
    }
    void seekTo(int seekLength)
    {
        seelLength=seekLength;
        mediaPlayer.seekTo(seekLength);
    }

    MusicInfo getMusicInfo(int index)
    {
        return musicInfoList.get(index);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mediaPlayer=MediaPlayer.create(this,R.id.music_name);
        this.mediaPlayer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mediaPlayer.stop();
    }
}
