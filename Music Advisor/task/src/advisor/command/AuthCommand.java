package advisor.command;

import advisor.SpotifyClient;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class AuthCommand implements ICommand {

    static final String commandName = "auth";
    static final String clientId = "c617b619a4594358bb024bc540743cbd";
    static final String clientSecret = "eecbf7e0857f4223be58cce5d9bdebc4";

    private HttpServer server;

    private volatile boolean codeReceived = false;

    private volatile boolean tokenReceived = false;

    private AtomicReference<String> code = new AtomicReference<>();

    private SpotifyClient spotifyClient;

    public AuthCommand(SpotifyClient spotifyClient) {
        this.spotifyClient = spotifyClient;
    }

    @Override
    public String exec() {

        try {

            startServer();
            System.out.println("use this link to request the access code:");
            System.out.printf("%s/authorize?client_id=%s&redirect_uri=http://localhost:8080&response_type=code%n", spotifyClient.getAuthHost(), clientId);
            processAuth();
            server.stop(1);


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return "Success!";
    }

    private void processAuth() throws IOException, InterruptedException {
        System.out.println("waiting for code...");
        while (!codeReceived) {
            //waiting code
        }
        String token = spotifyClient.token(code.get());
        if (token == null) {
            codeReceived = false;
            processAuth();
        }
    }
    private void startServer() throws IOException {
        server = HttpServer.create();
        server.bind(new InetSocketAddress(8080), 0);

        server.createContext("/",
                exchange -> {
                    String query = exchange.getRequestURI().getQuery();
                    String answer = "Authorization code not found. Try again.";
                    if (query != null) {
                        String[] received = query.split("=");
                        if (received.length > 1) {
                            String receivedCode = received[1];
                            if (receivedCode != null && "code".equals(received[0])) {
                                System.out.println("code received");
                                answer = "Got the code. Return back to your program.";
                                code.set(receivedCode);
                                codeReceived = true;
                            }
                        }
                    }
                    exchange.sendResponseHeaders(200, answer.length());
                    exchange.getResponseBody().write(answer.getBytes());
                    exchange.getResponseBody().close();
                }
        );
        server.start();
    }

    @Override
    public boolean needAuthorization() {
        return false;
    }

    public boolean isCodeReceived() {
        return codeReceived;
    }
}
