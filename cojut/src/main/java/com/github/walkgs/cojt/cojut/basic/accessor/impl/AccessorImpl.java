package com.github.walkgs.cojt.cojut.basic.accessor.impl;

import com.github.walkgs.cojt.cojut.Applicable;
import com.github.walkgs.cojt.cojut.basic.accessor.Accessor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;

@Getter
@AllArgsConstructor
class AccessorImpl<T extends Member> implements Accessor<T>, Applicable<AccessorImpl<T>> {

    @Getter(AccessLevel.NONE)
    protected T element;
    protected boolean accessible;

    @Override
    public void close() {
        ((AccessibleObject) element).setAccessible(accessible);
        this.element = null;
    }

    @Override
    public T get() {
        return element;
    }

}
