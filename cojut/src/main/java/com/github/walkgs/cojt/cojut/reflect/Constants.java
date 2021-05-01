package com.github.walkgs.cojt.cojut.reflect;

import java.util.HashMap;
import java.util.Map;

public interface Constants {

    String SYSTEM_LOADER_NAME = "SystemLoader";
    Integer PRIMITIVE_CAPACITY = 7 + 7 / 3;
    Map<Class<?>, Class<?>> PRIMITIVE_MAP = new HashMap<Class<?>, Class<?>>(PRIMITIVE_CAPACITY) {{
        put(char.class, Character.class);
        put(byte.class, Byte.class);
        put(short.class, Short.class);
        put(int.class, Integer.class);
        put(long.class, Long.class);
        put(float.class, Float.class);
        put(double.class, Double.class);
    }};

}
