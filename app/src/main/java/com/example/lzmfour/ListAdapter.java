package com.example.lzmfour;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

class ViewHolder{
    public ImageView itemIcon;
    public TextView itemMusicName;
    public TextView itemMusicSinger;
    public int defaultTextColor;
    View itemView;

    public TextView getItemMusicName() {
        return itemMusicName;
    }

    public ViewHolder(View itemView) {
        if (itemView==null)
        {
            throw new IllegalArgumentException("itemView can not be null");
        }
        this.itemView=itemView;
        itemIcon=itemView.findViewById(R.id.rand_icon);
        itemMusicName=itemView.findViewById(R.id.item_music_name);
        itemMusicSinger=itemView.findViewById(R.id.item_music_singer);
        defaultTextColor=itemMusicName.getCurrentTextColor();
    }



}

public class ListAdapter extends BaseAdapter {
    private List<MusicInfo> musicInfoList;
    private LayoutInflater layoutInflater;
    private Context context;
    private  int currentPos=-1;
    private  ViewHolder holder=null;

    public ListAdapter(List<MusicInfo> musicInfoList, Context context) {
        this.musicInfoList = musicInfoList;
        this.context = context;
        Log.e(null,"传入适配器音乐列表成功");
        layoutInflater=LayoutInflater.from(context);
    }

    public ViewHolder getHolder() {
        return holder;
    }

    public  void setFocuseItemPos(int pos)
    {
        currentPos=pos;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return musicInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return musicInfoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    public void remove(int i)
    {
        musicInfoList.remove(i);
    }

    public void refreshDataSet()
    {
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
       if(view==null)
       {
           view=layoutInflater.inflate(R.layout.item_layout,null);
           //ViewHolder构造方法，绑定构件
           holder=new ViewHolder(view);
           //setTag 可给控件附加存储任意类型值
           view.setTag(holder);
       }
       else {
           holder=(ViewHolder)view.getTag();
       }
       //判断是否是当前位置，是的话就更变字体颜色和前面的图标
       if(i==currentPos)
       {
           holder.itemIcon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.arrow));
           holder.itemMusicName.setTextColor(Color.RED);
       }else {
           holder.itemIcon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.music));
           holder.itemMusicName.setTextColor(holder.defaultTextColor);
           holder.itemMusicSinger.setTextColor(holder.defaultTextColor);
       }
       holder.itemMusicName.setText(musicInfoList.get(i).getMusic_title());
       holder.itemMusicSinger.setText(musicInfoList.get(i).getMusic_artist());
       //测试
        if(view==null)
        {
            Log.e(null,"适配器返回视图错误");
        }
        if(view!=null)
        {
            Log.e(null,"适配器返回视图成功");
        }
        return view;
    }
}
