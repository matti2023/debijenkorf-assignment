package com.debijenkorf.debijenkorfassignment.exception;

public class WriteToS3Exception extends RuntimeException {

    public WriteToS3Exception(String message, Exception exception) {
        super(message, exception);
    }
}
