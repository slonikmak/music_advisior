package advisor;

import advisor.command.*;

import java.util.Scanner;

public class Main {

    static boolean authorized = false;

    static String spotifyAuthHost = "https://accounts.spotify.com";
    static String spotifyApiHost = "https://api.spotify.com";
    static int limit = 5;

    static ICommand current;

    public static void main(String[] args) {

        init(args);
        SpotifyClient client = new SpotifyClient(spotifyAuthHost, spotifyApiHost);
        Scanner sc = new Scanner(System.in);

        while (true) {
            String query = sc.nextLine();
            String command = query.split(" ")[0];
            switch (command) {
                case "auth" :
                    run(new AuthCommand(client));
                    break;
                case "exit" :
                    run(new ExitCommand());
                    break;
                case "new" :
                    run(new NewCommand(client, limit));
                    break;
                case "playlists" :
                    run(new PlaylistsCommand(client, limit, query.replace("playlists ", "")));
                    break;
                case "categories" :
                    run(new CategoriesCommand(client, limit));
                    break;
                case "featured" :
                    run(new FeaturedCommand(client, limit));
                    break;
                case "next" :
                    if (current instanceof AbstractPagedCommand) {
                        System.out.println(((AbstractPagedCommand) current).next());
                    }
                    break;
                case "prev" :
                    if (current instanceof AbstractPagedCommand) {
                        System.out.println(((AbstractPagedCommand) current).prev());
                    }
                    break;
                default:
                    System.out.println("Command not found");
            }

        }
    }

    private static void run(ICommand command) {
        current = command;
        if (command.needAuthorization() && authorized || !command.needAuthorization()) {
            System.out.println(command.exec());
            if (command instanceof ExitCommand) exit();
            if (command instanceof AuthCommand) authorize();
        } else {
            System.out.println("Please, provide access for application.");
        }
    }

    private static void init(String[] args) {
        if (args.length > 0 && args[0].equals("-access")) {
            for (int i = 0; i < args.length; i = i+2) {
                if ("-access".equals(args[i])) {
                    spotifyAuthHost = args[i+1];
                } else if ("-resource".equals(args[i])) {
                    spotifyApiHost = args[i+1];
                } else if ("-page".equals(args[i])) {
                    limit = Integer.parseInt(args[i+1]);
                }
            }
        }
    }

    static void authorize(){
        authorized = true;
    }

    static void exit() {
        System.exit(1);
    }
}
