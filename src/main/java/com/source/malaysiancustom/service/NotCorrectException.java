package com.source.malaysiancustom.service;

public class NotCorrectException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotCorrectException() {
        super("Question is not correct");
    }
}
