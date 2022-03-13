package com.allanvital.moviesbattle.utils;

import com.allanvital.moviesbattle.utils.exception.MaximumValueTooLowException;
import com.allanvital.moviesbattle.utils.exception.TooManyTriesToFindPairException;
import org.springframework.data.util.Pair;

public class MathUtils {

    public static Pair<Integer, Integer> findTwoDifferentRandomIntegers(long maximumValue) throws MaximumValueTooLowException {
        if (maximumValue < 2) {
            throw new MaximumValueTooLowException("To return a pair of integers, the maximum value parameter must be higher than 2");
        }
        Integer first = 0;
        Integer second = 0;
        int count = 0;
        while (first.equals(second)) {
            if (count > 10000) {
                throw new TooManyTriesToFindPairException("Coudnt find suitable integer pair in sane time");
            }
            first = (int)(Math.random() * maximumValue);
            second = (int)(Math.random() * maximumValue);
            count++;
        }
        return Pair.of(first, second);
    }

}
