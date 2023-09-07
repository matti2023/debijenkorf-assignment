package com.debijenkorf.debijenkorfassignment.service;

public interface ImageRetrievalChain {
    byte[] handleRequest(String type, String reference);
    void setNext(ImageRetrievalChain next);
}
