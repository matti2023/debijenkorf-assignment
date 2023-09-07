package com.debijenkorf.debijenkorfassignment.service;

public interface ImageRemovalChain {
    void handleRequest(String type, String reference);
    void setNext(ImageRemovalChain next);
}
