package org.test.utils;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Random;

@Component
public class MathUtil {
    public  Float getRandomFloat(Float a, Float b)
    {
        return new Random().nextFloat(b - a) + a;
    }
    public  Integer getRandomInt(Integer a, Integer b)
    {
        return new Random().nextInt(b + 1 - a) + a;
    }
}
