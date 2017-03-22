package com.example.android.popularmovies;

/**
 * Created by kacper on 23.03.17.
 */
public class FavouriteMovie {

    private long id;
    private String originalTitle;

    public FavouriteMovie(long id, String originalTitle) {
        this.id = id;
        this.originalTitle = originalTitle;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FavouriteMovie that = (FavouriteMovie) o;

        if (id != that.id) return false;
        return originalTitle != null ? originalTitle.equals(that.originalTitle) : that.originalTitle == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (originalTitle != null ? originalTitle.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FavouriteMovie{" +
                "id=" + id +
                ", originalTitle='" + originalTitle + '\'' +
                '}';
    }
}
