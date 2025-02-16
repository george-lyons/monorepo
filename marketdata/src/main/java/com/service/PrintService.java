package com.service;
public class PrintService {
    private final String message;

    public PrintService(String message) {
        this.message = message;
    }

    public void print() {
        System.out.println(message);
    }

}
