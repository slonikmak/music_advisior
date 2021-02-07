package advisor.command;

import advisor.SpotifyClient;
import advisor.model.Page;
import advisor.model.Playlist;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class FeaturedCommand extends AbstractPagedCommand<Playlist>{

    public FeaturedCommand(SpotifyClient client, int limit) {
        super(client, limit);
    }

    @Override
    protected String dataToString(List<Playlist> elements) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(elements.stream().map(p-> p.getName()+"\n"+p.getHref()).collect(Collectors.joining("\n\n")));
        stringBuilder.append("\n\n");
        footer(stringBuilder, getCurrentPage(), getPage().getTotalPages());
        return stringBuilder.toString();
    }

    @Override
    public String exec() {
        try {
            List<Playlist> playlists = client.featuredPlaylists();
            setPage(new Page<>(playlists, limit));
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

}
