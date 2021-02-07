package advisor.command;

import advisor.SpotifyClient;
import advisor.model.Page;
import advisor.model.Playlist;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.stream.Collectors;

public class PlaylistsCommand extends AbstractPagedCommand<Playlist> {

    String playlistName;

    public PlaylistsCommand(SpotifyClient client, int limit, String playlistName) {
        super(client, limit);
        this.playlistName = playlistName;
    }

    @Override
    public String exec() {
        try {
            List<Playlist> playlists = client.playlists(playlistName);
            if (playlists.isEmpty()) {
                return "Unknown category name.";
            } else {
                setPage(new Page<>(playlists, limit));
                return dataToString(getPage().getElements(getCurrentPage()));
            }
        } catch (Exception e) {
            JsonObject jo = JsonParser.parseString(e.getMessage()).getAsJsonObject();
            return jo.getAsJsonObject("error").get("message").getAsString();
        }
    }

    @Override
    public boolean needAuthorization() {
        return true;
    }

    @Override
    protected String dataToString(List<Playlist> elements) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(elements.stream().map(p-> p.getName()+"\n"+p.getHref()).collect(Collectors.joining("\n\n")));
        stringBuilder.append("\n\n");
        footer(stringBuilder, getCurrentPage(), getPage().getTotalPages());
        return stringBuilder.toString();
    }
}
