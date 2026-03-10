package com.musicbase.entity;

public class SongSore {
    private String name;
    private String title;
    private String standardScore;

    public SongSore(String name, String title, String standardScore) {
        this.name = name;
        this.title = title;
        this.standardScore = standardScore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStandardScore() {
        return standardScore;
    }

    public void setStandardScore(String standardScore) {
        this.standardScore = standardScore;
    }
}
