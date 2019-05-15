package com.example.lzmfour;

public class MusicInfo {
    //音乐标题，名字，路径，演唱人，时间
    private String music_title;
    private String music_name;
    private String music_path;
    private String music_artist;
    private int musci_duration;

    public String getMusic_title() {
        return music_title;
    }

    public void setMusic_title(String music_title) {
        this.music_title = music_title;
    }

    public String getMusic_name() {
        return music_name;
    }

    public void setMusic_name(String music_name) {
        this.music_name = music_name;
    }

    public String getMusic_path() {
        return music_path;
    }

    public void setMusic_path(String music_path) {
        this.music_path = music_path;
    }

    public String getMusic_artist() {
        return music_artist;
    }

    public void setMusic_artist(String music_artist) {
        this.music_artist = music_artist;
    }


    public int getMusci_duration() {
        return musci_duration;
    }

    public void setMusci_duration(int musci_duration) {
        this.musci_duration = musci_duration;
    }


}
