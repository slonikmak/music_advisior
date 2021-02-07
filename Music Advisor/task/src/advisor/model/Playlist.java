package advisor.model;

public class Playlist {
    private String href;
    private String name;

    public Playlist(String href, String name) {
        this.href = href;
        this.name = name;
    }

    public String getHref() {
        return href;
    }

    public String getName() {
        return name;
    }

}
