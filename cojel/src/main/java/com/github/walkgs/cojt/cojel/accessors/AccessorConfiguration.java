package com.github.walkgs.cojt.cojel.accessors;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class AccessorConfiguration {

    @Getter(AccessLevel.NONE)
    private final boolean andSuperClass;
    private final boolean sync;

    public boolean andSuperClass() {
        return andSuperClass;
    }

}
