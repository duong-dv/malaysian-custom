package com.source.malaysiancustom.service;

public class QuestionEmptyException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public QuestionEmptyException() {
        super("Must choose at least 1 question and answer");
    }
}
