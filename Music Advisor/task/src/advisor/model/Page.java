package advisor.model;

import java.util.List;
import java.util.stream.Collectors;

public class Page <T> {

    private final int limit;
    private final List<T> elements;
    private final int pages;

    public Page(List<T> elements, int limit) {
        this.elements = elements;
        this.limit = limit;
        pages = elements.size() / limit;
    }

    public int getTotalPages(){
        return pages;
    }

    public List<T> getElements(int page){
        return elements.stream().skip((long) (page - 1) * limit).limit(limit).collect(Collectors.toList());
    }
}
