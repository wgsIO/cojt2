package com.github.walkgs.cojt.cojut.generic.pagination;

import com.github.walkgs.cojt.cojut.generic.collection.GenericCollection;
import com.github.walkgs.cojt.cojut.generic.collection.GenericList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Getter
@ToString
@SuppressWarnings("WeakerAccess")
public class GenericPaginationIterator<T> implements Pagination<T>, Pagination.Iterator<T> {

    private final int limit;
    private final int pageCapacity;
    private final boolean auto;
    private final List<GenericCollection.Set<T>> pages = new LinkedList<>();

    @Setter(AccessLevel.NONE)
    protected int currentPage = 0;

    public GenericPaginationIterator(int limit, int pageCapacity, boolean auto) {
        if (pageCapacity <= 0)
            throw new IllegalArgumentException("The maximum quantity of the set must be greater than 0.");
        this.limit = limit;
        this.pageCapacity = pageCapacity;
        this.auto = auto;
    }

    public GenericPaginationIterator(int pageCapacity, boolean auto) {
        this(0, pageCapacity, auto);
    }

    public GenericPaginationIterator(int pageCapacity) {
        this(pageCapacity, false);
    }

    public GenericCollection.Set<T> next() {
        if (isLast())
            throw new IndexOutOfBoundsException();
        ++currentPage;
        if (auto && !hasNext() && (limit == 0 || limit >= pages.size()))
            add();
        return get(currentPage).orElseThrow(NullPointerException::new);
    }

    public Optional<GenericCollection.Set<T>> current() {
        return get(currentPage);
    }

    public GenericCollection.Set<T> previous() {
        if (isFirst())
            throw new IndexOutOfBoundsException();
        --currentPage;
        return get(currentPage).orElseThrow(NullPointerException::new);
    }

    public boolean hasNext() {
        return !isLast();
    }

    public boolean isLast() {
        return currentPage >= pages.size();
    }

    public boolean isFirst() {
        return currentPage == 0;
    }

    @SuppressWarnings("UnusedReturnValue")
    public GenericCollection.List<T> add() {
        if (limit != 0 && pages.size() > limit)
            throw new IndexOutOfBoundsException();
        final GenericCollection.List<T> page = new GenericList<>(pageCapacity);
        pages.add(page);
        return page;
    }

    public Optional<GenericCollection.Set<T>> get(int page) {
        return Optional.ofNullable(pages.get(page));
    }

    public boolean remove(GenericCollection.Set<T> page) {
        return pages.remove(page);
    }

    public GenericCollection.Set<T> remove(int page) {
        return pages.remove(page);
    }

}
