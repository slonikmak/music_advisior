package advisor.command;

public interface ICommand {
    String exec();
    boolean needAuthorization();
}
