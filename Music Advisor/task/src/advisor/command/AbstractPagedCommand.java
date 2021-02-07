package advisor.command;

import advisor.SpotifyClient;
import advisor.model.Page;

import java.util.List;

public abstract class AbstractPagedCommand <T> implements IPagedCommand {

    protected SpotifyClient client;
    protected int limit;
    private Page<T> page;
    private int currentPage = 1;

    public AbstractPagedCommand(SpotifyClient client, int limit) {
        this.client = client;
        this.limit = limit;
    }

    @Override
    public String next() {
        if (currentPage == getPage().getTotalPages()) {
            return "No more pages.";
        }
        currentPage++;
        return dataToString(getPage().getElements(currentPage));
    }

    @Override
    public String prev() {
        if (currentPage == 1) {
            return "No more pages.";
        }
        currentPage--;
        return dataToString(getPage().getElements(currentPage));
    }

    protected int getCurrentPage() {
        return currentPage;
    }

    protected void setPage(Page<T> page) {
        this.page = page;
    }

    protected Page<T> getPage() {
        return page;
    }
    protected void footer(StringBuilder stringBuilder, int currentPage, int pages) {
        stringBuilder.append(String.format("---PAGE %s OF %s---", currentPage, pages));
    }
    protected abstract String dataToString(List<T> elements);
}
