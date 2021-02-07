package advisor;

import advisor.model.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SpotifyClient {

    public static final String clientId = "";
    public static final String clientSecret = "";

    private String authHost = "https://accounts.spotify.com";
    private String apiHost = "https://api.spotify.com";
    private String apiHostReal = "https://api.spotify.com/v1";
    private String token;

    public SpotifyClient(String authHost, String apiHost) {
        this.authHost = authHost;
        this.apiHost = apiHost;
    }

    public String token(String code) throws IOException, InterruptedException {
        System.out.println("making http request for access_token...");
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(authHost+"/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString(String.format("grant_type=authorization_code&code=%s&redirect_uri=http://localhost:8080&client_id=%s&client_secret=%s", code, clientId, clientSecret)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        if (validAuthResponse(response)) {
            JsonObject object = JsonParser.parseString(response.body()).getAsJsonObject();
            this.token = object.get("access_token").getAsString();
            return token;
        } else return null;
    }

    public List<Album> newReleases() throws IOException, InterruptedException {
        String body = sendRequest("/v1/browse/new-releases");
        JsonObject jo = JsonParser.parseString(body).getAsJsonObject();
        List<Album> albums = new ArrayList<>();
        for (JsonElement albumObj : jo.getAsJsonObject("albums").getAsJsonArray("items")) {
            Album album = new Album();
            List<Artist> artists = new ArrayList<>();
            album.setArtists(artists);
            for (JsonElement artistObj : albumObj.getAsJsonObject().getAsJsonArray("artists")) {
                JsonObject obj= artistObj.getAsJsonObject();
                artists.add(new Artist(obj.get("name").getAsString()));
            }
            album.setHref(albumObj.getAsJsonObject().get("external_urls").getAsJsonObject().get("spotify").getAsString().replace(apiHostReal, apiHost));
            album.setName(albumObj.getAsJsonObject().get("name").getAsString());
            albums.add(album);
        }
        return albums;
    }
    
    public List<Playlist> playlists(String categoryName) throws IOException, InterruptedException {

        Optional<Category> category = categories().stream().filter(c->c.getName().equals(categoryName)).findFirst();
        if (category.isPresent()) {
            String body = sendRequest(String.format("/v1/browse/categories/%s/playlists", category.get().getId()));

            if (body.contains("error")) {
                throw new SpotifyApiException(body);
            }
            List<Playlist> playlists = new ArrayList<>();
            JsonObject jo = JsonParser.parseString(body).getAsJsonObject();
            for (JsonElement playlistObj : jo.getAsJsonObject("playlists").getAsJsonArray("items")) {
                JsonObject obj= playlistObj.getAsJsonObject();
                String name = obj.get("name").getAsString();
                String href = obj.get("external_urls").getAsJsonObject().get("spotify").getAsString().replace(apiHostReal, apiHost);
                playlists.add(new Playlist(href, name));
            }

            return playlists;
        } else {
            return Collections.emptyList();
        }

    }

    public List<Playlist> featuredPlaylists() throws IOException, InterruptedException {
        String body = sendRequest("/v1/browse/featured-playlists");
        List<Playlist> playlists = new ArrayList<>();
        JsonObject jo = JsonParser.parseString(body).getAsJsonObject();
        for (JsonElement playlistObj : jo.getAsJsonObject("playlists").getAsJsonArray("items")) {
            JsonObject obj= playlistObj.getAsJsonObject();
            String name = obj.get("name").getAsString();
            String href = obj.get("external_urls").getAsJsonObject().get("spotify").getAsString().replace(apiHostReal, apiHost);
            playlists.add(new Playlist(href, name));
        }
        return playlists;
    }

    public List<Category> categories() throws IOException, InterruptedException {
        String body = sendRequest("/v1/browse/categories");
        List<Category> categories = new ArrayList<>();
        JsonObject jo = JsonParser.parseString(body).getAsJsonObject();
        for (JsonElement categoryObj : jo.getAsJsonObject("categories").getAsJsonArray("items")) {
            String name = categoryObj.getAsJsonObject().get("name").getAsString();
            String id = categoryObj.getAsJsonObject().get("id").getAsString();
            categories.add(new Category(id, name));
        }
        return categories;
    }

    private String sendRequest(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .uri(URI.create(apiHost+url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private boolean validAuthResponse(HttpResponse<String> response) {
        return !response.body().contains("Invalid authorization code");
    }

    public String getAuthHost() {
        return authHost;
    }

    public String getApiHost() {
        return apiHost;
    }
}
