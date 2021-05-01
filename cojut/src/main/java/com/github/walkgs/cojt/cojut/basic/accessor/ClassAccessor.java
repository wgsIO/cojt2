package com.github.walkgs.cojt.cojut.basic.accessor;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Member;

public abstract class ClassAccessor {

    @Getter
    @Setter
    private static ClassAccessor provider = null;

    public abstract MemberAccessor create(Class<?> clazz);

    public abstract <A extends Member> Accessor<A> createAccessor(A member);

}
