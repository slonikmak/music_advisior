package advisor.command;

public interface IPagedCommand extends ICommand {
    String next();
    String prev();
}
