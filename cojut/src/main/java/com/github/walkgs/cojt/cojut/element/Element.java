package com.github.walkgs.cojt.cojut.element;

import com.github.walkgs.cojt.cojut.Applicable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
@SuppressWarnings("WeakerAccess")
public class Element<T> implements Applicable<Element<T>> {

    private final long id;
    private final Charge charge;

    private final String elementName;
    private final T element;

}
