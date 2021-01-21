package me.leafs.cf.utils;

public class BitMask {
    public static boolean isAnd(int x, int and) {
        return (x & and) == and;
    }
}
