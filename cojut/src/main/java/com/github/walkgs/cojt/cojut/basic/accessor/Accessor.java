package com.github.walkgs.cojt.cojut.basic.accessor;

import java.io.Closeable;
import java.lang.reflect.Member;

public interface Accessor<T extends Member> extends Closeable {

    T get();

    boolean isAccessible();

}
