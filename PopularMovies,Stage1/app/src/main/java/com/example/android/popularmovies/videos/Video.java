package com.example.android.popularmovies.videos;


public class Video {

    private String key;
    private String name;
    private String site;

    public Video(String key, String name, String site) {
        this.key = key;
        this.name = name;
        this.site = site;
    }

    public Video() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Video video = (Video) o;

        if (key != null ? !key.equals(video.key) : video.key != null) return false;
        if (name != null ? !name.equals(video.name) : video.name != null) return false;
        return site != null ? site.equals(video.site) : video.site == null;

    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (site != null ? site.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Video{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", site='" + site + '\'' +
                '}';
    }
}
