package advisor.command;

import advisor.SpotifyClient;
import advisor.model.Category;
import advisor.model.Page;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CategoriesCommand extends AbstractPagedCommand<Category> {


    public CategoriesCommand(SpotifyClient client, int limit) {
        super(client, limit);
    }

    @Override
    protected String dataToString(List<Category> elements) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(elements.stream().map(Category::getName).collect(Collectors.joining("\n")));
        stringBuilder.append("\n");
        footer(stringBuilder, getCurrentPage(), getPage().getTotalPages());
        return stringBuilder.toString();
    }

    @Override
    public String exec() {
        try {
            List<Category> categories = client.categories();
            setPage(new Page<>(categories, limit));
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
