package com.github.walkgs.cojt.cojys.services.binder.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class Binding<B> {

    private final String name;
    private final B binding;

}
