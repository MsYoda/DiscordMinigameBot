package org.test.utils;

import java.util.Random;

public class MathUtil {
    public static Float getRandomFloat(Float a, Float b)
    {
        return new Random().nextFloat(b - a) + a;
    }
    public static Integer getRandomInt(Integer a, Integer b)
    {
        return new Random().nextInt(b + 1 - a) + a;
    }
}
