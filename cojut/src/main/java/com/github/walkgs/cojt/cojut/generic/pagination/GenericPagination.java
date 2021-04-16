package com.github.walkgs.cojt.cojut.generic.pagination;

import com.github.walkgs.cojt.cojut.generic.collection.GenericCollection;
import com.github.walkgs.cojt.cojut.generic.collection.GenericList;
import lombok.Getter;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Getter
@ToString
public class GenericPagination<T> implements Pagination<T> {

    private final int limit;
    private final int pageCapacity;
    private final List<GenericCollection.Set<T>> pages = new LinkedList<>();

    @SuppressWarnings("WeakerAccess")
    public GenericPagination(int limit, int pageCapacity) {
        if (pageCapacity <= 0)
            throw new IllegalArgumentException("The maximum quantity of the set must be greater than 0.");
        this.limit = limit;
        this.pageCapacity = pageCapacity;
    }

    public GenericPagination(int pageCapacity) {
        this(0, pageCapacity);
    }

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
