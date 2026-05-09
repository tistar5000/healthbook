package com.example.medicalapp.exception;

public class SlotUnavailableException extends RuntimeException {
    public SlotUnavailableException(String message) { super(message); }
}