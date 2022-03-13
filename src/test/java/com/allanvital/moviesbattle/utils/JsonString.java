package com.allanvital.moviesbattle.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonString {

    public static String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
