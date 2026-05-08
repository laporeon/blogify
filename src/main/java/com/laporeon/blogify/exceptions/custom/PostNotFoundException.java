package com.laporeon.splog.exceptions.custom;

public class PostNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Post with id %s not found.";

    public PostNotFoundException(String id) {
        super(DEFAULT_MESSAGE.formatted(id));
    }

}