package com.github.walkgs.cojt.cojut;

import java.text.NumberFormat;
import java.text.ParseException;

public interface NumberQuery {

    static boolean is(String number) {
        return from(number) != null;
    }

    static Number from(String number) {
        try {
            return NumberFormat.getInstance().parse(number);
        } catch (ParseException e) {
            return null;
        }
    }

}
