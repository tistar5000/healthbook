package com.example.medicalapp.exception;

public class SlotNotFoundException extends RuntimeException {
    public SlotNotFoundException(String message) { super(message); }
}