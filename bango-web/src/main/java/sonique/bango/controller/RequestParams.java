package sonique.bango.controller;

import sonique.bango.domain.sorter.Sorter;

import java.util.List;

public class RequestParams {
    private int page;
    private int start;
    private int limit;
    //TODO: property name should match request parameter name
    private List<Sorter> sort;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<Sorter> getSort() {
        return sort;
    }

    public void setSort(List<Sorter> sort) {
        this.sort = sort;
    }
}
