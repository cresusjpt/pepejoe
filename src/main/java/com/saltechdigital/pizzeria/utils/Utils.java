package com.saltechdigital.pizzeria.utils;

public class Utils {

    public static double roundDouble(double number, boolean over) {
        if (!over)
            return Math.floor(number);
        else
            return Math.ceil(number);
    }
}
