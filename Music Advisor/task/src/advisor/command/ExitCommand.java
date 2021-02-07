package advisor.command;

public class ExitCommand implements ICommand {

    @Override
    public String exec() {
        return "---GOODBYE!---";
    }

    @Override
    public boolean needAuthorization() {
        return false;
    }
}
