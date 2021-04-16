package com.github.walkgs.cojt.cojel;

import com.github.walkgs.cojt.cojel.accessors.linked.LinkedAccessor;
import lombok.Getter;

public class Experimental {

    @Getter(lazy = true)
    private static final LinkedAccessor accessor = new LinkedAccessor();

}
