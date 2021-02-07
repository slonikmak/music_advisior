package advisor.command;

import advisor.SpotifyClient;
import advisor.model.Album;
import advisor.model.Artist;
import advisor.model.Page;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class NewCommand extends AbstractPagedCommand <Album> {

    public NewCommand(SpotifyClient client, int limit) {
        super(client, limit);
    }

    @Override
    public String exec() {
        try {
            List<Album> albums = client.newReleases();
            setPage(new Page<>(albums, limit));
            return dataToString(getPage().getElements(getCurrentPage()));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean needAuthorization() {
        return true;
    }

    @Override
    public String dataToString(List<Album> albums) {
        StringBuilder stringBuilder = new StringBuilder();

        albums.forEach(a-> {
            stringBuilder.append(a.getName()).append("\n");
            stringBuilder.append("[").append(a.getArtists().stream().map(Artist::getName).collect(Collectors.joining(","))).append("]\n");
            stringBuilder.append(a.getHref()).append("\n\n");
        });
        stringBuilder.append("\n");
        footer(stringBuilder, getCurrentPage(), getPage().getTotalPages());
        return stringBuilder.toString();
    }

}
