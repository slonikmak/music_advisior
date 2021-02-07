package advisor.model;

import java.util.List;

public class Album {
    private List<Artist> artists;
    private String name;
    private String href;

    public Album() {
    }

    public Album(List<Artist> artists, String name, String href) {
        this.artists = artists;
        this.name = name;
        this.href = href;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
