package com.github.walkgs.cojt.cojut.basic.accessor.impl;

import com.github.walkgs.cojt.cojut.Applicable;
import com.github.walkgs.cojt.cojut.basic.accessor.Accessor;
import com.github.walkgs.cojt.cojut.basic.accessor.ClassAccessor;
import com.github.walkgs.cojt.cojut.basic.accessor.MemberAccessor;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;

public class LocalClassAcessor extends ClassAccessor implements Applicable<LocalClassAcessor> {

    public static final LocalClassAcessor LOCAL = new LocalClassAcessor();

    @Override
    public MemberAccessor create(Class<?> clazz) {
        return new MemberAccessorImpl(clazz);
    }

    @Override
    public <A extends Member> Accessor<A> createAccessor(A member) {
        return new AccessorImpl<>(member, ((AccessibleObject) member).isAccessible()).apply($accessor -> {
            System.out.println("Created: " + member.getName());
            if ($accessor.isAccessible())
                return;
            ((AccessibleObject) member).setAccessible(true);
        });
    }

}
